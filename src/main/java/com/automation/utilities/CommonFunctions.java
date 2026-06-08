package com.automation.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

public class CommonFunctions {

    private static final int TIMEOUT = 30;

    // ── Thread-local SoftAssert so each test thread has its own instance ──────
    private static final ThreadLocal<SoftAssert> softAssert =
        ThreadLocal.withInitial(SoftAssert::new);

    // ── Thread-local failure log for Allure reporting ─────────────────────────
    private static final ThreadLocal<List<String>> verificationFailures =
        ThreadLocal.withInitial(ArrayList::new);

    /**
     * Call this at the START of every @Test method to reset soft assertions.
     * Put it in a @BeforeMethod in your base test class instead to keep tests clean.
     */
    public static void resetSoftAssert() {
        softAssert.set(new SoftAssert());
        verificationFailures.set(new ArrayList<>());
    }

    /**
     * Call this at the END of every @Test method (or in @AfterMethod).
     * This is where failures are actually reported — test fails here if any
     * soft assertion failed, but only after all verifications have run.
     */
    public static void assertAll() {
        List<String> failures = verificationFailures.get();
        if (!failures.isEmpty()) {
            // Attach consolidated failure summary to Allure
            String summary = "❌ Verification Failures:\n" + String.join("\n", failures);
            Allure.addAttachment("Verification Summary", "text/plain", summary, ".txt");
            log(summary);
        }
        try {
            softAssert.get().assertAll(); // throws if any soft assertion failed
        } finally {
            // Always reset after assertAll so next test starts clean
            resetSoftAssert();
        }
    }

    /**
     * Check if there were any verification failures recorded during this test.
     * Does NOT reset the failures list.
     */
    public static boolean hadVerificationFailures() {
        return !verificationFailures.get().isEmpty();
    }

    /**
     * Returns a copy of all verification failure messages for the current test.
     */
    public static List<String> getVerificationFailures() {
        return new ArrayList<>(verificationFailures.get());
    }

    /**
     * Get the last verification failure throwable that was caught.
     * Useful for test listeners to retrieve the exception.
     */
    private static final ThreadLocal<Throwable> lastVerificationFailure = ThreadLocal.withInitial(() -> null);

    public static Throwable getLastVerificationFailure() {
        return lastVerificationFailure.get();
    }

    public static void setLastVerificationFailure(Throwable t) {
        lastVerificationFailure.set(t);
    }

    public static void clearLastVerificationFailure() {
        lastVerificationFailure.set(null);
    }
    // ─────────────────────────────────────────────────────────────────────────
    // Verification methods — all use soft assertions, execution continues
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verify element text equals expected value.
     */
    public static void verifyElementText(WebDriver driver, By locator,
                                         String expectedText, String fieldName) {
    String actualText = getText(driver, locator, fieldName);
    log("🔎 Verifying text for " + fieldName
            + ": expected=[" + expectedText + "] actual=[" + actualText + "]");

    boolean passed = expectedText.equals(actualText);
    String msg = passed
        ? "✅ Verify text for " + fieldName
        : "❌ [" + fieldName + "] expected=[" + expectedText + "] actual=[" + actualText + "]";

    if (!passed) verificationFailures.get().add(msg);

    Allure.step(msg, stepContext -> {
        String screenshotName = passed ? "Verify_Passed_" + fieldName : "Verify_Failed_" + fieldName;
        captureScreenshot(driver, screenshotName);
        if (!passed) {
            Allure.getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
        }
        softAssert.get().assertEquals(actualText, expectedText,
            "Text verification failed for " + fieldName);
    });
}

/**
 * Verify element text contains expected substring.
 */
public static void verifyElementTextContains(WebDriver driver, By locator,
                                              String expectedSubstring, String fieldName) {
    String actualText = getText(driver, locator, fieldName);
    log("🔎 Verifying text contains for " + fieldName
            + ": expected substring=[" + expectedSubstring + "] actual=[" + actualText + "]");

    boolean passed = actualText.contains(expectedSubstring);
    String msg = passed
        ? "✅ Verify text contains for " + fieldName
        : "❌ [" + fieldName + "] does not contain=[" + expectedSubstring + "] actual=[" + actualText + "]";

    if (!passed) verificationFailures.get().add(msg);

    Allure.step(msg, stepContext -> {
        String screenshotName = passed ? "Verify_Passed_" + fieldName : "Verify_Failed_" + fieldName;
        captureScreenshot(driver, screenshotName);
        if (!passed) {
            Allure.getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
        }
        softAssert.get().assertTrue(actualText.contains(expectedSubstring),
            "Text does not contain expected value for " + fieldName);
    });
}

/**
 * Verify input field value equals expected value.
 */
public static void verifyFieldValue(WebDriver driver, By locator,
                                     String expectedValue, String fieldName) {
    try {
        WebElement element = findElement(driver, locator, fieldName);
        String actualValue = element.getAttribute("value");
        log("🔎 Verifying field value for " + fieldName
                + ": expected=[" + expectedValue + "] actual=[" + actualValue + "]");

        boolean passed = expectedValue.equals(actualValue);
        String msg = passed
            ? "✅ Verify field value for " + fieldName
            : "❌ [" + fieldName + "] field value expected=[" + expectedValue + "] actual=[" + actualValue + "]";

        if (!passed) verificationFailures.get().add(msg);

        Allure.step(msg, stepContext -> {
            String screenshotName = passed ? "Verify_Passed_" + fieldName : "Verify_Failed_" + fieldName;
            captureScreenshot(driver, screenshotName);
            if (!passed) {
                Allure.getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
            }
            softAssert.get().assertEquals(actualValue, expectedValue,
                "Field value verification failed for " + fieldName);
        });
    } catch (Exception e) {
        String msg = "❌ [" + fieldName + "] could not retrieve field value: " + e.getMessage();
        verificationFailures.get().add(msg);
        log(msg);
        Allure.step(msg, stepContext -> {
            captureScreenshot(driver, "Verify_Error_" + fieldName);
            Allure.getLifecycle().updateStep(step -> step.setStatus(Status.FAILED));
            softAssert.get().fail(msg);
        });
    }
}

    // ─────────────────────────────────────────────────────────────────────────
    // All your other existing methods below — unchanged
    // ─────────────────────────────────────────────────────────────────────────

    public static WebElement findElement(WebDriver driver, By locator, String elementName) {
        try {
            System.out.println("🔎 Locating: " + elementName);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            captureScreenshot(driver, "Element_Not_Found_" + elementName);
            throw new RuntimeException("❌ Element [" + elementName + "] not found. " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mobile helpers — supports Android and iOS XCUITest using AppiumBy locators
    // ─────────────────────────────────────────────────────────────────────────

    public static WebElement findMobileElement(AppiumDriver driver, By locator, String elementName) {
        try {
            System.out.println("🔎 Locating mobile element: " + elementName);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            captureScreenshot(driver, "MobileElement_Not_Found_" + elementName);
            throw new RuntimeException("❌ Mobile element [" + elementName + "] not found. " + e.getMessage());
        }
    }

    public static WebElement findElement(AppiumDriver driver, By locator, String elementName) {
        return findMobileElement(driver, locator, elementName);
    }

    // Parse a locator string into a Selenium/Appium By. Supports prefixes like:
    // "xpath=...", "id=...", "accessibilityId=...", "android=...".
    private static By parseAppiumBy(String locator) {
        if (locator == null) throw new IllegalArgumentException("Locator cannot be null");
        if (locator.startsWith("xpath=")) return AppiumBy.xpath(locator.substring(6));
        if (locator.startsWith("id=")) return AppiumBy.id(locator.substring(3));
        if (locator.startsWith("accessibilityId=")) return AppiumBy.accessibilityId(locator.substring("accessibilityId=".length()));
        if (locator.startsWith("android=")) return AppiumBy.androidUIAutomator(locator.substring("android=".length()));
        // default: treat as xpath expression
        return AppiumBy.xpath(locator);
    }

    public static void clickMobileElement(WebDriver driver, String locator, String elementName) {
        try {
            System.out.println("🖱️ Clicking mobile element: " + elementName);

            // Accept locator strings in the form "xpath=...", "id=...", "accessibilityId=...", "android=..."
            org.openqa.selenium.By by;
            if (locator == null) {
                throw new IllegalArgumentException("Locator cannot be null");
            } else if (locator.startsWith("xpath=")) {
                by = AppiumBy.xpath(locator.substring(6));
            } else if (locator.startsWith("id=")) {
                by = AppiumBy.id(locator.substring(3));
            } else if (locator.startsWith("accessibilityId=")) {
                by = AppiumBy.accessibilityId(locator.substring("accessibilityId=".length()));
            } else if (locator.startsWith("android=")) {
                by = AppiumBy.androidUIAutomator(locator.substring("android=".length()));
            } else {
                // Default to treating the whole string as an XPath expression
                by = AppiumBy.xpath(locator);
            }

            WebElement element = driver.findElement(by);
            element.click();
            System.out.println("✅ " + elementName + " clicked successfully");
            captureScreenshot(driver, "MobileClick_Success_" + elementName);
        } catch (Exception e) {
            captureScreenshot(driver, "MobileClick_Failed_" + elementName);
            throw new RuntimeException("❌ Mobile click failed on " + elementName + ": " + e.getMessage());
        }
    }

    public static void clickMobileElement(WebDriver driver, By locator, String elementName) {
        try {
            System.out.println("🖱️ Clicking mobile element: " + elementName);
            WebElement element = driver.findElement(locator);
            element.click();
            System.out.println("✅ " + elementName + " clicked successfully");
            captureScreenshot(driver, "MobileClick_Success_" + elementName);
        } catch (Exception e) {
            captureScreenshot(driver, "MobileClick_Failed_" + elementName);
            throw new RuntimeException("❌ Mobile click failed on " + elementName + ": " + e.getMessage());
        }
    }

    public static void clickElement(AppiumDriver driver, By locator, String elementName) {
        clickMobileElement(driver, locator, elementName);
    }

    public static void tapMobileElement(AppiumDriver driver, By locator, String elementName) {
        try {
            System.out.println("👆 Tapping mobile element: " + elementName);
            WebElement element = findMobileElement(driver, locator, elementName);
            PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
            Sequence tap = new Sequence(finger, 0);
            Point elementLocation = new Point(element.getLocation().x, element.getLocation().y);
            tap.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), elementLocation));
            tap.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
            tap.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
            driver.perform(java.util.Collections.singletonList(tap));
            System.out.println("✅ Tapped " + elementName + " successfully");
            captureScreenshot(driver, "MobileTap_Success_" + elementName);
        } catch (Exception e) {
            captureScreenshot(driver, "MobileTap_Failed_" + elementName);
            throw new RuntimeException("❌ Mobile tap failed on " + elementName + ": " + e.getMessage());
        }
    }

    public static void tapElement(AppiumDriver driver, By locator, String elementName) {
        tapMobileElement(driver, locator, elementName);
    }

    public static void tapElement(WebDriver driver, By locator, String elementName) {
        clickElement(driver, locator, elementName);
    }

    // --- String-locator overloads for mobile helpers ---------------------
    public static WebElement findMobileElement(WebDriver driver, String locator, String elementName) {
        return findMobileElement((AppiumDriver) driver, parseAppiumBy(locator), elementName);
    }

    public static void tapMobileElement(WebDriver driver, String locator, String elementName) {
        tapMobileElement((AppiumDriver) driver, parseAppiumBy(locator), elementName);
    }

    public static void sendTextToMobileElement(WebDriver driver, String locator, String elementName, String value) {
        sendTextToMobileElement((AppiumDriver) driver, parseAppiumBy(locator), elementName, value);
    }

    public static String getMobileText(WebDriver driver, String locator, String elementName) {
        return getMobileText((AppiumDriver) driver, parseAppiumBy(locator), elementName);
    }

    public static boolean isMobileElementDisplayed(WebDriver driver, String locator) {
        return isMobileElementDisplayed((AppiumDriver) driver, parseAppiumBy(locator));
    }

    public static void waitForMobileElement(WebDriver driver, String locator) {
        waitForMobileElement((AppiumDriver) driver, parseAppiumBy(locator));
    }

    public static void clickElement(WebDriver driver, String locator, String elementName) {
        clickMobileElement(driver, locator, elementName);
    }

    public static void sendTextToMobileElement(AppiumDriver driver, By locator, String elementName, String value) {
        try {
            System.out.println("📝 Sending text to mobile element: " + elementName);
            WebElement element = findMobileElement(driver, locator, elementName);
            element.clear();
            element.sendKeys(value);
            System.out.println("✅ Text sent to mobile element " + elementName);
            captureScreenshot(driver, "MobileSendText_Success_" + elementName);
        } catch (Exception e) {
            captureScreenshot(driver, "MobileSendText_Failed_" + elementName);
            throw new RuntimeException("❌ Failed to send text to mobile element " + elementName + ": " + e.getMessage());
        }
    }

    public static void sendText(AppiumDriver driver, By locator, String elementName, String value) {
        sendTextToMobileElement(driver, locator, elementName, value);
    }

    public static String getMobileText(AppiumDriver driver, By locator, String elementName) {
        try {
            WebElement element = findMobileElement(driver, locator, elementName);
            return element.getText();
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to get text from mobile element " + elementName + ": " + e.getMessage());
        }
    }

    public static String getText(AppiumDriver driver, By locator, String elementName) {
        return getMobileText(driver, locator, elementName);
    }

    public static boolean isMobileElementDisplayed(AppiumDriver driver, By locator) {
        try {
            return findMobileElement(driver, locator, "Mobile Element").isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static boolean isElementDisplayed(AppiumDriver driver, By locator) {
        return isMobileElementDisplayed(driver, locator);
    }

    public static void waitForMobileElement(AppiumDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void waitForElement(AppiumDriver driver, By locator) {
        waitForMobileElement(driver, locator);
    }

    public static void navigate(WebDriver driver, String url) {
        try {
            System.out.println("➡️ Navigating to: " + url);
            driver.navigate().to(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
            wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState").equals("complete"));
            System.out.println("✅ Navigation successful");
            captureScreenshot(driver, "Navigation_Success");
        } catch (Exception e) {
            captureScreenshot(driver, "Navigation_Failed");
            throw new RuntimeException("❌ Navigation failed to: " + url + ", " + e.getMessage());
        }
    }

    public static void clickElement(WebDriver driver, By locator, String elementName) {
        try {
            System.out.println("🖱️ Clicking: " + elementName);
            WebElement element = findElement(driver, locator, elementName);
            element.click();
            System.out.println("✅ " + elementName + " clicked successfully");
            captureScreenshot(driver, "Click_Success_" + elementName);
        } catch (Exception e) {
            captureScreenshot(driver, "Click_Failed_" + elementName);
            throw new RuntimeException("❌ Click failed on " + elementName + ": " + e.getMessage());
        }
    }

    public static void sendText(WebDriver driver, By locator, String elementName, String value) {
        try {
            System.out.println("📝 Sending text to: " + elementName);
            WebElement element = findElement(driver, locator, elementName);
            element.clear();
            element.sendKeys(value);
            System.out.println("✅ Text sent to " + elementName);
            captureScreenshot(driver, "SendText_Success_" + elementName);
        } catch (Exception e) {
            captureScreenshot(driver, "SendText_Failed_" + elementName);
            throw new RuntimeException("❌ Failed to send text to " + elementName + ": " + e.getMessage());
        }
    }

    public static String getText(WebDriver driver, By locator, String elementName) {
        try {
            WebElement element = findElement(driver, locator, elementName);
            return element.getText();
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to get text from " + elementName + ": " + e.getMessage());
        }
    }

    public static void log(String message) {
        System.out.println(message);
        try {
            Allure.step(message);
        } catch (Exception ignored) {}
    }

    public static boolean isElementDisplayed(WebDriver driver, By locator) {
        try {
            return findElement(driver, locator, "Element").isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static void waitForElement(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static void scrollToElement(WebDriver driver, WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            Thread.sleep(500);
        } catch (Exception e) {
            System.out.println("⚠️ Failed to scroll to element: " + e.getMessage());
        }
    }

    public static <T> T retry(RetryableAction<T> action, int maxAttempts) throws Exception {
        Exception lastException = null;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                return action.execute();
            } catch (Exception e) {
                lastException = e;
                System.out.println("⚠️ Retry attempt " + (i + 1) + "/" + maxAttempts);
                if (i < maxAttempts - 1) Thread.sleep(500);
            }
        }
        throw lastException;
    }

    public static void captureScreenshot(WebDriver driver, String fileName) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String screenshotPath = "screenshots/" + timestamp + "_" + fileName + ".png";
            File destFile = new File(screenshotPath);
            destFile.getParentFile().mkdirs();
            org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
            try (InputStream inputStream = new FileInputStream(destFile)) {
                Allure.addAttachment(fileName, "image/png", inputStream, ".png");
            }
            System.out.println("📸 Screenshot saved: " + screenshotPath);
        } catch (Exception e) {
            System.out.println("❌ Failed to capture screenshot: " + e.getMessage());
        }
    }

    public static void waitForPageLoad(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    public static void acceptAlert(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
    }

    public static void dismissAlert(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.dismiss();
    }

    @FunctionalInterface
    public interface RetryableAction<T> {
        T execute() throws Exception;
    }
}