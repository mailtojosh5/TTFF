package com.automation.businesscomponents;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.automation.data.ExcelOperations;
import com.automation.pages.LoginPage;
import com.automation.utilities.CommonFunctions;

public class MobileAutomationLogin {

    /**
     * Mobile login keyword for BusinessFlow.
     * This method uses the existing Login page locators and runs the test in a mobile browser session.
     */
    public static void MobileAutomationLogin(String testCaseName, WebDriver driver) {
        try {
            System.out.println("➡️ Executing MobileAutomationLogin for test case: " + testCaseName);

            CommonFunctions.navigate(driver, LoginPage.URL);

            Map<String, String> testData = ExcelOperations.findRowByValue(
                    "Login",
                    "src\\test\\java\\com\\automation\\tests\\BusinessFlow.xlsx",
                    "TCID",
                    testCaseName
            );

            if (testData == null) {
                throw new RuntimeException("❌ Test case not found in Login sheet: " + testCaseName);
            }

            String username = testData.get("Username");
            String password = testData.get("Password");

            CommonFunctions.sendText(driver, LoginPage.USERNAME_INPUT, "Username Input", username);
            CommonFunctions.sendText(driver, LoginPage.PASSWORD_INPUT, "Password Input", password);
            CommonFunctions.clickElement(driver, LoginPage.LOGIN_BUTTON, "Login Button");

            // Wait for any mobile transitions
            Thread.sleep(2000);
            System.out.println("✅ MobileAutomationLogin completed for " + testCaseName);

        } catch (Exception e) {
            throw new RuntimeException("❌ MobileAutomationLogin failed: " + e.getMessage(), e);
        }
    }
}
