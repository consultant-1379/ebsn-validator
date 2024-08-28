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
 * ProtoMessage.
 */
public class ProtoMessage extends ProtoObject {

    private Map<String, ProtoEnum> enums;
    private Map<String, ProtoOneof> oneofs;
    private Map<String, ProtoMessage> messages;
    private Map<Short, ProtoVariable> variables;

    public ProtoMessage(final ProtoObject parent, final String name) {
        super(parent, name);
        this.enums = new HashMap<>();
        this.oneofs = new HashMap<>();
        this.messages = new HashMap<>();
        this.variables = new HashMap<>();
    }

    @Override
    public String getType() {
        return "MESSAGE";
    }

    @Override
    public void addEnum(final String key, final ProtoEnum value, final GpbValidationResult validationResult) {
        this.enums.put(key, value);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_007_ENUM_ADDED, key);
    }

    @Override
    public ProtoEnum checkEnum(final String key, final GpbValidationResult validationResult) {
        if (!this.enums.containsKey(key)) {
            if (!isSkipErrors()) {
                validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                        this.getElementDescription(), BACK_COMP_006_ENUM_NOT_FOUND, key);
            }
            // Adding a placeholder
            final ProtoEnum protoEnum = new ProtoEnum(this, key);
            protoEnum.skipErrors();
            this.enums.put(key, protoEnum);
            return protoEnum;
        }
        final ProtoEnum protoEnum = this.enums.get(key);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_008_ENUM_FOUND, key);
        return protoEnum;
    }

    @Override
    public void addOneof(final String key, final ProtoOneof value, final GpbValidationResult validationResult) {
        this.oneofs.put(key, value);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_010_ONEOF_ADDED, key);
    }

    @Override
    public ProtoOneof checkOneof(final String key, final GpbValidationResult validationResult) {
        if (!this.oneofs.containsKey(key)) {
            if (!isSkipErrors()) {
                validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                        this.getElementDescription(), BACK_COMP_009_ONEOF_NOT_FOUND, key);
            }
            // Adding a placeholder
            final ProtoOneof oneof = new ProtoOneof(this, key);
            oneof.skipErrors();
            this.oneofs.put(key, oneof);
            return oneof;
        }
        final ProtoOneof oneof = this.oneofs.get(key);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_011_ONEOF_FOUND, key);
        return oneof;
    }

    @Override
    public void addMessage(final String key, final ProtoMessage value, final GpbValidationResult validationResult) {
        this.messages.put(key, value);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_013_MESSAGE_ADDED, key);
    }

    @Override
    public ProtoMessage checkMessage(final String key, final GpbValidationResult validationResult) {
        if (!this.messages.containsKey(key)) {
            if (!isSkipErrors()) {
                validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                        this.getElementDescription(), BACK_COMP_012_MESSAGE_NOT_FOUND, key);
            }
            // Adding a placeholder
            final ProtoMessage message = new ProtoMessage(this, key);
            message.skipErrors();
            this.messages.put(key, message);
            return message;
        }
        final ProtoMessage message = this.messages.get(key);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_014_MESSAGE_FOUND, key);
        return message;
    }

    @Override
    public void addVariable(final Short key, final ProtoVariable value, final GpbValidationResult validationResult) {
        this.variables.put(key, value);
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_017_VARIABLE_ADDED,
                getFormattedVariable(key, value.getName(), value.getCompleteType(), value.isRepeated()));
    }

    @Override
    public void checkVariable(final Short key, final ProtoVariable value, final GpbValidationResult validationResult) {
        if (isSkipErrors()) {
            return;
        }
        if (!this.variables.containsKey(key)) {
            validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                    this.getElementDescription(), BACK_COMP_015_VARIABLE_NOT_FOUND,
                    getFormattedVariable(key, value.getName(), value.getCompleteType(), value.isRepeated()));
            return;
        }
        final ProtoVariable variable = this.variables.get(key);
        if (!isCompatibleVariable(value, variable)) {
            validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                    this.getElementDescription(), BACK_COMP_016_VARIABLE_NOT_COMPATIBLE,
                    getFormattedVariable(key, value.getName(), value.getCompleteType(), value.isRepeated()));
            return;
        }
        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING,
                this.getElementDescription(), BACK_COMP_018_VARIABLE_FOUND,
                getFormattedVariable(key, value.getName(), value.getCompleteType(), value.isRepeated()));
    }
}