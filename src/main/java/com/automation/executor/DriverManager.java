package com.automation.executor;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.appium.java_client.android.AndroidDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverManager {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    /**
     * Initialize WebDriver based on browser type
     */
    public static WebDriver getDriver(String browserName) {
        if (driver.get() == null) {
            driver.set(createDriver(browserName.toLowerCase()));
        }
        return driver.get();
    }

    /**
     * Create WebDriver instance
     */
    private static WebDriver createDriver(String browserName) {
        try {
            switch (browserName) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--start-maximized");
                    chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                    chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                    chromeOptions.setExperimentalOption("useAutomationExtension", false);
                    return new ChromeDriver(chromeOptions);

                case "mobile":
                case "mobilechrome":
                case "chrome-mobile":
                    throw new IllegalArgumentException("Unsupported browser option: " + browserName + ". WebDriver initialization is only supported for desktop browsers.");

                case "saucelabmobile":
                case "sauce-mobile":
                case "sauce mobile": {
                    ChromeOptions browserOptions = new ChromeOptions();
                    browserOptions.setPlatformName("Windows 11");
                    browserOptions.setBrowserVersion("latest");

                    Map<String, Object> sauceOptions = new HashMap<>();
                    sauceOptions.put("username", System.getenv().getOrDefault("SAUCE_USERNAME", "oauth-shilluchimmu-91931"));
                    sauceOptions.put("accessKey", System.getenv().getOrDefault("SAUCE_ACCESS_KEY", "bb2cc135-6036-4e2a-9e33-7036a381971d"));
                    sauceOptions.put("build", System.getenv().getOrDefault("SAUCE_BUILD", "selenium-build-GLMJZ"));
                    sauceOptions.put("name", System.getenv().getOrDefault("SAUCE_TEST_NAME", "mobileautomation test"));
                    browserOptions.setCapability("sauce:options", sauceOptions);

                    URL sauceUrl = new URL("https://ondemand.us-west-1.saucelabs.com:443/wd/hub");
                    return new RemoteWebDriver(sauceUrl, browserOptions);
                }
                case "sauce android": {
                    MutableCapabilities caps = new MutableCapabilities();
                    caps.setCapability("platformName", "Android");
                    caps.setCapability("appium:app", "storage:filename=Android-MyDemoAppRN.1.3.0.build-244.apk");
                    caps.setCapability("appium:deviceName", "Android GoogleAPI Emulator");
                    caps.setCapability("appium:platformVersion", "12.0");
                    caps.setCapability("appium:automationName", "UiAutomator2");
                    MutableCapabilities sauceOptions = new MutableCapabilities();
                    sauceOptions.setCapability("username", "oauth-shilluchimmu-91931");
                    sauceOptions.setCapability("accessKey", "bb2cc135-6036-4e2a-9e33-7036a381971d");
                    sauceOptions.setCapability("build", "appium-build-S1M7W");
                    sauceOptions.setCapability("name", "<your test name>");
                    sauceOptions.setCapability("deviceOrientation", "PORTRAIT");
                    caps.setCapability("sauce:options", sauceOptions);
                    URL url = new URL("https://ondemand.us-west-1.saucelabs.com:443/wd/hub");
                    return new AndroidDriver(url, (org.openqa.selenium.Capabilities) caps);
                }

                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--start-maximized");
                    return new FirefoxDriver(firefoxOptions);

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--start-maximized");
                    return new EdgeDriver(edgeOptions);

                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browserName);
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize WebDriver: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Get current WebDriver instance
     */
    public static WebDriver get() {
        return driver.get();
    }

    /**
     * Quit WebDriver and clear ThreadLocal
     */
    public static void quit() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }

    /**
     * Clear ThreadLocal
     */
    public static void removeDriver() {
        driver.remove();
    }
}
