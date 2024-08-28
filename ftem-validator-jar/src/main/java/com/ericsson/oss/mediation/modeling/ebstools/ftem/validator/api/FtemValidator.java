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

package com.ericsson.oss.mediation.modeling.ebstools.ftem.validator.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding;

/**
 * Perform runtime validation of network models.
 *
 * @return validation status (FAILED, SUCCESS or SUCCESS_WITH_WORKAROUND)
 * @throws IOException
 *             if an error occurs during validation
 */
public interface FtemValidator {

    /**
     * Validates proto files.
     * @param ftemFilePath
     *      *            properties provided to the validator
     * @return validation result
     * @throws IOException the exception to be thrown while validating files
     */
    Optional<Finding> validate(Path ftemFilePath) throws IOException;
}
