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

package com.ericsson.oss.mediation.modeling.ebstools.etcm.validator.impl

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Action
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Type

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Unroll

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class EtcmValidatorBeanSpec extends CdiSpecification {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get('target').toFile())

    @ObjectUnderTest
    private EtcmValidatorBean etcmValidator

    private Path junitTempPath

    def setup() {
        junitTempPath = temporaryFolder.getRoot().toPath()
    }

    private def copyFile(Path etcmPath) {
        Files.copy(etcmPath, junitTempPath.resolve(etcmPath.fileName))
    }

    private List<Finding>  validate(Path etcmPath){
        return etcmValidator.validate(junitTempPath.resolve(etcmPath.fileName))
    }

    @Unroll
    def 'check if invalid file is removed - EBS001 - #etcmPath.fileName'(Path etcmPath) {
        given: 'a valid temp input directory is ready'
            copyFile(etcmPath)

        when: 'etcm file is copied and validator invoked'
            Collection<Finding> findings = validate(etcmPath)

        then: 'the validator removed the invalid file and returned the findings along with actions taken'
            findings.size() == 1
            findings.first().getType() == Type.EBS001_UNSUPPORTED_XML_FORMAT
            findings.first().getAction() == Action.REMOVED_FILE

        where: 'an invalid etcm file is supplied'
            etcmPath << Paths.get('src/test/resources/REMOVE-FILES/EBS001').toFile().listFiles().collect({ it.toPath() })
    }

    @Unroll
    def 'check for invalid managed function - EBS003 - #etcmPath.fileName'(Path etcmPath) {
        given: 'a valid temp input directory is ready'
            copyFile(etcmPath)

        when: 'etcm file is copied and validator invoked'
            List<Finding> findings = validate(etcmPath)

        then: 'the validator removed the invalid file and returned the findings along with actions taken'
            findings.size() == 1
            findings.first().getType() == Type.EBS003_UNSUPPORTED_NETWORK_FUNCTION
            findings.first().getAction() == Action.REMOVED_FILE

        where: 'an invalid etcm file is supplied'
            etcmPath << Paths.get('src/test/resources/REMOVE-FILES/EBS003').toFile().listFiles().collect({ it.toPath() })
    }

    @Unroll
    def 'check for invalid counter xml - #etcmPath.fileName'(Path etcmPath) {
        given: 'a valid temp input directory is ready'
            copyFile(etcmPath)

        when: 'etcm file is copied and validator invoked'
            List<Finding> findings = validate(etcmPath)

        then: 'the validator removed the invalid counters from file and returned the findings along with actions taken'
            findings.size() == 1
            findings.first().getType() == Type.EBS002_UNSUPPORTED_SYNTAX_IN_DOCUMENT
            findings.first().getAction() == Action.REMOVED_COUNTER

            !junitTempPath.resolve(etcmPath.fileName).text.contains('<counter>')
            !junitTempPath.resolve(etcmPath.fileName).text.contains('</counter>')
        where: 'an invalid managed function etcm file is supplied'
            etcmPath << Paths.get('src/test/resources/REMOVE-COUNTERS/EBS002').toFile().listFiles().collect({ it.toPath() })
    }

    @Unroll
    def 'check valid etcm - #etcmPath.fileName'(Path etcmPath) {
        given: 'a valid temp input directory is ready and a valid file is copied'
            copyFile(etcmPath)

        when: 'etcm file is copied and validator invoked'
            List<Finding> findings = validate(etcmPath)

        then: 'the validator should not return any findings for the valid etcm file'
            findings.size() == 0
            where: 'a valid etcm file is supplied'
            etcmPath << Paths.get('src/test/resources/VALID-FILES').toFile().listFiles().collect({ it.toPath() })
    }

    def 'check IOException is thrown if the file does not exists'() {
        when: 'validator invoked'
            etcmValidator.validate(junitTempPath.resolve('etcm_cucp_3_7_0_999_2.xml'))

        then: 'IOException is thrown if the file is not found'
            thrown IOException
    }
}