package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * HomePage - Page Object for the Goibibo home page.
 * Contains all locators and methods for the main navigation.
 */
public class HomePage {

    private WebDriver driver;

    // Locators for navigation menu items
    private By cabsMenu = By.xpath("//a[contains(@href,'/cars') or contains(text(),'Cabs')]");
    private By hotelsMenu = By.xpath("//a[contains(@href,'/hotels') or contains(text(),'Hotels')]");
    private By loginSignupBtn = By.xpath("//div[contains(text(),'Login') or contains(text(),'Signup') or @class[contains(.,'loginBtn')]]//ancestor::div[@class[contains(.,'user')]] | //span[text()='Login / Signup'] | //div[contains(@class,'login')]//span[contains(text(),'Login')]");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Opens the Goibibo website.
     */
    public void openWebsite() {
        driver.get("https://www.goibibo.com");
        WaitUtils.hardWait(3000);
    }

    /**
     * Clicks on the Cabs navigation link.
     */
    public void clickCabs() {
        WebElement cabs = WaitUtils.waitForElementClickable(driver,
                By.xpath("//a[@href='/cars/' or @href='/cars'] | //span[text()='Cabs'] | //a[contains(@class,'') and contains(text(),'Cabs')]"));
        cabs.click();
        WaitUtils.hardWait(2000);
    }

    /**
     * Clicks on the Hotels navigation link.
     */
    public void clickHotels() {
        driver.get("https://www.goibibo.com/hotels/");
        WaitUtils.hardWait(3000);
    }

    /**
     * Clicks on Login / Signup button to open the dropdown.
     */
    public void clickLoginSignup() {
        WebElement loginBtn = WaitUtils.waitForElementClickable(driver,
                By.xpath("//*[contains(text(),'Login') and (contains(text(),'Signup') or contains(text(),'Sign'))]" +
                        " | //span[normalize-space()='Login / Signup']" +
                        " | //div[@class[contains(.,'signIn')]]" +
                        " | //div[contains(@class,'headerUser')]"));
        loginBtn.click();
        WaitUtils.hardWait(1500);
    }
}
