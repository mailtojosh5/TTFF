package com.automation.executor;

import java.lang.reflect.Method;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.automation.data.ExcelOperations;

public class Driver {

    /**
     * Execute business flow based on test case ID from Excel
     * Reads the flow from BusinessFlow sheet and executes keywords
     */
    public static void executeFlow(String testCaseId, WebDriver driver) {
        try {
            System.out.println("🚀 Starting execution for Test Case: " + testCaseId);

            // Read business flow from Excel
            java.util.List<Map<String, String>> flowData = ExcelOperations.readExcel(
                    "BusinessFlow",
                    "src\\test\\java\\com\\automation\\tests\\BusinessFlow.xlsx"
            );

            // Find the test case in the flow data
            Map<String, String> testCaseData = null;
            for (Map<String, String> row : flowData) {
                if (row.getOrDefault("TCID", "").equals(testCaseId)) {
                    testCaseData = row;
                    break;
                }
            }

            if (testCaseData == null) {
                throw new RuntimeException("❌ Test case not found in BusinessFlow sheet: " + testCaseId);
            }

            // Check if test case is marked for execution
            String executeFlag = testCaseData.getOrDefault("Execute", "NO");
            if (!executeFlag.equalsIgnoreCase("YES")) {
                System.out.println("⏭️ Test case skipped (Execute = " + executeFlag + ")");
                return;
            }

            // Get keywords from columns - try both numbered and duplicate-numbered patterns
            // Check for regular Keyword (first occurrence)
            String keyword = testCaseData.get("Keyword");
            if (keyword != null && !keyword.trim().isEmpty()) {
                System.out.println("➡️ Executing keyword: " + keyword);
                executeKeyword(keyword, testCaseId, driver);
            }
            
            // Check for duplicate column headers: Keyword_1, Keyword_2, Keyword_3
            for (int i = 1; i < 20; i++) {
                keyword = testCaseData.get("Keyword_" + i);
                if (keyword == null || keyword.trim().isEmpty()) {
                    continue;
                }

                System.out.println("➡️ Executing keyword: " + keyword);
                executeKeyword(keyword, testCaseId, driver);
            }

            // Also check for Keyword4, Keyword5, etc
            for (int i = 4; i < 20; i++) {
                keyword = testCaseData.get("Keyword" + i);
                if (keyword == null || keyword.trim().isEmpty()) {
                    continue;
                }

                System.out.println("➡️ Executing keyword: " + keyword);
                executeKeyword(keyword, testCaseId, driver);
            }

            System.out.println("✅ Test case execution completed: " + testCaseId);

        } catch (Exception e) {
            System.err.println("❌ Failed to execute flow: " + e.getMessage());
            throw new RuntimeException("Flow execution failed", e);
        }
    }

    /**
     * Execute a specific keyword/business component
     */
    private static void executeKeyword(String keyword, String testCaseId, WebDriver driver) {
        try {
            // Construct the class name from keyword
            String className = "com.automation.businesscomponents." + keyword;

            // Load the class
            Class<?> clazz = Class.forName(className);

            // Try common method name variants (e.g., "Login" -> "login")
            Method method = null;
            String[] candidates = new String[]{
                    keyword,
                    keyword.toLowerCase(),
                    keyword.substring(0, 1).toLowerCase() + keyword.substring(1)
            };

            for (String candidate : candidates) {
                try {
                    method = clazz.getMethod(candidate, String.class, WebDriver.class);
                    break;
                } catch (NoSuchMethodException ignored) {
                }
            }

            if (method == null) {
                throw new NoSuchMethodException("Method not found for keyword: " + keyword);
            }

            // Invoke the method
            method.invoke(null, testCaseId, driver);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("❌ Business component class not found: " + keyword, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("❌ Method not found in business component: " + keyword, e);
        } catch (Exception e) {
            System.err.println("❌ Failed to execute keyword: " + keyword);
            throw new RuntimeException("Keyword execution failed: " + keyword, e);
        }
    }
}
