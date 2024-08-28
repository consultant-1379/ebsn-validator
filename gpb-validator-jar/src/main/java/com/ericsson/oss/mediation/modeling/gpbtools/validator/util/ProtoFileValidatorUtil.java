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

package com.ericsson.oss.mediation.modeling.gpbtools.validator.util;

import java.io.*;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProtoFileValidatorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompilerUtils.class);

    private static final String PROTO_EXTENSION = ".proto";

    private static final String PROTO3 = "proto3";

    private ProtoFileValidatorUtil() {}

    private static boolean checkProtoSyntaxVersion(final File protocFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(protocFile),
                "UTF-8"))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.trim().startsWith("syntax=")) {
                    LOGGER.debug("Version of protoc File is : {}", currentLine);
                    if (currentLine.contains(PROTO3)) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    public static boolean validateProtoVersion(final String pmEventDirPath) throws IOException {
        File protocFile = null;
        boolean result = false;
        try {
            LOGGER.info("Checking proto3 version of protofiles ");
            final File inputDir = new File(pmEventDirPath);

            if (inputDir.exists() && inputDir.isDirectory()) {
                final List<File> protoFiles = Arrays.asList(inputDir.listFiles((dir, name) -> name.endsWith(PROTO_EXTENSION)));
                if (!protoFiles.isEmpty()) {
                    protocFile = protoFiles.get(0);
                    result = checkProtoSyntaxVersion(protocFile);
                }
            }
        } finally {
            if (protocFile != null) {
                FilesUtil.deleteDirectory(protocFile.getParentFile().getParentFile());
            }
        }
        LOGGER.info("Proto3 validation successful : {}", result);
        return result;
    }
}