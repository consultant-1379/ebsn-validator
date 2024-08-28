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

import java.util.ArrayList;
import java.util.List;

/**
 * ProtoVariable.
 */
public class ProtoVariable {
    private boolean repeated;
    private String type;
    private List<String> subTypes;
    private String name;

    public ProtoVariable() {
        subTypes = new ArrayList<>();
    }

    public boolean isRepeated() {
        return repeated;
    }

    public void setRepeated(final boolean repeated) {
        this.repeated = repeated;
    }

    public String getType() {
        return type;
    }

    public String getCompleteType() {
        if ("map".equals(type)) {
            return String.format("map<%s, %s>", subTypes.get(0), subTypes.get(1));
        } else {
            return type;
        }
    }

    public void setType(final String type) {
        if (type.startsWith("map<")) {
            this.type = "map";
            subTypes.add(type.substring(type.indexOf('<') + 1, type.indexOf(',')));
            subTypes.add(type.substring(type.indexOf(',') + 1, type.indexOf('>')));
        } else {
            this.type = type;
        }
    }

    public boolean isMap() {
        return type != null && "map".equals(type);
    }

    public List<String> getSubTypes() {
        return new ArrayList<>(subTypes);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
