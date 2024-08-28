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

package com.ericsson.oss.mediation.modeling.gpbtools.validator.model;

import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Cause.BACK_COMP_017_VARIABLE_ADDED;
import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Cause.BACK_COMP_018_VARIABLE_FOUND;
import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.ValidationType.BACKWARDS_COMPATIBILITY_CHECKING;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;

/**
 * ProtoOneof.
 */
public class ProtoOneof extends ProtoObject {

    private Map<Short, ProtoVariable> variables;

    public ProtoOneof(final ProtoObject parent, final String name) {
        super(parent, name);
        this.variables = new HashMap<>();
    }

    @Override
    public String getType() {
        return "ONEOF";
    }

    @Override
    public void addVariable(final Short key, final ProtoVariable value, final GpbValidationResult validationResult) {
        this.variables.put(key, value);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING, this.getElementDescription(),
                BACK_COMP_017_VARIABLE_ADDED, getFormattedVariable(key, value.getName(), value.getCompleteType()));
    }

    @Override
    public void checkVariable(final Short key, final ProtoVariable value, final GpbValidationResult validationResult) {
        if (isSkipErrors()) {
            return;
        }
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING, this.getElementDescription(),
                BACK_COMP_018_VARIABLE_FOUND, getFormattedVariable(key, value.getName(), value.getCompleteType()));
    }
}
