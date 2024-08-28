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

import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Cause.BACK_COMP_001_UNHANDLED_LINE;
import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.ValidationType.BACKWARDS_COMPATIBILITY_CHECKING;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.model.ProtoObject;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.model.ProtoVariable;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;

/**
 * ProtoParser.
 */
public final class ProtoParser {

    /**
     * Default constructor.
     */
    private ProtoParser() {
    }

    /**
     * Parse proto files.
     * @param line
     *         line
     * @param currentObject
     *         currentObject
     * @param readMode
     *         readMode
     * @param validationResult
     *         validationResult
     * @return current object
     */
    public static ProtoObject parse(final String line, final ProtoObject currentObject,
                                    final boolean readMode, final GpbValidationResult validationResult) {
        if (isMessage(line)) {
            // definition of a 'message'
            final String name = line.replace("message ", "").replace("{", "").trim();
            return currentObject.handleMessage(name, readMode, validationResult);
        }
        if (isOneof(line)) {
            // definition of an 'oneof'
            final String name = line.replace("oneof ", "").replace("{", "").trim();
            return currentObject.handleOneof(name, readMode, validationResult);
        }
        if (isEnum(line)) {
            // definition of an 'enum'
            final String name = line.replace("enum ", "").replace("{", "").trim();
            return currentObject.handleEnum(name, readMode, validationResult);
        }
        if (isEnding(line)) {
            // end of a proto object
            return currentObject.getParent();
        }
        if (isToBeSkipped(line)) {
            // do nothing
            return currentObject;
        }
        if (isItemOrVariable(line)) {
            String fragment1 = line.substring(0, line.indexOf('=')).trim();
            final String fragment2 = line.substring(line.indexOf('=') + 1).replaceAll("\\[.*?]", "").replace(";", "").trim();
            final short key = Short.parseShort(fragment2);
            if (!fragment1.contains(" ")) {
                return currentObject.handleItem(key, fragment1, readMode, validationResult);
            }
            final ProtoVariable variable = new ProtoVariable();
            if (isRepeated(fragment1)) {
                variable.setRepeated(true);
                fragment1 = fragment1.replace("repeated", "").trim();
            }
            if (isMap(fragment1)) {
                fragment1 = fragment1.substring(0, fragment1.indexOf('>') + 1).replaceAll("\\s+", "")
                        + fragment1.substring(fragment1.indexOf('>') + 1);
            }
            final String [] strArr = fragment1.split("\\s+");
            if (strArr.length == 2) {
                variable.setType(strArr[0].trim());
                variable.setName(strArr[1].trim());
                return currentObject.handleVariable(key, variable, readMode, validationResult);
            }
        }

        validationResult.addValidationIssue(BACKWARDS_COMPATIBILITY_CHECKING, currentObject.getElementDescription(),
                BACK_COMP_001_UNHANDLED_LINE, line);
        return currentObject;
    }

    private static boolean isMessage(final String line) {
        if (line == null) {
            return false;
        }
        return line.startsWith("message ") && line.endsWith("{");
    }

    private static boolean isEnum(final String line) {
        if (line == null) {
            return false;
        }
        return line.startsWith("enum ") && line.endsWith("{");
    }

    private static boolean isOneof(final String line) {
        if (line == null) {
            return false;
        }
        return line.startsWith("oneof ") && line.endsWith("{");
    }

    private static boolean isEnding(final String line) {
        if (line == null) {
            return false;
        }
        return "}".equals(line);
    }

    private static boolean isToBeSkipped(final String line) {
        if (line == null) {
            return false;
        }
        return line.startsWith("syntax=") || line.startsWith("syntax =") || line.startsWith("package ") || line.startsWith("option ")
                || line.startsWith("import ") || line.startsWith("reserved ") || line.startsWith("extension ");
    }

    private static boolean isItemOrVariable(final String line) {
        if (line == null) {
            return false;
        }
        return line.contains("=") && line.endsWith(";");
    }

    private static boolean isMap(final String fragment) {
        return fragment.startsWith("map ") || fragment.startsWith("map<");
    }

    private static boolean isRepeated(final String fragment) {
        return fragment.startsWith("repeated ");
    }
}
