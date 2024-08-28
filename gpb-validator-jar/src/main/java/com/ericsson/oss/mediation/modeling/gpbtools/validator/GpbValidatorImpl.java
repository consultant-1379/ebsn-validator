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

package com.ericsson.oss.mediation.modeling.gpbtools.validator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.api.GpbValidator;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.exceptions.ValidatorException;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.CheckerUtils;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.CompilerUtils;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.FilesUtil;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.UnzipUtils;

/**
 * GpbValidatorImpl.
 */
public class GpbValidatorImpl implements GpbValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(GpbValidatorImpl.class);
    private static final String VALIDATION_FAILED = "Validation failed.";

    /**
     * Zip files containing the new protofiles to be validated.
     */
    private final List<String> zipFilePaths;

    /**
     * Directory path containing old protofiles to be checked for backwards compatibilityFolder containing old protofiles.
     */
    private final String backwardsCompatibilityPath;

    /** Constructor.
     * @param zipFilePaths
     *            zip files path containing new protofiles
     * @param backwardsCompatibilityPath
     *            directory containing old protofiles
     */
    public GpbValidatorImpl(final List<String> zipFilePaths, final String backwardsCompatibilityPath) {
        this.zipFilePaths = new ArrayList<>(zipFilePaths);
        this.backwardsCompatibilityPath = backwardsCompatibilityPath;
    }

    @Override
    public GpbValidationResult validate() throws IOException {
        File pmEventDir = null;
        try {
            // unzip new protofiles
            pmEventDir = UnzipUtils.unzip(zipFilePaths);

            // compile new protofiles to check their syntactic correctness
            CompilerUtils.compile(pmEventDir.getAbsolutePath());

            // parse both new and old protofiles to check backwards compatibility
            return CheckerUtils.check(pmEventDir.getAbsolutePath(), backwardsCompatibilityPath);
        } catch (final Exception e) {
            LOGGER.error(VALIDATION_FAILED, e);
            throw new ValidatorException(VALIDATION_FAILED);
        } finally {
            if (pmEventDir != null) {
                FilesUtil.deleteDirectory(pmEventDir.getParentFile());
            }
        }
    }
}
