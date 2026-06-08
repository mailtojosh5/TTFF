# Migration Guide: Playwright to Selenium Java

This document outlines the conversion from Playwright/TypeScript to Selenium/Java.

## 📦 Technology Stack Changes

### Before (Playwright/TypeScript)
- **Framework**: Playwright with TypeScript
- **Package Manager**: npm
- **Test Runner**: Playwright Test
- **Language**: TypeScript
- **Build Tool**: None (Node.js)

### After (Selenium/Java)
- **Framework**: Selenium WebDriver with Java
- **Package Manager**: Maven
- **Test Runner**: TestNG
- **Language**: Java
- **Build Tool**: Maven

## 🔄 Code Migration

### Project Structure
```
OLD (TypeScript)              NEW (Java)
src/locators/ → src/main/java/com/automation/pages/
src/businesscomponents/ → src/main/java/com/automation/businesscomponents/
src/utilities/ → src/main/java/com/automation/utilities/
src/executor/ → src/main/java/com/automation/executor/
src/data/ → src/main/java/com/automation/data/
tests/ → src/test/java/com/automation/tests/
```

## 📝 File Type Changes

| TypeScript (*.ts) | Java (*.java) |
|---|---|
| Page objects as const | Page objects as classes with static By objects |
| Functions in modules | Static methods in classes |
| Async/await | Synchronous (with Thread.sleep for waits) |
| Import modules | Import packages |

## 🔍 Key Class Mappings

### WebDriver Management
```typescript
// OLD - Playwright
const browser = await chromium.launch();
const page = await browser.newPage();
```

```java
// NEW - Selenium
WebDriver driver = DriverManager.getDriver("chrome");
driver.manage().window().maximize();
```

### Locators
```typescript
// OLD - Playwright (string locators)
export const LoginPage = {
  usernameTxt: "//*[@name='username']",
  passwordTxt: "//*[@name='password']"
};

// Using locator
await page.fill(LoginPage.usernameTxt, "admin");
```

```java
// NEW - Selenium (By objects)
public class LoginPage {
  public static final By USERNAME_INPUT = By.xpath("//*[@name='username']");
  public static final By PASSWORD_INPUT = By.xpath("//*[@name='password']");
}

// Using locator
CommonFunctions.sendText(driver, LoginPage.USERNAME_INPUT, "admin");
```

### Common Actions
```typescript
// OLD - Playwright
await page.fill(locator, "text");
await page.click(locator);
await page.waitForLoadState("domcontentloaded");
```

```java
// NEW - Selenium
CommonFunctions.sendText(driver, locator, "Field", "text");
CommonFunctions.clickElement(driver, locator, "Button");
CommonFunctions.waitForPageLoad(driver);
```

### Excel Operations
```typescript
// OLD - TypeScript
const data = await ExcelOperations.readExcel("Login", "tests/BusinessFlow.xlsx");
const row = data.find(r => r[1] === testCaseName);
```

```java
// NEW - Java
Map<String, String> testData = ExcelOperations.findRowByValue(
  "Login",
  "tests/BusinessFlow.xlsx",
  "TCID",
  testCaseId
);
```

### Test Definitions
```typescript
// OLD - Playwright Test
test(`Execute Flow: ${tcid}`, async ({}, testInfo) => {
  // test code
});
```

```java
// NEW - TestNG
@Test(dataProvider = "testCaseData")
public void executeTestFlow(String testCaseId, String tags) {
  // test code
}
```

## 🚀 Running Tests - Command Changes

| Command | TypeScript | Java |
|---|---|---|
| Install | npm install | mvn clean install |
| Run All | npm test | mvn clean test |
| Smoke Tests | npm run smoke | mvn clean test -Dgroups=smoke |
| Regression | npm run regression | mvn clean test -Dgroups=regression |
| Reports | npm run allure:open | mvn allure:serve |
| Generate Report | npm run allure:generate | mvn allure:report |

## 📊 Report Generation

### OLD (Playwright)
```bash
allure generate allure-results --clean -o allure-report
allure open allure-report
```

### NEW (Selenium/Java)
```bash
mvn allure:report
mvn allure:serve
```

## ⚙️ Configuration Changes

### TypeScript Config
- **playwright.config.ts**: Browser configuration, timeout, workers, screenshots
- **package.json**: Scripts and dependencies

### Java Config
- **pom.xml**: Dependencies and build configuration
- **testng.xml**: Test suite definition and listeners
- **config.properties**: Application configuration
- **allure.properties**: Allure report configuration

## 🔗 Dependencies Migration

### NPM Packages → Maven Dependencies
| NPM Package | Maven Dependency |
|---|---|
| @playwright/test | org.seleniumhq.selenium:selenium-java |
| allure-playwright | io.qameta.allure:allure-testng |
| testng | org.testng:testng |
| exceljs, xlsx | org.apache.poi:poi-ooxml |
| TypeScript | Java built-in |

## 🧪 Test Execution Differences

### Parallel Execution
```typescript
// TypeScript - in playwright.config.ts
workers: process.env.CI ? 2 : undefined
```

```java
// Java - in testng.xml
<suite ... parallel="methods" thread-count="3">
```

### Retry Configuration
```typescript
// TypeScript - in playwright.config.ts
retries: process.env.CI ? 2 : 0
```

```java
// Java - in RetryHelper.java
private static final int MAX_RETRIES = 2;
```

## 📋 Excel Data Structure

The Excel structure remains similar with some naming adjustments:

```
Sheet: TestCases
├─ TCID
├─ Execute (YES/NO)
├─ Tags (@smoke, @regression)
└─ Other metadata

Sheet: Login
├─ TCID
├─ Username
├─ Password

Sheet: BusinessFlow
├─ TCID
├─ Execute (YES/NO)
├─ Keyword1, Keyword2, ...
```

## 🔨 Development Tools

### IDEs
- **TypeScript**: VS Code, WebStorm
- **Java**: IntelliJ IDEA, Eclipse, VS Code (with extensions)

### Debugging
```bash
# TypeScript
node --inspect test-file.js

# Java
mvn clean test -Dtest=TestRunner#testMethod
```

## 📚 Key Improvements

1. **Object-Oriented**: Full OOP support with inheritance and interfaces
2. **Type Safety**: Compile-time type checking
3. **Performance**: Faster execution than JavaScript
4. **IDE Support**: Better code completion and refactoring
5. **Maven Ecosystem**: Vast library ecosystem
6. **Java Community**: Larger testing community

## ⚠️ Important Notes

1. **Synchronous vs Asynchronous**
   - TypeScript uses async/await
   - Java is synchronous with explicit waits

2. **Dynamic Loading**
   - TypeScript dynamically loads modules
   - Java uses reflection for dynamic loading (see Driver.java)

3. **Excel Operations**
   - Switched from exceljs to Apache POI
   - Data returned as Maps instead of arrays

4. **ThreadLocal WebDriver**
   - Java implementation uses ThreadLocal for thread-safe WebDriver management
   - Important for parallel test execution

## 🎯 Next Steps

1. Update Excel file paths and sheet names
2. Migrate test data to Excel format
3. Build project: `mvn clean install`
4. Run tests: `mvn clean test`
5. Generate reports: `mvn allure:serve`

## 📞 Support

For detailed information on any component, refer to the inline code documentation in the respective Java classes.
