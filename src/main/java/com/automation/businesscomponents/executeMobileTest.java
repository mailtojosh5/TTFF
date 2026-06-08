package com.automation.businesscomponents;

import org.openqa.selenium.WebDriver;

import com.automation.utilities.CommonFunctions;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;

/**
 * Mobile business component for Android testing
 */
public class executeMobileTest {
    
    /**
     * Execute mobile test flow. This method supports both regular WebDriver
     * and AppiumDriver (Android/iOS). If an AppiumDriver is provided, it
     * performs a touch-style tap; otherwise it falls back to a standard click.
     */
    public static void executeMobileTest(String testCaseName, WebDriver driver) {
        try {
            System.out.println("🚀 Executing mobile test: " + testCaseName);

           
            CommonFunctions.clickMobileElement(driver, "xpath=//button[@text='Submit']", "Submit Button");
          //  CommonFunctions.clickMobileElement(driver, .id("submit"), "Submit Button");
          //  CommonFunctions.getMobileText(driver, (AppiumBy) AppiumBy.id("submit"), "Submit Button");

            System.out.println("✅ Mobile test completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Mobile test failed: " + e.getMessage());
            throw new RuntimeException("Mobile test execution failed", e);
        }
    }
}
