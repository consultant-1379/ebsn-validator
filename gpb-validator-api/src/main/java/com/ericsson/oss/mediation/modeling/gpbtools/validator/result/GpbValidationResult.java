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

import java.util.*;

import javax.xml.bind.annotation.*;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Cause;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.Severity;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.enums.ValidationType;

/**
 * ValidationResult.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class GpbValidationResult {

    @XmlElementWrapper(name = "validation-failed-items")
    @XmlElement(name = "validation-failed-item")
    private final Set<ValidationIssue> validationsFailed = new LinkedHashSet<>();

    /**
     * Gets the validation issues.
     *
     * @return validation issues
     */
    public Collection<ValidationIssue> getValidationIssues() {
        return new HashSet<>(validationsFailed);
    }

    /**
     * Adds a validation issue.
     *
     *@param type
     *            type
     * @param element
     *            element
     * @param cause
     *            cause
     * @param addInfo
     *            additional info
     */
    public void addValidationIssue(final ValidationType type, final String element, final Cause cause, final String addInfo) {
        if (cause.getRuntimeSeverity() != Severity.NONE || cause.getDesigntimeSeverity() != Severity.NONE) {
            validationsFailed.add(new ValidationIssue(type, element, cause, addInfo));
        }
    }
}

