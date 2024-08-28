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
package com.ericsson.oss.mediation.modeling.gpbtools.validator.impl.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.exceptions.CheckerException;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.impl.ProtoParser;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.impl.ProtoScanner;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.CheckerUtils;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.CompilerUtils;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.FilesUtil;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.util.UnzipUtils;
import org.junit.Rule;
import org.junit.Test;

import com.ericsson.oss.mediation.modeling.gpbtools.validator.GpbValidatorImpl;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.exceptions.ValidatorException;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.GpbValidationResult;
import com.ericsson.oss.mediation.modeling.gpbtools.validator.result.ValidationIssue;
import org.junit.rules.ExpectedException;

public class GpbValidatorImplSpec {

    private static final String COMPILING_ERROR = "Error while checking syntactical correctness of protofiles";
    private static final String IO_CREATE_DIR_ERROR = "Error while creating directory";
    private static final String ZIP_NAME_FORMAT_ERROR = "A zip file name has a wrong pattern";

    private static final String BACKWARDS_COMPATIBILITY_CHECKING_ERROR = "Error while checking protos for backwards compatibility";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /*
     * This test case is responsible to check the functionality in the positive scenarios
     */
    @Test
    public void positiveValidateScenario() throws IOException {
        GpbValidatorImpl validator = frameFilePathsForPositiveScenario();
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size() == 0);
    }

    /**
     * This method is responsible to add the file paths to the list
     * @return GpbValidatorImpl
     */
    private GpbValidatorImpl frameFilePathsForPositiveScenario() {
        final String zipFilepath_cucp = "src/test/resources/pm_event_package_cucp_13_43_0.zip";
        final String zipFilepath_cuup = "src/test/resources/pm_event_package_cuup_13_5_0.zip";
        final String zipFilepath_du = "src/test/resources/pm_event_package_du_13_22_0.zip";
        final String backwardpath = "src/test/resources/pm_event";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath_cucp);
        paths.add(zipFilepath_cuup);
        paths.add(zipFilepath_du);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        return validator;
    }

    /*
     * This test case is responsible to check whether the functionality identifies the syntactical correctness of proto files
     */
    @Test(expected = ValidatorException.class)
    public void invalidSyntaxScenario() throws IOException {
        final String zipFilepath = "src/test/resources/pm_event_package_test_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_protofiles1";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size() == 1);
        fail(COMPILING_ERROR);
    }

    /*
     * This test case is responsible to check whether the functionality identifies the availability of the Enum
     */
    @Test
    public void enumAvailabilityCheck() throws IOException {
        final String zipFilepath = "src/test/resources/pm_event_package_test_Enum_not_found_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_protofiles_test_Enum_not_found";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size()==1);
        for (ValidationIssue issue : result.getValidationIssues()) {
            assertEquals ("ENUM not found", issue.getCause ( ).getDescription ( ));
        }
    }

    /*
     * This test case is responsible to check whether the functionality identifies the compatibility of the variables
     */
    @Test
    public void incompatibleVariableCheck() throws IOException {
        final String zipFilepath = "src/test/resources/pm_event_package_test_Incompatible_variable_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_protofiles_test_Incompatible_variable";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size()==1);
        for (ValidationIssue issue : result.getValidationIssues()) {
            assertTrue(issue.getCause().getDescription().equals("VARIABLE not compatible"));
        }
    }

    /*
     * This test case is responsible to check whether the functionality identifies the availability of the Item
     */
    @Test
    public void itemAvailabilityCheck() throws IOException {
        final String zipFilepath = "src/test/resources/pm_event_package_test_item_not_found_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_protofiles_test_Item_not_found";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size()==1);
        for (ValidationIssue issue : result.getValidationIssues()) {
            assertTrue(issue.getCause().getDescription().equals("ITEM not found"));
        }
    }

    /*
     * This test case is responsible to check whether the functionality identifies the availability of the Message
     */
    @Test
    public void messageAvailabilityCheck() throws IOException {
        final String zipFilepath = "src/test/resources/pm_event_package_test_Message_not_found_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_protofiles_test_Message_not_found";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size()==1);
        for (ValidationIssue issue : result.getValidationIssues()) {
            assertTrue(issue.getCause().getDescription().equals("MESSAGE not found"));
        }
    }

    /*
     * This test case is responsible to check whether the functionality identifies the availability of the oneOf
     */
    @Test
    public void oneOfAvailabilityCheck() throws IOException {
        final String zipFilepath = "src/test/resources/pm_event_package_test_Oneof_not_found_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_protofiles_test_Oneof_not_found";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size()==1);
        for (ValidationIssue issue : result.getValidationIssues()) {
            assertTrue(issue.getCause().getDescription().equals("ONEOF not found"));
        }
    }

    /*
     * This test case is responsible to check whether the functionality identifies the availability of the variable
     */
    @Test
    public void variableAvailabilityCheck() throws IOException {
        final String zipFilepath = "src/test/resources/pm_event_package_test_variable_not_found_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_protofiles_test_Variable_not_found";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size()==1);
        for (ValidationIssue issue : result.getValidationIssues()) {
            assertTrue(issue.getCause().getDescription().equals("VARIABLE not found"));
        }
    }

    @Test
    public void negative_UnzipProto_Scenario() throws ValidatorException, IOException {
        exception.expect(ValidatorException.class);
        exception.expectMessage(containsString(ZIP_NAME_FORMAT_ERROR));
        final String zipFilepath_cucp = "src/test/resources/cu_cp_endc_mobility_evaluation.zip";
        UnzipUtils.unzip (Collections.singletonList (zipFilepath_cucp));

    }

    @Test
    public void negativeCompilerInput_dir_not_exist_Scenario() throws IOException, InterruptedException {
        exception.expect(IOException.class);
        exception.expectMessage(containsString("Input directory does not exist"));
        final String zipFilepath_cuup = "src/test/resources/latest_protofiles3/";
        CompilerUtils.compile(zipFilepath_cuup);
    }

    @Test
    public void negativeCompilerInput_Is_not_dir_Scenario() throws IOException, InterruptedException {
        exception.expect(IOException.class);
        exception.expectMessage(containsString("Input directory does not exist"));
        final String zipFilepath_cuup = "src/test/resources/cu_cp_endc_mobility_evaluation.zip";
        CompilerUtils.compile(zipFilepath_cuup);
    }

    @Test
    public void negative_Backward_compatibility_Scenario() throws IOException {
        final String zipFilepath_cuup = "src/test/resources/pm_event_packages_cuup_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_proto";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath_cuup);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size() == 1);
        for (ValidationIssue issue : result.getValidationIssues()) {
            assertEquals ("MESSAGE not found", issue.getCause ( ).getDescription ( ));
        }
    }

    @Test
    public void negative_Backward_compat_size_greater_Scenario() throws IOException {
        final String zipFilepath_cuup = "src/test/resources/pm_event_package_cuup_13_5_0.zip";
        final String backwardpath = "src/test/resources/latest_protofiles1";
        List<String> paths = new ArrayList<>();
        paths.add(zipFilepath_cuup);
        GpbValidatorImpl validator = new GpbValidatorImpl(paths, backwardpath);
        GpbValidationResult result = validator.validate();
        assertTrue(result.getValidationIssues().size() == 23);
    }

    @Test
    public void protos_not_readable_Error_scenario() throws CheckerException, IOException, InterruptedException {
        exception.expect(CheckerException.class);
        exception.expectMessage(containsString(BACKWARDS_COMPATIBILITY_CHECKING_ERROR));
        final String zipFilepath_cuup = "src/test/resources/latest_protofiles3";
        final String backwardpath = "src/test/resources/latest_protofiles2";
        CheckerUtils.check(zipFilepath_cuup, backwardpath );
    }

    @Test
    public void protos_unable_to_check_Error_scenario() throws CheckerException, FileNotFoundException {
        exception.expect(CheckerException.class);
        exception.expectMessage(containsString(BACKWARDS_COMPATIBILITY_CHECKING_ERROR));
        final String zipFilepath_cuup = "src/test/resources/latest_protofiles3";
        final String backwardpath = "src/test/resources/latest_protofiles2";
        CheckerUtils.check(zipFilepath_cuup, backwardpath );
    }

    @Test
    public void dir_creation_error_Scenario() throws IOException {
        exception.expect(IOException.class);
        exception.expectMessage(containsString(IO_CREATE_DIR_ERROR));
        final File parentDir = new File("src/test/resources/");
        final String dirName = "src/test/resources/protofiles3";
        FilesUtil.createChildDir(parentDir, dirName );
    }

    @Test()
    public void compilerUtilsPrivateConstructor() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<CompilerUtils> constructor = CompilerUtils.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        CompilerUtils compilerUtils = constructor.newInstance(new Object[0]);
    }

    @Test()
    public void filesUtilPrivateConstructors() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<FilesUtil> constructor = FilesUtil.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        FilesUtil filesUtil = constructor.newInstance(new Object[0]);
    }

    @Test()
    public void checkerUtilsPrivateConstructors() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<CheckerUtils> constructor = CheckerUtils.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        CheckerUtils checkerUtils = constructor.newInstance(new Object[0]);
    }

    @Test()
    public void unzipUtilsPrivateConstructors4() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<UnzipUtils> constructor = UnzipUtils.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        UnzipUtils unzipUtils = constructor.newInstance(new Object[0]);
    }

    @Test()
    public void protoParserPrivateConstructors5() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<ProtoParser> constructor = ProtoParser.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        ProtoParser protoParser = constructor.newInstance(new Object[0]);
    }

    @Test()
    public void protoScannerPrivateConstructors6() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<ProtoScanner> constructor = ProtoScanner.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        ProtoScanner protoScanner = constructor.newInstance(new Object[0]);
    }


}