package com.automation.businesscomponents;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.automation.data.ExcelOperations;
import com.automation.pages.LoginPage;
import com.automation.utilities.CommonFunctions;

public class Login {

    /**
     * Login business component
     * Reads login credentials from Excel and performs login
     */
    public static void login(String testCaseName, WebDriver driver) {
        try {
            System.out.println("➡️ Executing Login for test case: " + testCaseName);

            // Navigate to login page
            CommonFunctions.navigate(driver, LoginPage.URL);

            // Read test data from Excel
            Map<String, String> testData = ExcelOperations.findRowByValue(
                    "Login",
                    "src\\test\\java\\com\\automation\\tests\\BusinessFlow.xlsx",
                    "TCID",
                    testCaseName
            );

            if (testData == null) {
                throw new RuntimeException("❌ Test case not found in Login sheet: " + testCaseName);
            }

            // Extract data
            String username = testData.get("Username");
            String password = testData.get("Password");

            // Perform login actions
            CommonFunctions.verifyElementTextContains(driver, LoginPage.LOGIN_BUTTON, "submit", "Login button");
            CommonFunctions.sendText(driver, LoginPage.USERNAME_INPUT, "Username Input", username);
            CommonFunctions.sendText(driver, LoginPage.PASSWORD_INPUT, "Password Input", password);
            CommonFunctions.clickElement(driver, LoginPage.LOGIN_BUTTON, "Login Button");

            // Validation - wait for dashboard
            Thread.sleep(2000);
            System.out.println("✅ Login completed for " + testCaseName);

        } catch (Exception e) {
            throw new RuntimeException("❌ Login failed: " + e.getMessage(), e);
        }
    }
}
