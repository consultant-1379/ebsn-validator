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

package com.ericsson.oss.mediation.modeling.ebstools.ftem.validator.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding;
import com.ericsson.oss.mediation.modeling.ebstools.ftem.validator.api.FtemValidator;

@ApplicationScoped
public class FtemValidatorBean implements FtemValidator {

    @Override
    public Optional<Finding> validate(final Path ftemFilePath) throws IOException {
        return new FtemValidatorImpl().validate(ftemFilePath);
    }

}