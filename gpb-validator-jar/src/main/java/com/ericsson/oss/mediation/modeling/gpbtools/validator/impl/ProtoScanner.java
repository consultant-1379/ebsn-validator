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

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * ProtoScanner.
 */
public class ProtoScanner {
    private Scanner scanner;
    private boolean commentMode;
    private String lineReservoir;

    /**
     * Default constructor.
     */
    private ProtoScanner() {
    }

    public ProtoScanner(final File file) throws FileNotFoundException {
        this.scanner = new Scanner(file, StandardCharsets.UTF_8.name());
        lineReservoir = "";
        commentMode = false;
    }

    public String getNextLine() {
        final StringBuilder bld = new StringBuilder();
        if (!lineReservoir.isEmpty()) {
            bld.append(lineReservoir);
            lineReservoir = "";
        }
        while (!canLineBeProcessed(bld.toString())) {
            if (!scanner.hasNextLine()) {
                return null;
            }
            String newLine = scanner.nextLine().trim();
            newLine = getRidOfComments(newLine).trim();
            if (!newLine.isEmpty()) {
                if (!bld.toString().isEmpty()) {
                    bld.append(" ");
                }
                bld.append(newLine);
            }
        }
        final String[] strArray = splitString(bld.toString());
        lineReservoir = strArray[1];
        return strArray[0];
    }

    private boolean canLineBeProcessed(final String line) {
        return line.contains("{") || line.contains("}") || line.contains(";");
    }

    private static String[] splitString(final String line) {
        final String[] strArray = new String[2];
        int splitIndex = line.length();
        int index = line.indexOf('{');
        if (index > -1 && index + 1 < splitIndex) {
            splitIndex = index + 1;
        }
        index = line.indexOf('}');
        if (index > -1 && index + 1 < splitIndex) {
            splitIndex = index + 1;
        }
        index = line.indexOf(';');
        if (index > -1 && index + 1 < splitIndex) {
            splitIndex = index + 1;
        }
        strArray[0] = line.substring(0, splitIndex).trim();
        strArray[1] = line.substring(splitIndex).trim();
        return strArray;
    }

    private String getRidOfComments(final String line) {
        if (commentMode) {
            if (line.contains("*/")) {
                commentMode = false;
                return getRidOfComments(line.substring(line.indexOf("*/") + 2).trim());
            }
            return "";
        } else {
            if (line.contains("/*") && (!line.contains("//") || (line.indexOf("//") > line.indexOf("/*")))) {
                final String line1 = line.substring(0, line.indexOf("/*")).trim();
                commentMode = true;
                return line1 + " " + getRidOfComments(line.substring(line.indexOf("/*") + 2).trim());
            } else if (line.contains("//") && (!line.contains("*/") || (line.indexOf("*/") > line.indexOf("//")))) {
                return line.substring(0, line.indexOf("//")).trim();
            } else {
                return line;
            }
        }
    }

    public void close() {
        scanner.close();
    }
}
