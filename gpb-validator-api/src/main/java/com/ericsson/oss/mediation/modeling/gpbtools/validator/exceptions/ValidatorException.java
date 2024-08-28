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

package com.ericsson.oss.mediation.modeling.gpbtools.validator.exceptions;

/**
 * Validator Exception.
 */
public class ValidatorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Constructor.
     * @param cause
     *            cause
     */
    public ValidatorException(final String cause) {
        super(cause);
    }

}
