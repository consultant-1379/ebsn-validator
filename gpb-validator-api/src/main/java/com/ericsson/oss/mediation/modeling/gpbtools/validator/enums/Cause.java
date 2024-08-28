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

package com.ericsson.oss.mediation.modeling.gpbtools.validator.enums;

import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Severity.ERROR;
import static com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Severity.NONE;

/**
 * Cause Enum.
 * <p>
 * This enum type represents the issues that may be detected during validation and their severity according to
 * the table available at this page:
 * <a href="https://confluence-oss.seli.wh.rnd.internal.ericsson.com/pages/viewpage.action?pageId=172665847">Model Validation (ECIM, CPP)</a>
 * <p>
 * <b>##### WARNING ######</b>
 * <p>
 * These enum values are also hardcoded in the Release Independence UI.<br>
 * If values are added/modified/deleted, the following UI code MUST be updated accordingly.<br>
 * <p>
 * <b>UI GIT Repo</b>:<br>
 * <i>OSS/com.ericsson.oss.presentation.client/upgrade-independence-service-ui.git</i><br>
 * <b>UI Source File</b>:<br>
 * <i>code upgrade-independence-service-ui-tgz/NodeVersionSupport/locales/en-us/nodeversionsupport/dictionary.json</i><br>
 * @see <a href="https://confluence-oss.seli.wh.rnd.internal.ericsson.com/pages/viewpage.action?pageId=172665847">Model Validation (ECIM, CPP)</a>
 */
public enum Cause {

    // BACKWARDS COMPATIBILITY RELATED
    BACK_COMP_001_UNHANDLED_LINE(1, "Unhandled line", ERROR, ERROR),
    BACK_COMP_002_WRONG_OBJECT_TERMINATION(2, "Wrong object termination", ERROR, ERROR),
    BACK_COMP_003_ITEM_NOT_FOUND(3, "ITEM not found", ERROR, ERROR),
    BACK_COMP_004_ITEM_ADDED(4, "ITEM added", NONE, NONE),
    BACK_COMP_005_ITEM_FOUND(5, "ITEM found", NONE, NONE),
    BACK_COMP_006_ENUM_NOT_FOUND(6, "ENUM not found", ERROR, ERROR),
    BACK_COMP_007_ENUM_ADDED(7, "ENUM added", NONE, NONE),
    BACK_COMP_008_ENUM_FOUND(8, "ENUM found", NONE, NONE),
    BACK_COMP_009_ONEOF_NOT_FOUND(9, "ONEOF not found", ERROR, ERROR),
    BACK_COMP_010_ONEOF_ADDED(10, "ONEOF added", NONE, NONE),
    BACK_COMP_011_ONEOF_FOUND(11, "ONEOF found", NONE, NONE),
    BACK_COMP_012_MESSAGE_NOT_FOUND(12, "MESSAGE not found", ERROR, ERROR),
    BACK_COMP_013_MESSAGE_ADDED(13, "MESSAGE added", NONE, NONE),
    BACK_COMP_014_MESSAGE_FOUND(14, "MESSAGE found", NONE, NONE),
    BACK_COMP_015_VARIABLE_NOT_FOUND(15, "VARIABLE not found", ERROR, ERROR),
    BACK_COMP_016_VARIABLE_NOT_COMPATIBLE(16, "VARIABLE not compatible", ERROR, ERROR),
    BACK_COMP_017_VARIABLE_ADDED(17, "VARIABLE added", NONE, NONE),
    BACK_COMP_018_VARIABLE_FOUND(18, "VARIABLE found", NONE, NONE);

    private int code;
    private String description;
    private Severity designtimeSeverity;
    private Severity runtimeSeverity;

    /**
     * Cause constructor.
     *
     * @param code
     *            code
     * @param description
     *            description
     * @param designtimeSeverity
     *            design time severity
     * @param runtimeSeverity
     *            runtime severity
     */
    Cause(final int code, final String description, final Severity designtimeSeverity, final Severity runtimeSeverity) {
        this.code = code;
        this.description = description;
        this.designtimeSeverity = designtimeSeverity;
        this.runtimeSeverity = runtimeSeverity;
    }

    public String getDescription() {
        return description;
    }

    public Severity getDesigntimeSeverity() {
        return designtimeSeverity;
    }

    public Severity getRuntimeSeverity() {
        return runtimeSeverity;
    }

}
