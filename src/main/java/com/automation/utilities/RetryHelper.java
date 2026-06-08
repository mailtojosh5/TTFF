package com.automation.utilities;

import org.openqa.selenium.WebDriver;

public class RetryHelper {

    /**
     * Execute action with retry logic
     */
    public static void executeWithRetry(String testCaseId, WebDriver driver, int maxRetries) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxRetries) {
            try {
                attempt++;
                System.out.println("➡️ Execution Attempt " + attempt + "/" + maxRetries);
                // The actual test execution will be called from the test class
                // This is just a framework for retry logic
                break;
            } catch (Exception e) {
                lastException = e;
                System.err.println("❌ Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Max retries exceeded for test: " + testCaseId, e);
                }
            }
        }
    }

    /**
     * Generic retry for any action
     */
    public static <T> T retryAction(RetryableAction<T> action, int maxRetries) throws Exception {
        Exception lastException = null;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                attempt++;
                System.out.println("➡️ Retry attempt " + attempt + "/" + maxRetries);
                return action.execute();
            } catch (Exception e) {
                lastException = e;
                System.err.println("❌ Attempt " + attempt + " failed: " + e.getMessage());
                if (attempt >= maxRetries) {
                    throw lastException;
                }
                Thread.sleep(1000); // Wait before retrying
            }
        }
        throw lastException;
    }

    @FunctionalInterface
    public interface RetryableAction<T> {
        T execute() throws Exception;
    }
}
