/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.mediation.modeling.ebstools.etcm.validator.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.modeling.schema.util.ModelHandlingUtil;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.ebstools.api.ManagedFunction;
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Action;
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding;
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Type;
import com.ericsson.oss.mediation.modeling.schema.gen.net_etcm.Counter;
import com.ericsson.oss.mediation.modeling.schema.gen.net_etcm.Counters;

public class EtcmValidatorImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(EtcmValidatorImpl.class);
    private final Unmarshaller xsdUnmarshaller = ModelHandlingUtil.getUnmarshaller(SchemaConstants.NET_ETCM, true);
    private final Unmarshaller noXsdUnmarshaller = ModelHandlingUtil.getUnmarshaller(SchemaConstants.NET_ETCM, false);
    private Marshaller marshaller;

    private Marshaller getMarshaller() {
        if (marshaller == null) {
            marshaller = ModelHandlingUtil.getMarshaller(SchemaConstants.NET_ETCM, true);
        }
        return marshaller;
    }

    @SuppressWarnings({ "squid:S3725" })
    public List<Finding> validate(final Path etcmFilePath) throws IOException {
        final List<Finding> findings = new ArrayList<>();
        LOGGER.info("Validating etcmFile: {}", etcmFilePath);
        try {
            if (!Files.exists(etcmFilePath)) {
                throw new IOException(String.format("ETCM model file %s does not exist", etcmFilePath.getFileName()));
            }
            LOGGER.info("Process etcm model file name: {}", etcmFilePath.getFileName());
            removeInvalidCountersFromFile(etcmFilePath, findings);
            final Counters counters = Counters.class.cast(xsdUnmarshaller.unmarshal(etcmFilePath.toFile()));
            if (!ManagedFunction.contains(counters.getManagedFunction().toUpperCase(Locale.ROOT))) {
                Files.delete(etcmFilePath);
                findings.add(new Finding(etcmFilePath.getFileName().toString(), Type.EBS003_UNSUPPORTED_NETWORK_FUNCTION,
                        String.format("invalid managed function %s", counters.getManagedFunction()),
                        Action.REMOVED_FILE));
            }
        } catch (final JAXBException | XMLStreamException exception) {
            LOGGER.warn("Error when validating etcm model file: {}", etcmFilePath.getFileName());
            LOGGER.debug("Exception validation etcm file :", exception);
            findings.add(new Finding(etcmFilePath.getFileName().toString(), Type.EBS001_UNSUPPORTED_XML_FORMAT,
                    exception.getCause().toString(), Action.REMOVED_FILE));
            Files.delete(etcmFilePath);
        }
        return findings;
    }

    private void removeInvalidCountersFromFile(final Path etcmFilePath, final List<Finding> findings)
            throws JAXBException, IOException, XMLStreamException {
        final Counters counters = parseAndGetValidCounters(etcmFilePath, findings);
        if (!findings.isEmpty()) {
            LOGGER.info("removing invalid counters from file {}", etcmFilePath.getFileName());
            getMarshaller().setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            getMarshaller().marshal(counters, etcmFilePath.toFile());
        }
    }

    private Counters parseAndGetValidCounters(final Path etcmFilePath, final List<Finding> findings)
            throws JAXBException, IOException, XMLStreamException {
        final Counters jaxbCounters = Counters.class.cast(noXsdUnmarshaller.unmarshal(etcmFilePath.toFile()));
        final XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

        final List<Counter> validCounters = new ArrayList<>();
        int i = -1;
        try (InputStream in = Files.newInputStream(etcmFilePath)) {
            final XMLEventReader xer = factory.createXMLEventReader(in);

            while (xer.hasNext()) {
                if (xer.peek().isStartElement() && xer.peek().asStartElement().getName().getLocalPart().equals("counter")) {
                    try {
                        i++;
                        final Counter counter = Counter.class.cast(xsdUnmarshaller.unmarshal(xer));
                        validCounters.add(counter);
                    } catch (final JAXBException exception) {
                        final Counter jaxbCounter = jaxbCounters.getCounter().get(i);
                        LOGGER.debug("Dropping the counter {} from file {} ", exception, etcmFilePath.getFileName());
                        final Finding finding = new Finding(etcmFilePath.getFileName().toString(), Type.EBS002_UNSUPPORTED_SYNTAX_IN_DOCUMENT,
                                exception.getCause().toString(), getErrorElement(jaxbCounter),
                                Action.REMOVED_COUNTER);

                        findings.add(finding);
                    }
                }
                xer.nextEvent();
            }
        }
        checkForInvalidSessionAggregateCounters(validCounters, etcmFilePath, findings);
        removeInvalidCounters(jaxbCounters, validCounters);
        return jaxbCounters;
    }

    private void checkForInvalidSessionAggregateCounters(final List<Counter> counters, final Path etcmFilePath, final List<Finding> findings) {
        final Iterator<Counter> counterIterator = counters.iterator();
        while (counterIterator.hasNext()) {
            final Counter counter = counterIterator.next();
            if (!EtcmValidatorUtil.isValidSessionAggregator(counter)) {
                counterIterator.remove();
                LOGGER.debug("Dropping the counter {} from file {} ", getErrorElement(counter), etcmFilePath.getFileName());
                final Finding finding = new Finding(etcmFilePath.getFileName().toString(), Type.EBS002_UNSUPPORTED_SYNTAX_IN_DOCUMENT,
                        "Invalid Session Aggregate Counter", getErrorElement(counter),
                        Action.REMOVED_COUNTER);

                findings.add(finding);
            }
        }
    }

    private void removeInvalidCounters(final Counters jaxbCounters, final List<Counter> validCounters) {
        final List<String> validCounterNames = validCounters.stream()
                .map(counter -> counter.getName().getFormat())
                .collect(Collectors.toList());

        final Iterator<Counter> counterIterator = jaxbCounters.getCounter().iterator();
        while (counterIterator.hasNext()) {
            final Counter counter = counterIterator.next();
            if (counter.getName() == null
                    || counter.getName().getFormat() == null
                    || !validCounterNames.contains(counter.getName().getFormat())) {
                counterIterator.remove();
            }
        }
    }

    private String getErrorElement(final Counter jaxbCounter) {
        String element = "";

        if (jaxbCounter.getMo() == null || jaxbCounter.getMo().isEmpty()) {
            element += "EMPTY MO";
        } else {
            element += jaxbCounter.getMo().get(0);
        }

        if (jaxbCounter.getName() == null || jaxbCounter.getName().getFormat() == null) {
            element += "_" + "EMPTY COUNTER";
        } else {
            element += "_" + jaxbCounter.getName().getFormat();
        }

        if (jaxbCounter.getPmeventname() == null) {
            element += "_" + "EMPTY EVENT";
        } else {
            element += "_" + jaxbCounter.getPmeventname();
        }
        return element;
    }
}

