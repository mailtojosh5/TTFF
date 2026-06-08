# Quick Start Guide

Get up and running with the Selenium Hybrid Framework in 5 minutes.

## ⚡ Prerequisites

- Java 11+ (`java -version`)
- Maven 3.6+ (`mvn -version`)
- Chrome, Firefox, or Edge browser installed
- Git (optional)

## 🚀 Quick Setup

### 1. Clone/Download Project
```bash
cd SeleniumJavaHybridAllure
```

### 2. Install Dependencies
```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the project
- Prepare for testing

### 3. Create Test Excel File

Create `tests/BusinessFlow.xlsx` with these sheets:

**TestCases Sheet:**
| TCID | Execute | Tags |
|------|---------|------|
| TC_001 | YES | @smoke |
| TC_002 | YES | @regression |

**Login Sheet:**
| TCID | Username | Password |
|------|----------|----------|
| TC_001 | admin | admin123 |
| TC_002 | user | pass123 |

**BusinessFlow Sheet:**
| TCID | Execute | Keyword1 | Keyword2 |
|------|---------|----------|----------|
| TC_001 | YES | Login | Logout |
| TC_002 | YES | Login | Admin | Logout |

### 4. Run Your First Test
```bash
mvn clean test
```

## ✅ Verify Installation

You should see:
- ✅ Build SUCCESS
- ✅ Tests executed
- ✅ Results in `target/allure-results`

## 📊 View Test Reports

```bash
mvn allure:serve
```

This opens the Allure report in your browser.

## 📝 Next Steps

### Create Your First Test Case

1. **Create Business Component** (`src/main/java/com/automation/businesscomponents/YourComponent.java`):
```java
package com.automation.businesscomponents;

import org.openqa.selenium.WebDriver;
import com.automation.utilities.CommonFunctions;
import com.automation.pages.YourPage;

public class YourComponent {
    public static void yourKeyword(String testCaseId, WebDriver driver) {
        // Your test automation code
        CommonFunctions.navigate(driver, "https://example.com");
        CommonFunctions.clickElement(driver, YourPage.SOME_BUTTON, "Button");
    }
}
```

2. **Create Page Object** (`src/main/java/com/automation/pages/YourPage.java`):
```java
package com.automation.pages;

import org.openqa.selenium.By;

public class YourPage {
    public static final String URL = "https://example.com";
    public static final By SOME_BUTTON = By.id("button-id");
    public static final By SOME_INPUT = By.xpath("//input[@name='fieldName']");
}
```

3. **Add to Excel**:
- Update `BusinessFlow.xlsx`
- Add TCID row with keywords
- Mark as Execute: YES

4. **Run Test**:
```bash
mvn clean test
```

## 🎯 Common Commands

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn clean test -Dtest=TestRunner

# Run smoke tests only
mvn clean test -Dgroups=smoke

# Run with Firefox
mvn clean test -Dbrowser=firefox

# Run with headless mode
mvn clean test -Dheadless=true

# Generate Allure report
mvn allure:report

# View Allure report
mvn allure:serve

# Run with Maven output
mvn clean test -X
```

## 📚 Common Functions

```java
// Navigate
CommonFunctions.navigate(driver, "https://example.com");

// Find element
WebElement element = CommonFunctions.findElement(driver, By.id("element"), "Element");

// Click
CommonFunctions.clickElement(driver, By.id("button"), "Button");

// Type text
CommonFunctions.sendText(driver, By.id("input"), "Field", "value");

// Get text
String text = CommonFunctions.getText(driver, By.id("label"), "Label");

// Wait for element
CommonFunctions.waitForElement(driver, By.id("element"));

// Screenshot
CommonFunctions.captureScreenshot(driver, "MyScreenshot");

// Wait for page load
CommonFunctions.waitForPageLoad(driver);
```

## 🐛 Troubleshooting

### Maven build fails
```bash
# Clear Maven cache
mvn clean
rm -rf ~/.m2/repository

# Reinstall
mvn clean install
```

### WebDriver not found
- WebDriverManager automatically downloads drivers
- Check internet connection
- Check `~/.wdm` folder for drivers

### Excel file not found
- Verify path: `tests/BusinessFlow.xlsx`
- Ensure sheet names match Excel exactly
- Check column names in sheet

### Test hangs
- Increase timeout in `src/main/resources/config.properties`
- Check that application is accessible
- Verify internet connection

## 📞 Getting Help

1. Check console output for error messages
2. Look for screenshots in `screenshots/` folder
3. Check logs in `logs/` folder
4. Review Allure report: `mvn allure:serve`

## 🔗 Useful Resources

- [Selenium WebDriver Java](https://www.selenium.dev/documentation/webdriver/getting_started/)
- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [Allure Report](https://docs.qameta.io/allure/)
- [Apache POI](https://poi.apache.org/)

## 🎓 Project Structure Reference

```
src/
├── main/java/com/automation/
│   ├── pages/              - Page Object Models
│   ├── businesscomponents/ - Business logic
│   ├── utilities/          - Helper functions
│   ├── executor/           - Driver & flow execution
│   ├── data/               - Excel operations
│   └── config/             - Configuration
└── test/java/com/automation/tests/
    ├── BaseTest.java       - Test base class
    └── TestRunner.java     - Main test class
```

## ✨ Tips & Tricks

1. **Use page objects** instead of hardcoding locators
2. **Use @Step annotations** for better Allure reports
3. **Add screenshots** on failure for debugging
4. **Use retry logic** for flaky tests
5. **Separate data** from code using Excel
6. **Use business components** for reusability

Happy Testing! 🎉
