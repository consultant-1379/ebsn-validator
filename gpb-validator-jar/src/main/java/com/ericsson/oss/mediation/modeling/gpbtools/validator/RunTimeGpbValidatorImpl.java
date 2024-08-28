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
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Action;
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding;
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Type;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.api.RunTimeGpbValidator;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.*;

/**
 * RunTimeGpbValidatorImpl.
 */
@ApplicationScoped
public class RunTimeGpbValidatorImpl implements RunTimeGpbValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunTimeGpbValidatorImpl.class);

    @Override
    public Optional<Finding> validate(final String file) throws IOException {
        File pmEventDir = null;
        boolean validateSuccess = false;
        LOGGER.info(" RunTimeGpbValidatorImpl file : {} ", file);
        final String fileName = file.substring(file.indexOf("pm_event"));
        try {
            if (file.endsWith(FilesUtil.ZIP)) {
                final List zipFiles = new ArrayList<>();
                zipFiles.add(file);
                // unzip new protofiles
                pmEventDir = UnzipUtils.unzip(zipFiles);

                // validateProtoVersion new protofiles to check the proto version as proto3
                validateSuccess = ProtoFileValidatorUtil.validateProtoVersion(pmEventDir.getAbsolutePath());
            } else if (file.endsWith(FilesUtil.PROTO)) {
                validateSuccess = true;
            }

            if (!validateSuccess) {
                return Optional.of(new Finding(fileName, Type.GPB001_UNSUPPORTED_PROTO_VERSION,
                        "Unsupported Proto version within PM Event Decoder Model.", Action.REMOVED_FILE));
            }
        } catch (final Exception e) {
            LOGGER.warn("Exception during gpb validation : {}", e.getCause().toString());
            LOGGER.debug("Exception during gpb validation", e);
            return Optional.of(new Finding(fileName, Type.GPB001_UNSUPPORTED_PROTO_VERSION,
                    e.getCause().toString(), Action.REMOVED_FILE));
        } finally {
            if (pmEventDir != null) {
                FilesUtil.deleteDirectory(pmEventDir.getParentFile());
            }
        }
        return Optional.empty();
    }
}