# Selenium Java Hybrid Framework with Allure Reporting

A comprehensive hybrid automation framework built with Selenium WebDriver, TestNG, and Allure for testing web applications.

## 📋 Project Structure

```
src/
├── main/
│   ├── java/com/automation/
│   │   ├── pages/              # Page Object Model classes
│   │   │   ├── LoginPage.java
│   │   │   ├── AdminPage.java
│   │   │   └── LogoutPage.java
│   │   ├── businesscomponents/ # Business flow components
│   │   │   ├── Login.java
│   │   │   ├── Admin.java
│   │   │   └── Logout.java
│   │   ├── utilities/          # Utility classes
│   │   │   ├── CommonFunctions.java
│   │   │   └── RetryHelper.java
│   │   ├── executor/           # Executor classes
│   │   │   ├── DriverManager.java
│   │   │   └── Driver.java
│   │   └── data/               # Data operations
│   │       └── ExcelOperations.java
│   └── resources/              # Configuration files
│       ├── allure.properties
│       └── log4j.properties
└── test/
    ├── java/com/automation/tests/ # Test classes
    │   ├── BaseTest.java
    │   └── TestRunner.java
    └── resources/
        └── testng.xml
```

## ✨ Key Features

- **Page Object Model (POM)**: Organized locators and page interactions
- **Hybrid Framework**: Keyword-driven approach using Excel for test data
- **Allure Reporting**: Beautiful and detailed test reports
- **Retry Logic**: Automatic retry on test failure
- **Self-Healing Locators**: Multiple strategies to find elements
- **Parallel Execution**: Run tests in parallel using TestNG
- **Excel Integration**: Read test data and business flows from Excel
- **WebDriver Management**: ThreadLocal WebDriver management for thread safety
- **Screenshot Capture**: Automatic screenshots on failure

## 🛠️ Prerequisites

- Java 11 or higher
- Maven 3.6+
- Chrome/Firefox browser with compatible WebDriver

## 📦 Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd SeleniumJavaHybridAllure
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

## 🚀 Running Tests

### Run all tests
```bash
mvn clean test
```

### Run smoke tests
```bash
mvn clean test -Dgroups=smoke
```

### Run regression tests
```bash
mvn clean test -Dgroups=regression
```

### Run with specific browser
```bash
mvn clean test -Dbrowser=firefox
```

### Run with different browsers
```bash
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=edge
mvn clean test -Dbrowser=firefox
```

## 📊 Allure Reports

### Generate Allure Report
```bash
mvn allure:report
```

### Open Allure Report
```bash
mvn allure:serve
```

## 📝 Creating Test Cases

### 1. Create Excel File
Create a file `tests/BusinessFlow.xlsx` with sheets:
- **TestCases**: Contains TCID, Execute, Tags columns
- **Login**: Contains TCID, Username, Password
- **Admin**: Contains TCID, UserRole, EmployeeName, Status, Password
- **BusinessFlow**: Contains TCID, Execute, and Keywords columns

### 2. Create Business Component
```java
package com.automation.businesscomponents;

import org.openqa.selenium.WebDriver;
import com.automation.utilities.CommonFunctions;

public class YourComponent {
    public static void yourKeyword(String testCaseId, WebDriver driver) {
        // Your test logic here
    }
}
```

### 3. Create Page Object
```java
package com.automation.pages;

import org.openqa.selenium.By;

public class YourPage {
    public static final String URL = "https://example.com";
    public static final By ELEMENT_LOCATOR = By.xpath("//xpath");
}
```

## 🔧 Common Functions

### Navigate to URL
```java
CommonFunctions.navigate(driver, "https://example.com");
```

### Find Element
```java
WebElement element = CommonFunctions.findElement(driver, By.id("element"), "Element Name");
```

### Click Element
```java
CommonFunctions.clickElement(driver, By.id("button"), "Button Name");
```

### Send Text
```java
CommonFunctions.sendText(driver, By.id("input"), "Field Name", "Value");
```

### Get Text
```java
String text = CommonFunctions.getText(driver, By.id("label"), "Label Name");
```

### Wait for Element
```java
CommonFunctions.waitForElement(driver, By.id("element"));
```

### Capture Screenshot
```java
CommonFunctions.captureScreenshot(driver, "Screenshot Name");
```

## 📋 Allure Annotations

Use Allure annotations for better reporting:

```java
@Epic("Feature Group")
@Feature("Feature Name")
@Story("Story Description")
@Step("Step description")
public void yourTest() {
    // Test code
}
```

## ⚙️ Configuration Files

### testng.xml
TestNG configuration file defining test suites and groups.

### pom.xml
Maven configuration with dependencies:
- Selenium WebDriver 4.15.0
- TestNG 7.8.1
- Allure 2.23.0
- Apache POI 5.0.0
- WebDriverManager 5.6.3

## 🐛 Troubleshooting

### WebDriver not found
- Ensure WebDriverManager is added to dependencies
- It automatically downloads compatible WebDriver versions

### Test data not found
- Verify Excel file path and sheet names
- Check that TCID column matches test case ID

### Allure report not generating
- Run: `mvn allure:report`
- Check target/allure-results directory for results

## 📚 Best Practices

1. **Use POM**: Keep locators in page object classes
2. **Business Components**: Create reusable keyword methods
3. **Error Handling**: Always throw meaningful error messages
4. **Screenshots**: Capture on failure for debugging
5. **Data Separation**: Keep test data in Excel, not hardcoded
6. **Naming**: Use clear, descriptive names for methods and variables

## 🤝 Contributing

1. Create feature branches
2. Follow existing code patterns
3. Add appropriate Allure annotations
4. Test locally before submitting

## 📄 License

ISC

## 📞 Support

For issues or questions, please contact the team or create an issue in the repository.
