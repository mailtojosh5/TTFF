package com.automation.tests;

import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.automation.executor.DriverManager;
import com.automation.utilities.AllureConsoleLogger;
import com.automation.utilities.CommonFunctions;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;

/**
 * Base test class with common setup and teardown
 */
@Epic("Selenium Automation Tests")
@Feature("Test Suite")
public class BaseTest {

    protected WebDriver driver;
    protected String browserName = System.getProperty("browser", "chrome");

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        CommonFunctions.resetSoftAssert();
        CommonFunctions.clearLastVerificationFailure();
        System.out.println("🔧 Base test setup started. WebDriver will be initialized for each test case.");
    }

    protected void initializeDriver(String browserName) {
        try {
            this.browserName = browserName == null || browserName.trim().isEmpty() ? "chrome" : browserName.trim();
            System.out.println("🔧 Initializing WebDriver for browser: " + this.browserName);
            driver = DriverManager.getDriver(this.browserName);
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize WebDriver: " + e.getMessage());
            throw new RuntimeException("WebDriver initialization failed", e);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            // Call assertAll() which will throw if any verification failed.
            // We catch the throwable here and attach it to the ITestResult so
            // the test case (not the teardown) is marked as FAILED in reports.
            if (result.getStatus() == ITestResult.SUCCESS) {
                try {
                    CommonFunctions.assertAll();
                    System.out.println("✅ [tearDown] All verifications passed");
                } catch (Throwable t) {
                    System.out.println("❌ [tearDown] Verifications failed: " + t.getMessage());
                    // Attach failure to the test result so the test shows as failed
                    result.setStatus(ITestResult.FAILURE);
                    result.setThrowable(t);
                    CommonFunctions.setLastVerificationFailure(t);
                    // Also update Allure's test case status/details so the report reflects the failure
                    try {
                        Allure.getLifecycle().updateTestCase(testResult -> {
                            testResult.setStatus(Status.FAILED);
                            testResult.setStatusDetails(new StatusDetails().setMessage(t.getMessage()));
                        });
                    } catch (Exception ignored) {}
                    // Do NOT rethrow — avoid failing the @AfterMethod itself
                }
            } else {
                System.out.println("⚠️ [tearDown] Test already marked as failed");
            }
        } finally {
            try {
                Allure.step("Tear down", () -> {
                    System.out.println("🧹 Cleaning up WebDriver");
                    DriverManager.quit();
                });
            } catch (Exception e) {
                System.err.println("⚠️ Error during teardown: " + e.getMessage());
            } finally {
                // Console log is attached during the Execution phase by the test runner.
                // Clear per-thread logs here to avoid duplicates.
                try {
                    AllureConsoleLogger.clear();
                } catch (Exception e) {
                    System.err.println("⚠️ Failed to clear console logs: " + e.getMessage());
                }
            }
        }
    }
}
