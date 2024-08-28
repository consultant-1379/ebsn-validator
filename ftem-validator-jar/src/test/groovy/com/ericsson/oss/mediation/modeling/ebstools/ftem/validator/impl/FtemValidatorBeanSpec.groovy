package com.ericsson.oss.mediation.modeling.ebstools.ftem.validator.impl

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Action
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Finding
import com.ericsson.oss.mediation.modeling.ebstools.api.validator.findings.Type

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Unroll

import java.nio.file.Files
import java.nio.file.Paths

class FtemValidatorBeanSpec extends CdiSpecification {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder(Paths.get('target').toFile());

    @ObjectUnderTest
    private FtemValidatorBean validator

    @Unroll
    def 'check if invalid files are removed' () {
        given: 'a valid temp input directory is ready'
            def ftem_folder = 'src/test/resources/invalid_xml';
            def junitTempPath = temporaryFolder.getRoot().toPath()
        when: 'ftem file is copied and validator invoked'
            Files.copy(Paths.get(ftem_folder).resolve(etcmFile), junitTempPath.resolve(etcmFile))
            def findingOpt = validator.validate(junitTempPath.resolve(etcmFile));
        then: 'the validator removed the invalid file and returned the findings along with actions taken'
            findingOpt.isPresent() == true
            findingOpt.get().getType() == Type.EBS001_UNSUPPORTED_XML_FORMAT
            findingOpt.get().getAction() == Action.REMOVED_FILE
        where:
            etcmFile << ['ftem_cucp_60_0.xml', 'ftem_cucp_62_0.xml']
    }

    @Unroll
    def 'check for invalid managed function'  () {
        given: 'a valid temp input directory is ready and an invalid files is copied'
            def ftem_folder = 'src/test/resources/invalid_xml';
            def junitTempPath = temporaryFolder.getRoot().toPath()
            Files.copy(Paths.get(ftem_folder).resolve(etcmFile), junitTempPath.resolve(etcmFile))
        when: 'validator is invoked'
            def finding = validator.validate(junitTempPath.resolve(etcmFile))
        then: 'the validator removed the invalid file and returned the findings along with actions taken'
            finding.isPresent() == true
            finding.get().getType() == Type.EBS003_UNSUPPORTED_NETWORK_FUNCTION
        where:
            etcmFile << ['ftem_cucp_61_0.xml']
    }

    @Unroll
    def 'check valid ftem' () {
        given: 'a valid temp input directory is ready and a valid file is copied'
            def ftem_folder = 'src/test/resources/valid_xml';
            def junitTempPath = temporaryFolder.getRoot().toPath()
            Files.copy(Paths.get(ftem_folder).resolve(etcmFile), junitTempPath.resolve(etcmFile))
        when: 'validator is invoked'
            def finding = validator.validate(junitTempPath.resolve(etcmFile))
        then: 'the validator should not return any findings for the valid ftem file'
            finding.isPresent() == false
        where:
            etcmFile << ['ftem_cucp_59_0.xml']
    }

    def 'check IOException is thrown if the file does not exists' () {
        given: 'a valid temp input directory is ready'
            def junitTempPath = temporaryFolder.getRoot().toPath()
            Set<Finding> findings = new LinkedHashSet<>()
        when: 'validator invoked'
            findings.add(validator.validate(junitTempPath.resolve('ftem_cucp_999_0.xml')))
        then: 'IOException is thrown if the file is not found'
            thrown IOException
            findings.size() == 0
    }
}
