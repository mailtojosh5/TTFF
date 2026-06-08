package com.automation.listeners;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import com.automation.utilities.CommonFunctions;

/**
 * InvokedMethod Listener - Runs after all lifecycle methods (@Before/@After)
 */
public class InvokedMethodListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult result) {
        // Not needed for this use case
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        System.out.println("📊 [afterInvocation] Method: " + method.getTestMethod().getMethodName() 
                + ", isTestMethod: " + method.isTestMethod() 
                + ", status: " + result.getStatus()
                + ", hasFailure: " + (CommonFunctions.getLastVerificationFailure() != null));
        
        // Check if this is a @Test method (not a setup/teardown method)
        if (method.isTestMethod() && result.getStatus() == ITestResult.SUCCESS) {
            // Check if there were verification failures that were caught in @AfterMethod
            Throwable verificationFailure = CommonFunctions.getLastVerificationFailure();
            
            if (verificationFailure != null) {
                System.out.println("❌ [afterInvocation] Marking test as FAILED due to verification failures");
                result.setStatus(ITestResult.FAILURE);
                result.setThrowable(verificationFailure);
                CommonFunctions.clearLastVerificationFailure();
            }
        }
    }
}
