package com.automation.pages;

import org.openqa.selenium.By;

public class AdminPage {
    
    // Locators
    public static final By ADMIN_TAB = By.xpath("(//a[contains(@href, 'admin') and contains(normalize-space(.), 'Admin')] | //span[contains(normalize-space(.), 'Admin')])[1]");
    public static final By ADD_BUTTON = By.xpath("(//button[contains(normalize-space(.), 'Add')] | //span[contains(normalize-space(.), 'Add')])[1]");
    public static final By USER_ROLE = By.xpath("//*[@id='app']/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[1]/div/div[2]/div/div/div[2]/i");
    public static final By EMPLOYEE_NAME = By.xpath("//*[@id='app']/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[2]/div/div[2]/div/div/input");
    public static final By STATUS = By.xpath("//*[@id='app']/div[1]/div[2]/div[2]/div/div/form/div[1]/div/div[3]/div/div[2]/div/div/div[2]/i");
    public static final By PASSWORD = By.xpath("//*[@id='app']/div[1]/div[2]/div[2]/div/div/form/div[2]/div/div[1]/div/div[2]/input");
    public static final By SAVE_BUTTON = By.xpath("//button[contains(text(), 'Save')]");
}
