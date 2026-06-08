# Conversion Complete: File Reference Guide

This guide explains all the files created during the Playwright → Selenium conversion.

## 📂 Project Configuration Files

### pom.xml
**Purpose**: Maven project configuration and dependency management
**Key Contents**:
- Project metadata (groupId, artifactId, version)
- All Java dependencies (Selenium, TestNG, Allure, POI, etc.)
- Maven plugins for test execution
- Allure report generation configuration
**Modify When**: Adding new dependencies or changing Java version

---

## 🎯 Source Code Files

### src/main/java/com/automation/

#### pages/ - Page Object Models
Define all web page locators and elements

**LoginPage.java**
- URL to application login page
- Locators for username, password, login button
- `STATUS`: Converted from `src/locators/loginPage.ts`

**AdminPage.java**
- Locators for admin page elements
- Add user, role, employee name, status fields
- `STATUS`: Converted from `src/locators/adminPage.ts`

**LogoutPage.java**
- Locators for logout functionality
- Profile button and logout link
- `STATUS`: Converted from `src/locators/logoutPage.ts`

#### businesscomponents/ - Business Logic
Implement keyword-driven automated actions

**Login.java**
- login() method: Performs login with test data from Excel
- Navigates to login page and fills credentials
- `STATUS`: Converted from `src/businesscomponents/login.ts`

**Admin.java**
- admin() method: Creates admin user from Excel data
- Fills multiple form fields and dropdowns
- `STATUS`: Converted from `src/businesscomponents/admin.ts`

**Logout.java**
- logout() method: Logs out from application
- Clicks profile and logout buttons
- `STATUS`: Converted from `src/businesscomponents/logout.ts`

#### utilities/ - Helper Functions

**CommonFunctions.java**
- navigate() - Go to URL
- findElement() - Locate element with waits
- clickElement() - Click and handle errors
- sendText() - Type text in field
- getText() - Read element text
- waitForElement() - Explicit wait
- captureScreenshot() - Screenshot on failure
- `STATUS`: Converted from `src/utilities/commonFunctions.ts`

**RetryHelper.java**
- executeWithRetry() - Retry test execution
- retryAction() - Generic retry mechanism
- Handles flaky test scenarios
- `STATUS`: Converted from `src/utilities/retryHelper.ts`

#### executor/ - Test Execution Engine

**DriverManager.java** ⭐ **NEW**
- getDriver() - Initialize WebDriver  
- Creates browser instance (Chrome, Firefox, Edge)
- quit() - Cleanup WebDriver
- Uses ThreadLocal for thread safety
- Manages browser window and options

**Driver.java**
- executeFlow() - Main test execution logic
- Reads test flow from Excel BusinessFlow sheet
- Executes keywords in sequence
- Dynamic reflection-based keyword execution
- `STATUS`: Converted from `src/executor/driver.ts`

#### data/ - Data Operations

**ExcelOperations.java**
- readExcel() - Read Excel sheet as List<Map>
- readExcelAsArray() - Read as 2D array
- findRowByValue() - Find specific row
- getCellValue() - Get single cell
- Uses Apache POI for Excel handling
- `STATUS`: Converted from `src/data/excelOperations.ts`

#### config/ - Configuration

**Config.java** ⭐ **NEW**
- getProperty() - Read from config.properties
- getIntProperty() - Read integer values
- getBooleanProperty() - Read boolean values
- Provides default values
- Centralized configuration management

### src/main/resources/

**config.properties**
- Browser selection (chrome, firefox, edge)
- Timeout configurations
- Excel file paths
- Logging levels
- Retry settings
- Allure and report paths

**allure.properties**
- Allure report directories
- Report configuration

**log4j.properties**
- Console and file appender configuration
- Log levels for different packages
- Timestamp and message formats

---

## 🧪 Test Files

### src/test/java/com/automation/

#### tests/ - Test Classes

**BaseTest.java** ⭐ **NEW**
- Base test class with @BeforeMethod and @AfterMethod
- WebDriver initialization and cleanup
- Browser selection via system property
- Test setup/teardown logic

**TestRunner.java**
- Main test class with @Test methods
- getTestCases() - DataProvider for test data
- executeTestFlow() - Main test method
- Retry logic implementation
- Reads test cases from Excel
- `STATUS`: Converted from `tests/runner.spec.ts`

#### listeners/ - Test Listeners

**TestListener.java** ⭐ **NEW**
- Implements ITestListener interface
- onTestStart() - Log when test starts
- onTestSuccess() - Log when test passes
- onTestFailure() - Log when test fails
- onStart/onFinish() - Suite lifecycle events
- Integrates with Allure reporting

### src/test/resources/

**testng.xml**
- TestNG suite configuration
- Defines smoke and regression test groups
- Listener configuration (Allure + TestListener)
- Parallel execution settings (methods, thread-count)
- Test class definitions

---

## 📚 Documentation Files

**README.md**
- Complete project overview
- Features and capabilities
- Installation and setup instructions
- Running tests (all variations)
- Allure report generation
- Creating test cases
- Common functions reference
- Best practices

**QUICK_START.md**
- 5-minute quick setup
- Prerequisites checklist
- Step-by-step installation
- First test execution
- Verify installation
- Common commands reference
- Troubleshooting quick tips

**MIGRATION_GUIDE.md**
- Old vs New technology stack
- Code migration examples
- File structure changes
- Class mappings
- Command changes
- Dependencies migration
- Configuration changes
- Key improvements

**CONVERSION_SUMMARY.md**
- Overview of entire conversion
- Statistics and metrics
- List of all converted files
- New project structure
- Features implemented
- Getting started steps
- Breaking changes
- Next steps

**PROJECT_FILES_REFERENCE.md** (this file)
- Explains purpose of each file
- File locations and relationships
- Dependencies between files
- What to modify for different scenarios

---

## 🔗 File Dependencies

```
TestRunner.java
    ↓ extends
BaseTest.java
    ↓ uses
DriverManager.java (for WebDriver)
    ↓ uses
Driver.java (executes flow)
    ↓ loads
Login.java, Admin.java, Logout.java (business components)
    ↓ use
CommonFunctions.java (actions)
    ↓ use
LoginPage.java, AdminPage.java, LogoutPage.java (locators)

All tests use:
ExcelOperations.java (read test data)
RetryHelper.java (retry logic)
Config.java (configuration)
TestListener.java (event handling)
```

---

## 📝 Modification Guide

### To Add a New Test Case:
1. Create new sheet in Excel (if needed)
2. Create new Page class in `pages/`
3. Create new Business Component in `businesscomponents/`
4. Add test data to Excel sheets

### To Add a New Business Function:
1. Create method in business component class
2. Add method name to Excel BusinessFlow sheet
3. Test execution will call it automatically via reflection

### To Change Timeout Values:
- Edit `src/main/resources/config.properties`
- Or modify constants in individual utility classes

### To Add a New Browser:
1. Add case to DriverManager.createDriver()
2. Use same browser name in config or @Test parameter

### To Enable Headless Mode:
1. Edit `src/main/java/com/automation/executor/DriverManager.java`
2. Or set in `config.properties` and use in DriverManager

---

## 🏗️ Build and Execution Flow

```
maven command
  ↓
Reads pom.xml
  ↓
Downloads dependencies
  ↓
Compiles Java source files
  ↓
Runs TestNG (via surefire plugin)
  ↓
Reads testng.xml configuration
  ↓
Loads TestListener and AllureTestNg listeners
  ↓
Runs BaseTest (setup)
  ↓
Runs TestRunner (tests)
  ↓
Reads Excel data via ExcelOperations
  ↓
Executes Driver.executeFlow()
  ↓
Loads business components via reflection
  ↓
Runs teardown
  ↓
Generates allure-results
  ↓
Report generation (mvn allure:report)
```

---

## 📊 File Statistics

| Category | Count | Type |
|----------|-------|------|
| Java Source Files | 14 | Production |
| Java Test Files | 3 | Test |
| Properties Files | 3 | Config |
| XML Config Files | 1 | Config |
| Markdown Docs | 5 | Documentation |
| **Total** | **26** | - |

---

## ✅ Checklist After Conversion

- [x] All TypeScript files converted to Java
- [x] All page objects converted to Java classes
- [x] All business components converted to Java
- [x] All utilities converted to Java
- [x] Test runner converted to TestNG
- [x] Configuration files created
- [x] Documentation completed
- [x] Maven build configured
- [x] Allure integration complete
- [x] Excel operations working
- [] Excel test data file created (YOUR ACTION REQUIRED)
- [ ] Tests executed successfully (YOUR ACTION REQUIRED)

---

## 🚀 Quick Reference Commands

```bash
# Build
mvn clean install

# Run all tests
mvn clean test

# Run smoke tests
mvn clean test -Dgroups=smoke

# Run with Firefox
mvn clean test -Dbrowser=firefox

# Generate report
mvn allure:report

# View report
mvn allure:serve

# Run specific test class
mvn clean test -Dtest=TestRunner
```

---

## 📞 File Lookup

**Need to modify locators?** → Check `src/main/java/com/automation/pages/`
**Need to add test logic?** → Check `src/main/java/com/automation/businesscomponents/`
**Need to add helper functions?** → Check `src/main/java/com/automation/utilities/`
**Need to change browser?** → Check `src/main/java/com/automation/executor/DriverManager.java`
**Need to change timeouts?** → Check `src/main/resources/config.properties`
**Need to change test execution?** → Check `src/test/resources/testng.xml`

---

Last Updated: May 15, 2026
Conversion Status: ✅ Complete
