package com.automation.pages;

import org.openqa.selenium.By;

public class LoginPage {
    
    public static final String URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    
    // Locators
    public static final By USERNAME_INPUT = By.xpath("//*[@name='username']");
    public static final By PASSWORD_INPUT = By.xpath("//*[@name='password']");
    public static final By LOGIN_BUTTON = By.xpath("//button[contains(@type, 'submit')]");
    
    // Alternative locators for resilience
    public static final By USERNAME_FIELD = By.name("username");
    public static final By PASSWORD_FIELD = By.name("password");
}
