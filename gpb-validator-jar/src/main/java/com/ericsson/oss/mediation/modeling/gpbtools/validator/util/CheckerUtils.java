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

package com.ericsson.oss.mediation.modeling.gpbtools.validator.util;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.exceptions.CheckerException;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.impl.ProtoReader;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.model.ProtoSet;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;

/**
 * CheckerUtils.
 */
public final class CheckerUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckerUtils.class);

    private static final String BACKWARDS_COMPATIBILITY_READING_ERROR = "Error while reading protos for backwards compatibility";
    private static final String BACKWARDS_COMPATIBILITY_CHECKING_ERROR = "Error while checking protos for backwards compatibility";

    /**
     * Default constructor.
     */
    private CheckerUtils() {
    }

    /**
     * Backwards Compatibility Check.
     * @param pmEventDirPath the directory where is possible to find the new proto files.
     * @param latestProtofilesPath the directory where to find the old proto files
     * @return results of backwards compatibility validation check
     * @throws FileNotFoundException the exception to be thrown while checking files
     */
    public static GpbValidationResult check(final String pmEventDirPath, final String latestProtofilesPath) throws FileNotFoundException {
        final ProtoReader reader = new ProtoReader();
        final ProtoSet protoSet = new ProtoSet();

        LOGGER.info("Checking backwards compatibility...");
        try {
            reader.readProtos(latestProtofilesPath, protoSet);
        } catch (final CheckerException e) {
            LOGGER.error(BACKWARDS_COMPATIBILITY_READING_ERROR, e);
            throw new CheckerException(BACKWARDS_COMPATIBILITY_READING_ERROR);
        }

        reader.reset();

        try {
            reader.checkProtos(pmEventDirPath, protoSet);
        } catch (final CheckerException e) {
            LOGGER.error(BACKWARDS_COMPATIBILITY_CHECKING_ERROR, e);
            throw new CheckerException(BACKWARDS_COMPATIBILITY_CHECKING_ERROR);
        }
        if (reader.getValidationResult().getValidationIssues().isEmpty()) {
            LOGGER.info("All the files have been checked successfully.");
        } else {
            if (reader.getValidationResult().getValidationIssues().size() > 1) {
                LOGGER.error("{} errors have been found while checking backwards compatibility.",
                        reader.getValidationResult().getValidationIssues().size());
            } else {
                LOGGER.error("1 error has been found while checking backwards compatibility.");
            }
        }
        return reader.getValidationResult();
    }
}
