package com.automation.pages;

import org.openqa.selenium.By;

public class LogoutPage {
    
    // Locators
    public static final By LOGOUT_BUTTON = By.xpath("//a[contains(text(), 'Logout')]");
    public static final By PROFILE_BUTTON = By.xpath("//*[@id='app']/div[1]/div[1]/header/div[1]/div[3]/ul/li/span/i");
}
