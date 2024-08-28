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

package com.ericsson.oss.mediation.modeling.ebstools.api;

public enum ManagedFunction {
    CUCP,
    CUUP,
    DU;

    public static boolean contains(final String managedFunctionFromModel) {
        for (final ManagedFunction managedFunction : ManagedFunction.values()) {
            if (managedFunction.toString().equals(managedFunctionFromModel)) {
                return true;
            }
        }
        return false;
    }
}
