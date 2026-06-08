package com.automation.tests;

import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.automation.data.ExcelOperations;
import com.automation.executor.Driver;
import com.automation.listeners.RetryAnalyzer;
import com.automation.utilities.AllureConsoleLogger;
import com.automation.utilities.CommonFunctions;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

/**
 * Main Test Runner - Executes test cases from Excel
 */
@Epic("Selenium Hybrid Framework")
@Feature("Business Flow Tests")
public class TestRunner extends BaseTest {

    /**
     * Data Provider - reads test cases from test-data.json or Excel
     */
    @DataProvider(name = "testCaseData", parallel = true)
    public Object[][] getTestCases() {
        try {
            List<Map<String, String>> testData = ExcelOperations.readExcel(
                    "BusinessFlow",
                    "src\\test\\java\\com\\automation\\tests\\BusinessFlow.xlsx"
            );

            Object[][] data = new Object[testData.size()][3];
            int index = 0;

            for (Map<String, String> row : testData) {
                String tcid = row.get("TCID");
                String executeFlag = row.getOrDefault("Execute", "NO");
                String tags = row.getOrDefault("Tags", "");
                String browser = row.getOrDefault("Browser", "chrome");

                // Only include test cases marked for execution
                if ("YES".equalsIgnoreCase(executeFlag)) {
                    data[index][0] = tcid;
                    data[index][1] = tags;
                    data[index][2] = browser;
                    index++;
                }
            }

            // Trim array to actual size
            Object[][] trimmedData = new Object[index][3];
            System.arraycopy(data, 0, trimmedData, 0, index);

            // Ensure at least `threadCount` test cases are returned so parallel threads can run.
            int threadCount = 0;
            try {
                String tcProp = System.getProperty("threadCount");
                if (tcProp != null && !tcProp.isEmpty()) threadCount = Integer.parseInt(tcProp);
            } catch (Exception ignored) {}

            if (threadCount > 0 && trimmedData.length > 0 && trimmedData.length < threadCount) {
                Object[][] expanded = new Object[threadCount][3];
                for (int i = 0; i < threadCount; i++) {
                    expanded[i] = trimmedData[i % trimmedData.length];
                }
                return expanded;
            }

            return trimmedData;

        } catch (Exception e) {
            System.err.println("❌ Failed to load test data: " + e.getMessage());
            return new Object[0][0];
        }
    }

    /**
     * Main test method that executes the flow
     */
    @Test(dataProvider = "testCaseData", groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    @Story("Execute Business Flow")
    @Severity(SeverityLevel.NORMAL)
    public void executeTestFlow(String testCaseId, String tags, String browserName) {
        try {
            // Add parameters to Allure report
            // Allure.parameter("TCID", testCaseId);
            // Allure.parameter("Tags", tags);
            // Allure.parameter("Browser", browserName);
            
            // Create Parameters section in Allure
            // Allure.step("Parameters", () -> {
            //     Allure.step("🔹 TCID: " + testCaseId);
            //     Allure.step("🏷️ Tags: " + tags);
            //     Allure.step("🌐 Browser: " + browserName);
            // });
            
            System.out.println("\n========================================");
            System.out.println("🚀 Starting Test: " + testCaseId);
            System.out.println("Tags: " + tags);
            System.out.println("Browser: " + browserName);
            System.out.println("========================================\n");

            Allure.parameter("TCID", testCaseId);
            Allure.parameter("Tags", tags);
            Allure.parameter("Browser", browserName);
            Allure.label("category", "Product defects");

            Allure.step("🚀 Execute test flow for " + testCaseId, () -> {
                initializeDriver(browserName);

                // Execute the business flow
                Driver.executeFlow(testCaseId, driver);

                if (CommonFunctions.hadVerificationFailures()) {
                    String failureSummary = "Verification failures occurred for " + testCaseId + ":\n" + String.join("\n", CommonFunctions.getVerificationFailures());
                    Allure.addAttachment("Verification Summary", "text/plain", failureSummary, ".txt");
                    throw new AssertionError(failureSummary);
                }
            });

            System.out.println("\n✅ Test passed: " + testCaseId + "\n");
        } catch (Throwable t) {
            System.err.println("\n❌ Test failed: " + testCaseId);
            System.err.println("Error: " + t.getMessage() + "\n");

            if (t instanceof Error) {
                throw (Error) t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new RuntimeException("Test execution failed: " + testCaseId, t);
        } finally {
            attachExecutionLog();
            AllureConsoleLogger.clear();
        }
    }

    private void attachExecutionLog() {
        try {
            List<String> logs = AllureConsoleLogger.getLogs();
            if (logs != null && !logs.isEmpty()) {
                StringBuilder logContent = new StringBuilder();
                for (String line : logs) {
                    logContent.append(line).append("\n");
                }
                Allure.addAttachment("Execution Log", "text/plain", logContent.toString());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Failed to attach execution log: " + e.getMessage());
        }
    }

}
