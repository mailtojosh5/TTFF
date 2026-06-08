package com.automation.businesscomponents;

import org.openqa.selenium.WebDriver;

import com.automation.pages.LogoutPage;
import com.automation.utilities.CommonFunctions;

public class Logout {

    /**
     * Logout business component
     * Clicks profile button and logout button
     */
    public static void logout(String testCaseName, WebDriver driver) {
        try {
            System.out.println("➡️ Executing Logout for test case: " + testCaseName);

            // Click profile button
            CommonFunctions.clickElement(driver, LogoutPage.PROFILE_BUTTON, "Profile Button");

            // Click logout button
            CommonFunctions.clickElement(driver, LogoutPage.LOGOUT_BUTTON, "Logout Button");

            // Wait for logout to complete
            Thread.sleep(2000);

            System.out.println("✅ Logout completed for " + testCaseName);

        } catch (Exception e) {
            throw new RuntimeException("❌ Logout failed: " + e.getMessage(), e);
        }
    }
}
