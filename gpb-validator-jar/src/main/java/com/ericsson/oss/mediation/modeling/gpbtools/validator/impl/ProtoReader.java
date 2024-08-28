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

package com.ericsson.oss.mediation.modeling.gpbtools.validator.impl;

import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Cause.BACK_COMP_002_WRONG_OBJECT_TERMINATION;
import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.ValidationType.BACKWARDS_COMPATIBILITY_CHECKING;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.exceptions.CheckerException;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.model.ProtoObject;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.model.ProtoSet;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;

/**
 * ProtoReader.
 */
public class ProtoReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoReader.class);

    private static final String BACKWARDS_COMPATIBILITY_READING_ERROR = "Error while reading protos for backwards compatibility";

    private List<File> protoFiles;
    private ProtoObject currentObject;
    private GpbValidationResult validationResult;

    /**
     * Default constructor.
     */
    public ProtoReader() {
        protoFiles = new ArrayList<>();
        validationResult = new GpbValidationResult();
    }

    public void reset() {
        currentObject = null;
        protoFiles.clear();
    }

    public GpbValidationResult getValidationResult() {
        return validationResult;
    }

    public void readProtos(final String folderPath, final ProtoSet protoSet) throws FileNotFoundException {
        this.currentObject = protoSet;
        processProtos(folderPath, true);
    }

    public void checkProtos(final String folderPath, final ProtoSet protoSet) throws FileNotFoundException {
        this.currentObject = protoSet;
        processProtos(folderPath, false);
    }

    public void processProtos(final String folderPath, final boolean readMode) throws FileNotFoundException {
        final File folder = new File(folderPath);
        final File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (final File file : listOfFiles) {
                protoFiles.add(file);
                LOGGER.debug(file.getPath());
            }
            parseProtos(readMode);
            if (!currentObject.getType().equals("FILE")) {
                validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING, currentObject.getElementDescription(),
                        BACK_COMP_002_WRONG_OBJECT_TERMINATION, "");
            }
        } else {
            throw new CheckerException(BACKWARDS_COMPATIBILITY_READING_ERROR);
        }
    }

    private void parseProtos(final boolean readMode) throws FileNotFoundException {
        for (final File file : protoFiles) {
            if (readMode) {
                LOGGER.debug("Reading: {}", file.getName());
            } else  {
                LOGGER.debug("Checking: {}", file.getName());
            }
            processFile(file, readMode);
        }
    }

    private void processFile(final File file, final boolean readMode) throws FileNotFoundException {
        final ProtoScanner scanner = new ProtoScanner(file);
        this.currentObject.setName(file.getName());
        String line;
        while ((line = scanner.getNextLine()) != null) {
            currentObject = ProtoParser.parse(line, currentObject, readMode, validationResult);
        }
        scanner.close();
    }
}
