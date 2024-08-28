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

import java.text.MessageFormat;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;

/**
 * ProtoObject.
 */
public abstract class ProtoObject {
    protected static final String SYSTEM_ERROR = "System error";

    protected ProtoObject parent;
    protected String name;
    private boolean skipErrors;

    public ProtoObject(final ProtoObject parent, final String name) {
        this.parent = parent;
        this.name = name;
        this.skipErrors = false;
    }

    public void skipErrors() {
        skipErrors = true;
    }

    public boolean isSkipErrors() {
        return skipErrors;
    }

    public ProtoObject getParent() {
        if (parent == null) {
            return this;
        }
        return parent;
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getElementDescription() {
        final StringBuilder bld = new StringBuilder();
        if (parent != null) {
            bld.append(parent.getElementDescription()).append(" / ");
        }
        bld.append(getType()).append(" ").append(getName());
        return bld.toString();
    }

    public void addEnum(final String key, final ProtoEnum value, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public void addOneof(final String key, final ProtoOneof value, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public void addMessage(final String key, final ProtoMessage value, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public void addVariable(final Short key, final ProtoVariable value, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public void addItem(final Short key, final String value, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public ProtoEnum checkEnum(final String key, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public ProtoOneof checkOneof(final String key, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public ProtoMessage checkMessage(final String key, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public void checkVariable(final Short key, final ProtoVariable value, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public void checkItem(final Short key, final String value, final GpbValidationResult validationResult) {
        throw new NullPointerException(SYSTEM_ERROR);
    }

    public ProtoObject handleMessage(final String key, final boolean readMode, final GpbValidationResult validationResult) {
        if (readMode) {
            final ProtoMessage message = new ProtoMessage(this, key);
            this.addMessage(key, message, validationResult);
            return message;
        } else {
            return this.checkMessage(key, validationResult);
        }
    }

    public ProtoObject handleOneof(final String key, final boolean readMode, final GpbValidationResult validationResult) {
        if (readMode) {
            final ProtoOneof oneof = new ProtoOneof(this, key);
            this.addOneof(key, oneof, validationResult);
            return oneof;
        } else {
            return this.checkOneof(key, validationResult);
        }
    }

    public ProtoObject handleEnum(final String key, final boolean readMode, final GpbValidationResult validationResult) {
        if (readMode) {
            final ProtoEnum protoEnum = new ProtoEnum(this, key);
            this.addEnum(key, protoEnum, validationResult);
            return protoEnum;
        } else {
            return this.checkEnum(key, validationResult);
        }
    }

    public ProtoObject handleVariable(final short key, final ProtoVariable variable,
                                      final boolean readMode, final GpbValidationResult validationResult) {
        if (readMode) {
            this.addVariable(key, variable, validationResult);
        } else {
            this.checkVariable(key, variable, validationResult);
        }
        return this;
    }

    public ProtoObject handleItem(final short key, final String value, final boolean readMode,
                                  final GpbValidationResult validationResult) {
        if (readMode) {
            this.addItem(key, value, validationResult);
        } else {
            this.checkItem(key, value, validationResult);
        }
        return this;
    }

    protected static boolean isCompatibleVariable(final ProtoVariable var1, final ProtoVariable var2) {
        if (var1.isMap() && var2.isMap()) {
            return isCompatibleType(var1.getSubTypes().get(0), var2.getSubTypes().get(0)) && isCompatibleType(var1.getSubTypes().get(1),
                    var2.getSubTypes().get(1));
        } else {
            return isCompatibleType(var1.getType(), var2.getType());
        }
    }

    private static boolean isCompatibleType(final String str1, final String str2) {
        return str1.equals(str2) || isRuleInt32(str1, str2) || isRuleSInt32(str1, str2) || isRuleString(str1, str2)
                || isRuleFixed32(str1, str2) || isRuleFixed64(str1, str2);
    }

    private static boolean isRuleInt32(final String str1, final String str2) {
        return ("int32".equals(str1) || "uint32".equals(str1) || "int64".equals(str1) || "uint64".equals(str1))
                && ("int32".equals(str2) || "uint32".equals(str2) || "int64".equals(str2) || "uint64".equals(str2));
    }

    private static boolean isRuleSInt32(final String str1, final String str2) {
        return ("sint32".equals(str1) || "sint64".equals(str1)) && ("sint32".equals(str2) || "sint64".equals(str2));
    }

    private static boolean isRuleString(final String str1, final String str2) {
        return ("string".equals(str1) || "bytes".equals(str1)) && ("string".equals(str2) || "bytes".equals(str2));
    }

    private static boolean isRuleFixed32(final String str1, final String str2) {
        return ("fixed32".equals(str1) || "sfixed32".equals(str1)) && ("fixed32".equals(str2) || "sfixed32".equals(str2));
    }

    private static boolean isRuleFixed64(final String str1, final String str2) {
        return ("fixed64".equals(str1) || "sfixed64".equals(str1)) && ("fixed64".equals(str2) || "sfixed64".equals(str2));
    }

    public static String getFormattedItem(final Short key, final String value) {
        return MessageFormat.format("{0} = {1}", value, key);
    }

    public static String getFormattedVariable(final Short key, final String name, final String type, final boolean repeated) {
        return MessageFormat.format("{0}{1} {2} = {3}", repeated ? "repeated " : "", type, name, key);
    }

    public static String getFormattedVariable(final Short key, final String name, final String type) {
        return getFormattedVariable(key, name, type, false);
    }
}
