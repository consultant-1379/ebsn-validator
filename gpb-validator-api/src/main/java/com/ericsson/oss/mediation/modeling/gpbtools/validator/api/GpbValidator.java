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

package com.ericsson.oss.mediation.modeling.gpbtools.validator.api;

import java.io.IOException;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;

/**
 * Gpb Proto Validator.
 */
public interface GpbValidator {

    /** Validates proto files.
     * @return validation result
     * @throws IOException the exception to be thrown while validating files
     */
    GpbValidationResult validate() throws IOException;
}
