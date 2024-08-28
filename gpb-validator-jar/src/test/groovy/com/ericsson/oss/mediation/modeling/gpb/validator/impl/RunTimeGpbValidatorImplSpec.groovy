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

package com.ericsson.oss.mediation.modeling.gpb.validator.impl


import java.nio.file.Paths

import org.junit.Rule
import org.junit.rules.TemporaryFolder

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Type
import com.ericsson.oss.mediation.modeling.gpbtools.validator.RunTimeGpbValidatorImpl

class RunTimeGpbValidatorImplSpec extends CdiSpecification {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get("target").toFile());

    @ObjectUnderTest
    private RunTimeGpbValidatorImpl validator

    def 'check valid zip file' () {
        given: 'a valid zip file is ready with valid proto version'
        validator = new RunTimeGpbValidatorImpl()
        def file = "src/test/resources/pm_event_package_cucp_13_47_0.zip"
        when: 'validator is invoked'
        def findingOpt = validator.validate(file)

        then: 'the validator should not return any findings for the valid pmevent file'
        findingOpt.isPresent() == false
    }

    def 'check invalid zip file invalid proto version' () {
        given: 'invalid zip file is ready with invalid proto version'
        validator = new RunTimeGpbValidatorImpl()
        def file = "src/test/resources/pm_event_package_cucp_13_49_0.zip"
        when: 'validator is invoked'
        def findingOpt = validator.validate(file)

        then: 'the validator should return a result for the invalid proto version'
        findingOpt.isPresent() == true
        findingOpt.get().type == Type.GPB001_UNSUPPORTED_PROTO_VERSION
    }


    def 'check valid proto file' () {
        given: 'a valid proto file is ready'
        validator = new RunTimeGpbValidatorImpl()
        def file = "src/test/resources/pm_event_package_cucp_13_47_0.zip"
        when: 'validator is invoked'
        def findingOpt = validator.validate(file);

        then: 'the validator should not return any findings for the valid pmevent file'
        findingOpt.isPresent() == false
    }

    def 'check valid .proto file' () {
        given: 'a valid .proto file is ready'
        validator = new RunTimeGpbValidatorImpl()
        def file = "src/test/resources/pm_event-CUCP-13_156_0.proto"
        when: 'validator is invoked'
        def findingOpt = validator.validate(file);

        then: 'the validator should not return any findings for the valid .proto file'
        findingOpt.isPresent() == false
    }

    def testNoFileException() {
        given:
        validator = new RunTimeGpbValidatorImpl()
        when:
        List<Finding> findings = validator.validate(null);

        then:
        thrown NullPointerException
    }

    List<String> frameFilePathsForPositiveScenario() {
        final String zipFilepath_cucp = "src/test/resources/pm_event_package_cucp_13_47_0.zip"
        final String zipFilepath_cuup = "src/test/resources/pm_event_package_cuup_13_5_0.zip"
        final String zipFilepath_du = "src/test/resources/pm_event_package_du_13_22_0.zip"
        List<String> paths = new ArrayList<>()
        paths.add(zipFilepath_cucp)
        paths.add(zipFilepath_cuup)
        paths.add(zipFilepath_du)

        return paths
    }

    List<String> frameFilePathsForNegativeScenario() {
        final String zipFilepath_cucp = "src/test/resources/pm_event_package_cucp_13_45_0.zip"
        List<String> paths = new ArrayList<>()
        paths.add(zipFilepath_cucp)
        return paths
    }
}