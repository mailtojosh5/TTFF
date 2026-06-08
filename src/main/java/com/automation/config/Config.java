package com.automation.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration loader for reading properties from config file
 */
public class Config {

    private static Properties properties;

    static {
        loadProperties();
    }

    /**
     * Load properties from config.properties file
     */
    private static void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("⚠️ Config file not found, using defaults");
        }
    }

    /**
     * Get property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key, getDefault(key));
    }

    /**
     * Get property as integer
     */
    public static int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    /**
     * Get property as boolean
     */
    public static boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    /**
     * Default values
     */
    private static String getDefault(String key) {
        switch (key) {
            case "browser":
                return "chrome";
            case "headless":
                return "false";
            case "timeout":
                return "30";
            case "base.url":
                return "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
            case "excel.path":
                return "tests/BusinessFlow.xlsx";
            default:
                return "";
        }
    }
}
