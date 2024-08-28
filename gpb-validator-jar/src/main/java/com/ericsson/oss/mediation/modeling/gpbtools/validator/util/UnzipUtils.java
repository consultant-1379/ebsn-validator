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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.exceptions.ValidatorException;

/**
 * UnzipUtils.
 */
public final class UnzipUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnzipUtils.class);

    private static final String TEMP_DIR_PREFIX = "protofiles_";
    private static final String PM_EVENT_DIR = "pm_event";
    private static final String PM_EVENT_PROTO_FILE = "pm_event.proto";

    private static final String ZIP_NAME_FORMAT_ERROR = "A zip file name has a wrong pattern";

    /**
     * Default class constructor.
     */
    private UnzipUtils() {
    }

    /**
     * Unzips the zip files containing the protofiles to a temp directory and returns it.
     *
     * @param zipFiles the zip files containing the protofiles
     * @return the temp directory where the zip files have been unzipped to
     * @throws IOException the exception to be thrown while unzipping files
     */
    public static File unzip(final List<String> zipFiles) throws IOException {
        final File tmpDir = FilesUtil.createTempDir(TEMP_DIR_PREFIX, null);
        LOGGER.info("Temp directory created: {}", tmpDir.getAbsolutePath());

        final File pmEventDir = FilesUtil.createChildDir(tmpDir, PM_EVENT_DIR);
        LOGGER.info("Directory created: {}", pmEventDir.getAbsolutePath());

        for (final String zipFile : zipFiles) {
            LOGGER.info("Unzipping zip files containing new protofiles : {}", zipFile);

            final String pmEventSource = getPmEventSource(zipFile);
            LOGGER.info("Zip file containing the latest common version of pm_event.proto: {}", pmEventSource);

            final boolean pmEventFile = pmEventSource.equals(zipFile);
            LOGGER.info("Boolean value : {}", pmEventFile);
            LOGGER.info("File {} unzipped", zipFile);
            final byte[] buffer = new byte[1024];
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    if (!zipEntry.getName().equalsIgnoreCase(PM_EVENT_PROTO_FILE) || pmEventFile) {
                        final File newFile = createFileFromZipEntry(pmEventDir, zipEntry);
                        try (FileOutputStream fos = new FileOutputStream(newFile)) {
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                        LOGGER.info("File {} unzipped", newFile.getName());
                    }
                    zipEntry = zis.getNextEntry();
                }
                zis.closeEntry();
            }
        }
        LOGGER.info("All the files have been unzipped successfully.");
        return pmEventDir;
    }

    /**
     * Gets the name of the zip file containing the file pm_event.proto with the latest common version.
     * @param zipFiles
     *         zipFiles
     * @return pmEventZipFile containing the latest common version of pm_event proto
     */
    private static String getPmEventSource(final String zipFiles) {
        final int latestCommonVersion = -1;
        String pmEventZipFile = "";
        final String fileName = new File(zipFiles).getName();
        LOGGER.info("getting the version for fileName : {}", fileName);
        final int version = getCommonVersion(fileName);
        if (version >= latestCommonVersion) {
            pmEventZipFile = zipFiles;
        }
        return pmEventZipFile;
    }

    /**
     * Get the common version from a zip filename.
     * @param fileName
     *         fileName
     * @return CommonVersion of proto files
     */
    private static int getCommonVersion(final String fileName) {
        final String[] strArray = fileName.split("_");
        for (final String s : strArray) {
            if (isNumber(s)) {
                return Integer.parseInt(s);
            }
        }
        LOGGER.error(ZIP_NAME_FORMAT_ERROR);
        throw new ValidatorException(ZIP_NAME_FORMAT_ERROR);
    }

    private static boolean isNumber(final String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (final NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Creates a file from a zip entry.
     * @param destinationDir
     *          destinationDir
     * @param zipEntry
     *          zipEntry
     * @return File frem zip entry
     */
    private static File createFileFromZipEntry(final File destinationDir, final ZipEntry zipEntry) {
        return new File(destinationDir, zipEntry.getName());
    }
}
