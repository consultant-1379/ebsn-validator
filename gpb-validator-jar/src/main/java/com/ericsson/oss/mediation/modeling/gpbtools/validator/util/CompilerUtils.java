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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.exceptions.CompilerException;

/**
 * CompilerUtils.
 */
public final class CompilerUtils {

    public static final String FILE_SEPARATOR = "/";

    private static final Logger LOGGER = LoggerFactory.getLogger(CompilerUtils.class);

    private static final String OUTPUT_TEMP_DIR_PREFIX = "protoc_output_";
    private static final String EXE_TEMP_DIR_PREFIX = "protoc_exe_";
    private static final String BIN_DIR = "bin";
    private static final String VERSION = "3.9.2";
    private static final String PROTO_EXTENSION = ".proto";
    private static final String COMPILING_ERROR = "Error while checking syntactical correctness of protofiles";

    /**
     * Default constructor.
     */
    private CompilerUtils() {
    }

    /**
     * Proto File Compilation.
     * @param pmEventDirPath the directory where is possible to find the new proto files.
     * @throws IOException the exception to be thrown while compiling files.
     * @throws InterruptedException the exception to be thrown while compiling files.
     */
    public static void compile(final String pmEventDirPath) throws IOException, InterruptedException {
        File outputDir = null;
        File protocFile = null;
        try {
            LOGGER.info("Compiling new protofiles to check syntactical correctness...");

            outputDir = FilesUtil.createTempDir(OUTPUT_TEMP_DIR_PREFIX, null);
            LOGGER.debug("Temp directory created: {}", outputDir.getAbsolutePath());

            protocFile = getProtoc();

            final File inputDir = new File(pmEventDirPath);
            final File includeDir = inputDir.getParentFile();
            if (inputDir.exists() && inputDir.isDirectory()) {
                final List<File> protoFiles = Arrays.asList(inputDir.listFiles((dir, name) -> name.endsWith(PROTO_EXTENSION)));
                for (final File protoFile : protoFiles) {
                    final List<String> cmd = new ArrayList<>();
                    cmd.add(protocFile.getAbsolutePath());
                    cmd.add("-I" + includeDir.getPath());
                    cmd.add("-I" + protoFile.getParentFile().getAbsolutePath());
                    cmd.add("--java_out=" + outputDir.getPath());
                    cmd.add(protoFile.getPath());
                    final int ret = executeProtoc(cmd);
                    if (ret != 0) {
                        LOGGER.error(COMPILING_ERROR);
                        throw new CompilerException(COMPILING_ERROR);
                    }
                }
                LOGGER.info("All the files have been compiled successfully.");
            } else {
                throw new IOException("Input directory does not exist");
            }
        } finally {
            if (protocFile != null) {
                FilesUtil.deleteDirectory(protocFile.getParentFile().getParentFile());
            }
            if (outputDir != null) {
                FilesUtil.deleteDirectory(outputDir);
            }
        }
    }

    private static File getProtoc() throws IOException, InterruptedException {
        final File protocFile = extractProtoc(null);
        executeProtoc(Arrays.asList(protocFile.getAbsolutePath(), "--version"));
        return protocFile;
    }

    private static File extractProtoc(final File dir) throws IOException {
        final File protocFile;
        final File tmpDir = FilesUtil.createTempDir(EXE_TEMP_DIR_PREFIX, dir);
        LOGGER.debug("Temp directory created: {}", tmpDir.getAbsolutePath());

        final File binDir = FilesUtil.createChildDir(tmpDir, BIN_DIR);
        LOGGER.debug("Directory created: {}", binDir.getAbsolutePath());

        if (System.getProperty("os.name").contains("Windows")) {
            protocFile = FilesUtil.copyExeFileFromResources(BIN_DIR + FILE_SEPARATOR + VERSION
                    + FILE_SEPARATOR + "protoc.exe", binDir, "protoc.exe");
        } else {
            protocFile = FilesUtil.copyExeFileFromResources(BIN_DIR + File.separator
                    + VERSION + File.separator + "protoc_linux", binDir, "protoc.exe");
        }
        LOGGER.debug("ProtoC Compiler file copied: {}", protocFile.getAbsolutePath());
        return protocFile;
    }

    private static int executeProtoc(final List<String> command) throws IOException, InterruptedException {
        Process protoc = null;
        while (protoc == null) {
            LOGGER.debug("Executing: {}", command);
            final ProcessBuilder pb = new ProcessBuilder(command);
            protoc = pb.start();
        }
        (new Thread(new FilesUtil.LogStreamCopier(protoc.getInputStream(), false, LOGGER))).start();
        (new Thread(new FilesUtil.LogStreamCopier(protoc.getErrorStream(), true, LOGGER))).start();
        return protoc.waitFor();
    }
}