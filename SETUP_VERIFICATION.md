# Setup Verification Checklist

Use this checklist to verify your Selenium Java conversion is complete and ready to use.

## ✅ Prerequisites Check

- [ ] Java 11+ installed
  ```bash
  java -version  # Should show Java 11 or higher
  ```

- [ ] Maven 3.6+ installed
  ```bash
  mvn -version  # Should show Maven 3.6.0 or higher
  ```

- [ ] Git installed (optional)
  ```bash
  git --version
  ```

- [ ] Chrome/Firefox browser installed and accessible

---

## ✅ Project Structure Check

### Core Source Files
- [ ] `src/main/java/com/automation/pages/LoginPage.java` exists
- [ ] `src/main/java/com/automation/pages/AdminPage.java` exists
- [ ] `src/main/java/com/automation/pages/LogoutPage.java` exists

### Business Components
- [ ] `src/main/java/com/automation/businesscomponents/Login.java` exists
- [ ] `src/main/java/com/automation/businesscomponents/Admin.java` exists
- [ ] `src/main/java/com/automation/businesscomponents/Logout.java` exists

### Utilities
- [ ] `src/main/java/com/automation/utilities/CommonFunctions.java` exists
- [ ] `src/main/java/com/automation/utilities/RetryHelper.java` exists

### Executor
- [ ] `src/main/java/com/automation/executor/DriverManager.java` exists
- [ ] `src/main/java/com/automation/executor/Driver.java` exists

### Data
- [ ] `src/main/java/com/automation/data/ExcelOperations.java` exists

### Config
- [ ] `src/main/java/com/automation/config/Config.java` exists

### Test Files
- [ ] `src/test/java/com/automation/tests/BaseTest.java` exists
- [ ] `src/test/java/com/automation/tests/TestRunner.java` exists
- [ ] `src/test/java/com/automation/listeners/TestListener.java` exists

### Configuration Files
- [ ] `src/main/resources/config.properties` exists
- [ ] `src/main/resources/allure.properties` exists
- [ ] `src/main/resources/log4j.properties` exists
- [ ] `src/test/resources/testng.xml` exists

### POM File
- [ ] `pom.xml` exists in root directory

---

## ✅ Build Check

### 1. Clean Build
```bash
mvn clean
```
- [ ] No errors during clean

### 2. Install Dependencies
```bash
mvn install
```
- [ ] All dependencies downloaded successfully
- [ ] No "Failed to download" messages
- [ ] Sees "BUILD SUCCESS"

### 3. Verify Compilation
```bash
mvn compile
```
- [ ] Compilation successful
- [ ] No compilation errors shown
- [ ] No warnings (or only minor warnings)

### 4. Verify Test Compilation
```bash
mvn test-compile
```
- [ ] Test classes compiled successfully
- [ ] No compilation errors for test classes

---

## ✅ Dependency Check

Verify all dependencies are present:
```bash
mvn dependency:tree
```

Check that includes:
- [ ] `org.seleniumhq.selenium:selenium-java:4.15.0`
- [ ] `org.testng:testng:7.8.1`
- [ ] `io.qameta.allure:allure-testng:2.23.0`
- [ ] `org.apache.poi:poi-ooxml:5.0.0`
- [ ] `io.github.bonigarcia:webdrivermanager:5.6.3`
- [ ] `commons-io:commons-io:2.11.0`

---

## ✅ Excel Test Data Check

### Create Required Excel File
Create `tests/BusinessFlow.xlsx` (if not exists)

### Verify Sheet Structure

**TestCases Sheet:**
- [ ] Columns exist: TCID, Execute, Tags
- [ ] At least 1 test case with Execute = "YES"
- [ ] Example:
  ```
  TCID    | Execute | Tags
  TC_001  | YES     | @smoke
  ```

**Login Sheet:**
- [ ] Columns exist: TCID, Username, Password
- [ ] Data for at least one test case
- [ ] Example:
  ```
  TCID    | Username | Password
  TC_001  | admin    | admin123
  ```

**BusinessFlow Sheet:**
- [ ] Columns exist: TCID, Execute, and keywords
- [ ] Keywords point to valid business components
- [ ] Example:
  ```
  TCID    | Execute | Keyword1 | Keyword2
  TC_001  | YES     | Login    | Logout
  ```

---

## ✅ IDE Configuration Check (Optional but Recommended)

### For IntelliJ IDEA
- [ ] Project SDK set to Java 11 or higher
- [ ] Project Structure → SDK configured
- [ ] Maven home configured (Settings → Build → Maven)

### For VS Code
- [ ] Java Extension Pack installed
- [ ] Project file recognition working
- [ ] IntelliCode installed (optional, for better suggestions)

### For Eclipse
- [ ] Java 11 JRE configured
- [ ] Maven plugin installed (m2e)
- [ ] Project converted to Maven project

---

## ✅ WebDriver Check

Run WebDriver setup:
```bash
mvn clean test -Dtest=TestRunner#smokeTest
```

Check that:
- [ ] ChromeDriver is downloaded (or WebDriver auto-downloaded)
- [ ] No "WebDriver not found" errors
- [ ] Browser launches and closes properly

---

## ✅ Documentation Check

- [ ] `README.md` exists and is readable
- [ ] `QUICK_START.md` exists
- [ ] `MIGRATION_GUIDE.md` exists
- [ ] `CONVERSION_SUMMARY.md` exists
- [ ] `PROJECT_FILES_REFERENCE.md` exists

---

## ✅ Configuration Check

### config.properties
```bash
grep -E "^(browser|timeout|base.url)" src/main/resources/config.properties
```
- [ ] `browser=chrome` (or your preferred browser)
- [ ] `timeout.explicit=30` (or suitable value)
- [ ] `base.url` points to test application

### allure.properties
```bash
cat src/main/resources/allure.properties
```
- [ ] Allure results directory configured
- [ ] Allure report directory configured

### log4j.properties
```bash
cat src/main/resources/log4j.properties
```
- [ ] Log level set appropriately
- [ ] Console and file appenders configured

### testng.xml
```bash
cat src/test/resources/testng.xml
```
- [ ] Listeners are configured
- [ ] Test classes are defined
- [ ] Parallel settings are set

---

## ✅ Execution Check

### 1. Run a Single Test
```bash
mvn clean test -Dtest=TestRunner -Dgroups=smoke
```

Expected output:
- [ ] Test starts
- [ ] WebDriver initializes
- [ ] Test runs (may pass/fail depending on data)
- [ ] Report generated
- [ ] No "Exception" errors

### 2. Check Test Results
```bash
ls -la target/surefire-reports/
```
- [ ] TEST-*.xml files exist
- [ ] Multiple test report files

### 3. Check Allure Results
```bash
ls -la target/allure-results/
```
- [ ] *-result.json files exist
- [ ] Test case data captured

---

## ✅ Report Generation Check

### Generate Allure Report
```bash
mvn allure:report
```

Check that:
- [ ] No errors during generation
- [ ] target/allure-report directory created
- [ ] index.html exists in allure-report

### View Allure Report
```bash
mvn allure:serve
```

Check that:
- [ ] Browser opens automatically
- [ ] Allure report displays
- [ ] Test results visible
- [ ] Can navigate reports

---

## ✅ Browser Compatibility Check

Test each supported browser:

```bash
# Chrome
mvn clean test -Dbrowser=chrome

# Firefox
mvn clean test -Dbrowser=firefox

# Edge
mvn clean test -Dbrowser=edge
```

- [ ] Chrome tests pass
- [ ] Firefox tests pass
- [ ] Edge tests pass

---

## ✅ Parallel Execution Check

```bash
mvn clean test -Dgroups=smoke,regression -DthreadCount=3
```

- [ ] Multiple tests run simultaneously
- [ ] No thread conflicts
- [ ] All tests complete successfully
- [ ] Results are consistent

---

## ✅ Logging Check

### Check Console Logs
```bash
mvn clean test 2>&1 | grep -E "(ERROR|WARN|INFO)" | head -20
```

- [ ] ERROR messages are minimal (only expected errors)
- [ ] INFO messages show test progress
- [ ] WARN messages are informational

### Check Log Files
```bash
ls -la logs/
cat logs/automation-test.log | tail -50
```

- [ ] Log file created
- [ ] Contains test execution details
- [ ] No unexpected errors

---

## ✅ Screenshots Check

Run a test and check for failures:
```bash
mvn clean test
```

- [ ] Screenshots directory created (if failures occur)
- [ ] Screenshots have timestamps
- [ ] Screenshots are readable PNG files

---

## ✅ Final Verification

Run complete test suite:
```bash
mvn clean test allure:report
```

Verify:
- [ ] No build errors (BUILD SUCCESS)
- [ ] All tests executed as expected
- [ ] Allure report generated
- [ ] Report accessible and complete

---

## 📋 Common Issues & Solutions

### Issue: Java version error
**Solution**: 
```bash
java -version  # Check java version
mvn -version   # Check Maven uses correct Java
```

### Issue: Maven can't find dependencies
**Solution**:
```bash
mvn clean install -U  # Force update dependencies
```

### Issue: WebDriver not found
**Solution**:
- Internet connection required (WebDriverManager downloads drivers)
- Check `~/.wdm` directory for cached drivers

### Issue: Excel file not found
**Solution**:
- Verify path: `tests/BusinessFlow.xlsx` exists
- Check file is not locked by another application

### Issue: Test hangs or times out
**Solution**:
- Increase timeout in config.properties
- Check application is accessible
- Verify internet connection

---

## 🎯 Completion Criteria

All of the following should be true:

- [ ] Maven build succeeds without errors
- [ ] Dependencies resolve correctly
- [ ] Java code compiles without errors
- [ ] TestNG configuration is valid
- [ ] Excel test data is properly formatted
- [ ] WebDriver downloads or is cached correctly
- [ ] Tests execute (pass or fail with meaningful errors)
- [ ] Allure reports generate successfully
- [ ] All documentation files are present
- [ ] Project structure matches expected layout

---

## ✨ Congratulations!

If all checkboxes above are checked, your Selenium Java project is:
- ✅ Properly converted from Playwright
- ✅ Set up correctly
- ✅ Ready for test development
- ✅ Ready for CI/CD integration

---

## 📞 Next Steps

1. **Read** - Review the QUICK_START.md guide
2. **Test** - Create your first test case
3. **Execute** - Run tests with Maven
4. **Report** - Generate and review Allure reports
5. **Extend** - Add more business components and test cases

---

**Date**: May 15, 2026
**Status**: Conversion Complete ✅
