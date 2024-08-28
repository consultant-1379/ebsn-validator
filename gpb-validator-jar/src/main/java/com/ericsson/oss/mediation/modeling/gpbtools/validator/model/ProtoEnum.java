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

import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Cause.*;
import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.ValidationType.BACKWARDS_COMPATIBILITY_CHECKING;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;

/**
 * ProtoEnum.
 */
public class ProtoEnum extends ProtoObject {

    private Map<Short, String> items;

    public ProtoEnum(final ProtoObject parent, final String name) {
        super(parent, name);
        this.items = new HashMap<>();
    }

    @Override
    public String getType() {
        return "ENUM";
    }

    @Override
    public void addItem(final Short key, final String value, final GpbValidationResult validationResult) {
        items.put(key, value);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING, this.getElementDescription(),
                BACK_COMP_004_ITEM_ADDED, getFormattedItem(key, value));
    }

    @Override
    public void checkItem(final Short key, final String value, final GpbValidationResult validationResult) {
        if (isSkipErrors()) {
            return;
        }
        if (!this.items.containsKey(key)) {
            validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                    this.getElementDescription(), BACK_COMP_003_ITEM_NOT_FOUND, getFormattedItem(key, value));
            return;
        }
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_005_ITEM_FOUND, getFormattedItem(key, value));
    }
}
