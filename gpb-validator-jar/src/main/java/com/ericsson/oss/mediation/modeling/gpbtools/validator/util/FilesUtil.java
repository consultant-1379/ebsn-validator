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

import static com.ericsson.oss.mediation.modeling.gpbtools.validator.util.CompilerUtils.FILE_SEPARATOR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FilesUtil.
 */
public final class FilesUtil {

    public static final String ZIP = "zip";
    public static final String PROTO = "proto";
    private static final String IO_CREATE_DIR_ERROR = "Error while creating directory";
    private static final String IO_COPY_FILE_ERROR = "Error while creating file";
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesUtil.class);

    /**
     * Default class constructor.
     */
    private FilesUtil() {
    }

    /**
     * Creates a temp directory into a directory.
     *
     * @param prefix prefix for the temp directory to be created
     * @param parentDir the directory where to create the temp directory
     * @return the temp directory created
     * @throws IOException the exception to be thrown while creating the directory
     */
    public static File createTempDir(final String prefix, final File parentDir) throws IOException {
        final File tempDir = File.createTempFile(prefix, "", parentDir);
        Files.delete(tempDir.toPath());
        if (!tempDir.mkdirs()) {
            throw new IOException(IO_CREATE_DIR_ERROR);
        }
        return tempDir;
    }

    /**
     * Creates a new directory into a directory.
     *
     * @param parentDir the directory where to create the new directory
     * @param dirName the name for the directory to be created
     * @return the new directory created
     * @throws IOException the exception to be thrown while creating the directory
     */
    public static File createChildDir(final File parentDir, final String dirName) throws IOException {
        final File childDir = new File(parentDir, dirName);
        if (!childDir.mkdir()) {
            throw new IOException(IO_CREATE_DIR_ERROR);
        }
        return childDir;
    }

    /**
     * Copies an executable file from a source directory to a dest directory.
     *
     * @param srcFilePath the path where the executable file is copied from
     * @param destDir the directory where to copy the executable file to
     * @param fileName the name to be given to the executable file
     * @return the file just copied
     * @throws IOException the exception to be thrown while reading or writing files
     */
    public static File copyExeFileFromResources(final String srcFilePath, final File destDir, final String fileName) throws IOException {
        final File tempFile = new File(destDir, fileName);
        final String resourcePath = FILE_SEPARATOR + srcFilePath;
        try (InputStream is = CompilerUtils.class.getResourceAsStream(resourcePath)) {
            try (FileOutputStream os = new FileOutputStream(tempFile)) {
                final byte[] buf = new byte[4096];
                int length;
                while ((length = is.read(buf)) > 0) {
                    os.write(buf, 0, length);
                }
            }
        }
        if (!tempFile.setExecutable(true)) {
            throw new IOException(IO_COPY_FILE_ERROR);
        }
        return tempFile;
    }

    /**
     * Delete the supplied file. If the file fails to delete, the method will retry {@code numberOfRetries} times.
     *
     * @param directory
     *            the file to delete.
     * @throws IOException the exception to be thrown while deleting directory.
     */
    public static void deleteDirectory(final File directory) {
        // if the file is directory or not
        try {
            if (directory.isDirectory()) {
                final File[] files = directory.listFiles();

                // if the directory contains any file
                if (files != null) {
                    for (final File file : files) {
                        deleteDirectory(file);
                    }
                }
                if (directory.exists()) {
                    Files.delete(directory.toPath());
                }
            } else {
                if (directory.exists()) {
                    Files.delete(directory.toPath());
                }
            }
        } catch (final Exception exception) {
            LOGGER.error("Unable to delete directory : {} with exception message : {}", directory, exception.getMessage());
            LOGGER.debug("Unable to delete directory with exception %s", exception);
        }
    }

    static class LogStreamCopier implements Runnable {
        private InputStream in;
        private boolean isError;
        private Logger logger;

        /**
         * Class constructor.
         *
         * @param in input stream to be read
         * @param isError true if the input stream is to be logged as error, false otherwise
         * @param logger the logger to be used to log messages
         */
        LogStreamCopier(final InputStream in, final boolean isError, final Logger logger) {
            this.in = in;
            this.isError = isError;
            this.logger = logger;
        }

        @Override
        public void run() {
            try {
                logStreamCopy();
            } catch (final IOException var) {
                logger.error("Error while copying", var);
            }
        }

        /**
         * Reads anything from the input stream passed as parameter and logs it as error or either.
         * as debug information depending on the second parameter
         */
        private void logStreamCopy() throws IOException {
            final byte[] buf = new byte[4096];
            int length;
            while ((length = in.read(buf)) > 0) {
                final String str = new String(buf, 0, length, StandardCharsets.UTF_8.name());
                if (isError) {
                    logger.error(str);
                } else {
                    logger.debug(str);
                }
            }
        }
    }
}