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

package com.ericsson.oss.mediation.modeling.gpbtools.validator.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Cause;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.ValidationType;

/**
 * Validation Issue.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationIssue {

    @XmlElement(name = "validation-type")
    private final ValidationType type;
    @XmlElement(name = "validation-element")
    private String element;
    private Cause cause;
    @XmlElement(name = "additional-info")
    private String addInfo;

    /** Constructor for ValidationIssue.
     * @param type
     *            type
     * @param element
     *            element
     * @param cause
     *            cause
     * @param addInfo
     *            add info
     */
    public ValidationIssue(final ValidationType type, final String element, final Cause cause, final String addInfo) {
        this.type = type;
        this.element = element;
        this.cause = cause;
        this.addInfo = addInfo;
    }

    public Cause getCause() {
        return cause;
    }

    /** Covert to simple string.
     *  @return human readable description of the item that failed validation.
     */
    public String toSimpleString() {
        final StringBuilder sb = new StringBuilder(500);
        sb.append(type).append(" - ");
        sb.append(cause.getDescription()).append(" - ");
        sb.append('[').append(element).append(']');
        if (addInfo != null) {
            sb.append(": ").append(addInfo);
        }
        return sb.toString();
    }
}