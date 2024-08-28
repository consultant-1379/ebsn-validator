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

package com.ericsson.oss.mediation.modeling.ebstools.ftem.validator.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.modeling.schema.util.ModelHandlingUtil;
import com.ericsson.oss.itpf.modeling.schema.util.SchemaConstants;
import com.ericsson.oss.mediation.modeling.ebstools.api.ManagedFunction;
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Action;
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding;
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Type;
import com.ericsson.oss.mediation.modeling.schema.gen.net_ftem.Flex;

public class FtemValidatorImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtemValidatorImpl.class);
    private  final Unmarshaller unmarshaller = ModelHandlingUtil.getUnmarshaller(SchemaConstants.NET_FTEM, true);

    @SuppressWarnings({ "squid:S3725" })
    public Optional<Finding> validate(final Path ftemFilePath) throws IOException {
        LOGGER.info("Validating ftemFile: {}", ftemFilePath);
        try {
            if (!Files.exists(ftemFilePath)) {
                throw new IOException(String.format("FTEM model file %s does not exist", ftemFilePath.getFileName()));
            }
            LOGGER.info("Process ftem model file name: {}", ftemFilePath.getFileName());
            final Flex flex = (Flex) unmarshaller.unmarshal(ftemFilePath.toFile());
            if (!ManagedFunction.contains(flex.getManagedFunction().toUpperCase(Locale.ROOT))) {
                // Remove files
                Files.delete(ftemFilePath);
                return Optional.of(new Finding(ftemFilePath.getFileName().toString(), Type.EBS003_UNSUPPORTED_NETWORK_FUNCTION,
                        String.format("Invalid managed function %s", flex.getManagedFunction()), Action.REMOVED_FILE));
            }
        } catch (final JAXBException exception) {
            LOGGER.warn("Error when validating ftem model file: {}", ftemFilePath.getFileName());
            LOGGER.debug("Exception validation ftem file :", exception);
            Files.delete(ftemFilePath);
            return Optional.of(new Finding(ftemFilePath.getFileName().toString(), Type.EBS001_UNSUPPORTED_XML_FORMAT,
                    exception.getCause().toString(), Action.REMOVED_FILE));
        }
        return Optional.empty();
    }

}

