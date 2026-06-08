package com.automation.businesscomponents;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.automation.data.ExcelOperations;
import com.automation.pages.AdminPage;
import com.automation.utilities.CommonFunctions;

public class Admin {

    /**
     * Admin business component
     * Creates a new admin user with data from Excel
     */
    public static void admin(String testCaseName, WebDriver driver) {
        try {
            System.out.println("➡️ Executing Admin for test case: " + testCaseName);

            // Read test data from Excel
            Map<String, String> testData = ExcelOperations.findRowByValue(
                    "Admin",
                    "src\\test\\java\\com\\automation\\tests\\BusinessFlow.xlsx",
                    "TCID",
                    testCaseName
            );

            if (testData == null) {
                throw new RuntimeException("❌ Test case not found in Admin sheet: " + testCaseName);
            }

            // Extract data
            String userRole = testData.getOrDefault("UserRole", testData.get("Username"));
            String employeeName = testData.getOrDefault("EmployeeName", testData.get("Password"));
            String status = testData.getOrDefault("Status", testData.get("Column4"));
            String password = testData.getOrDefault("Password", testData.get("Column5"));

            if (userRole == null || userRole.isEmpty()) {
                userRole = "Admin";
            }
            if (employeeName == null || employeeName.isEmpty()) {
                employeeName = "Admin User";
            }
            if (status == null || status.isEmpty()) {
                status = "Enabled";
            }
            if (password == null || password.isEmpty()) {
                password = "admin123";
            }

            // Perform admin actions
            CommonFunctions.clickElement(driver, AdminPage.ADMIN_TAB, "Admin Tab");
            CommonFunctions.clickElement(driver, AdminPage.ADD_BUTTON, "Add Button");

            // Select user role from dropdown
            CommonFunctions.clickElement(driver, AdminPage.USER_ROLE, "User Role Dropdown");
            Thread.sleep(1000);

            // Send employee name
            CommonFunctions.sendText(driver, AdminPage.EMPLOYEE_NAME, "Employee Name", employeeName);

            // Select status from dropdown
            CommonFunctions.clickElement(driver, AdminPage.STATUS, "Status Dropdown");
            Thread.sleep(1000);

            // Send password
            CommonFunctions.sendText(driver, AdminPage.PASSWORD, "Password", password);

            // Save (uncomment when ready to save)
            // CommonFunctions.clickElement(driver, AdminPage.SAVE_BUTTON, "Save Button");

            System.out.println("✅ Admin flow completed for " + testCaseName);

        } catch (Exception e) {
            throw new RuntimeException("❌ Admin flow failed: " + e.getMessage(), e);
        }
    }
}
