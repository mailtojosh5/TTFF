package com.automation.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.automation.utilities.AllureConsoleLogger;
import com.automation.utilities.CommonFunctions;
import com.automation.utilities.XrayClient;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;

/**
 * TestNG Listener for test lifecycle events
 */
public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        AllureConsoleLogger.initialize();
        System.out.println("\n========== Test Suite Started: " + context.getName() + " ==========");
        System.out.println("Total Tests: " + context.getAllTestMethods().length);
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size() + "\n");
    }

    @Override
    public void onTestStart(ITestResult result) {
        AllureConsoleLogger.clear();
        
        // Extract TCID from test parameters and update Allure test case name
        Object[] parameters = result.getParameters();
        final String tcid;
        
        if (parameters != null && parameters.length > 0) {
            // First parameter is TCID
            tcid = parameters[0].toString();
        } else {
            tcid = "Test Case";
        }
        
        // Update the Allure test case name to display TCID instead of method name
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setName(tcid);
        });
        
        System.out.println("✅ Test started: " + tcid);
        Allure.step("Test started: " + tcid);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // If any verification failure was recorded, convert this success into a failure
        Throwable verificationFailure = CommonFunctions.getLastVerificationFailure();

        Object[] parameters = result.getParameters();
        final String tcid = (parameters != null && parameters.length > 0) ? parameters[0].toString() : "Test Case";
        final String xrayExecutionKey = (parameters != null && parameters.length > 3) ? parameters[3].toString() : "";

        if (verificationFailure != null) {
            // Attach throwable and mark TestNG result as failed
            result.setStatus(ITestResult.FAILURE);
            result.setThrowable(verificationFailure);

            // Update Allure test case status/details
            try {
                Allure.getLifecycle().updateTestCase(testResult -> {
                    testResult.setStatus(Status.FAILED);
                    testResult.setStatusDetails(new StatusDetails().setMessage(verificationFailure.getMessage()));
                });
            } catch (Exception ignored) {}

            System.out.println("\n========================================");
            System.out.println("❌ FAILED " + tcid + " (verification failure)");
            System.out.println("Reason: " + verificationFailure.getMessage());
            System.out.println("========================================\n");
            Allure.step("FAILED " + tcid, Status.FAILED);
            CommonFunctions.clearLastVerificationFailure();
        } else {
            System.out.println("\n========================================");
            System.out.println("✅ PASSED " + tcid);
            System.out.println("========================================");
            long duration = result.getEndMillis() - result.getStartMillis();
            System.out.println("⏱️ Duration: " + duration + "ms");
            System.out.println("========================================\n");
            // Update Xray: mark as PASSED
            try {
                if (xrayExecutionKey != null && !xrayExecutionKey.isEmpty()) {
                    XrayClient.updateTestCaseStatus(tcid, "PASSED", xrayExecutionKey);
                } else {
                    XrayClient.queueTestResult(tcid, "PASSED");
                }
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Object[] parameters = result.getParameters();
        final String tcid = (parameters != null && parameters.length > 0) ? 
            parameters[0].toString() : "Test Case";
        final String xrayExecutionKey = (parameters != null && parameters.length > 3) ? parameters[3].toString() : "";
        
        System.out.println("\n========================================");
        System.out.println("❌ FAILED " + tcid);
        System.out.println("========================================");
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            System.out.println("Reason: " + throwable.getMessage());
        }
        System.out.println("========================================\n");
        Allure.step("FAILED " + tcid, Status.FAILED);
        // Update Xray: mark as FAILED
        try {
            if (xrayExecutionKey != null && !xrayExecutionKey.isEmpty()) {
                XrayClient.updateTestCaseStatus(tcid, "FAILED", xrayExecutionKey);
            } else {
                XrayClient.queueTestResult(tcid, "FAILED");
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭️ Test skipped: " + result.getName());
        // Update Xray: mark as SKIPPED
        Object[] parameters = result.getParameters();
        final String tcid = (parameters != null && parameters.length > 0) ? parameters[0].toString() : result.getName();
        final String xrayExecutionKey = (parameters != null && parameters.length > 3) ? parameters[3].toString() : "";
        try {
            if (xrayExecutionKey != null && !xrayExecutionKey.isEmpty()) {
                XrayClient.updateTestCaseStatus(tcid, "SKIPPED", xrayExecutionKey);
            } else {
                XrayClient.queueTestResult(tcid, "SKIPPED");
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("\n========== Test Suite Finished: " + context.getName() + " ==========");
        System.out.println("Total Tests: " + context.getAllTestMethods().length);
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size() + "\n");
        // Flush any queued Xray updates (batched when no per-row execution key provided)
        try {
            XrayClient.flushBatch();
        } catch (Exception ignored) {}
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not implemented
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        System.out.println("⏰ Test timed out: " + result.getName());
    }
}
