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

package com.ericsson.oss.mediation.modeling.ebstools.etcm.validator.api;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding;

public interface EtcmValidator {
    List<Finding> validate(Path etcmFilePath) throws IOException;
}
