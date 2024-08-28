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

package com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings;

public class Finding {
    private final String file;

    private final Type type;

    private final String message;

    private final String element;

    private final Action action;

    public Finding(final String file, final Type type, final String message, final Action action) {
        this.file = file;
        this.type = type;
        this.message = message;
        this.element = null;
        this.action = action;
    }

    public Finding(final String file, final Type type, final String message, final String element, final Action action) {
        this.file = file;
        this.type = type;
        this.message = message;
        this.element = element;
        this.action = action;
    }

    public String getFile() {
        return file;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getElement() {
        return element;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "Finding{"
                + "file='" + file + '\''
                + ", type=" + type
                + ", message='" + message + '\''
                + ", element='" + element + '\''
                + ", action=" + action + '}';
    }
}
