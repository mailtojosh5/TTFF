package com.automation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry Analyzer for TestNG
 * Handles automatic test retry on failure
 * Allows Allure to properly track retries
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY_COUNT = 2; // 2 retries = 3 total attempts (initial + 2)
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        Throwable throwable = result.getThrowable();
        while (throwable != null) {
            if (throwable.getMessage() != null && throwable.getMessage().contains("Verification failures occurred")) {
                System.out.println("❌ Not retrying test due to verification failure: " + result.getName());
                return false;
            }
            throwable = throwable.getCause();
        }

        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.println("\n🔄 Retrying test: " + result.getName() + " | Attempt: " + (retryCount + 1) + "/" + (MAX_RETRY_COUNT + 1));
            return true; // Tell TestNG to retry the test
        }
        return false; // No more retries
    }

    public int getRetryCount() {
        return retryCount;
    }
}
