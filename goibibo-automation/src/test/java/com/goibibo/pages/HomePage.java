package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * HomePage - Page Object for the Goibibo home page navigation.
 */
public class HomePage {

    private WebDriver driver;

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Opens the Goibibo website and waits for it to load.
     */
    public void openWebsite() {
        driver.get("https://www.goibibo.com");
        WaitUtils.hardWait(3000);
    }

    /**
     * Navigates directly to Cabs page via URL.
     */
    public void clickCabs() {
        driver.get("https://www.goibibo.com/cars/");
        WaitUtils.hardWait(3000);
    }

    /**
     * Navigates directly to Hotels page via URL.
     */
    public void clickHotels() {
        driver.get("https://www.goibibo.com/hotels/");
        WaitUtils.hardWait(3000);
    }

    /**
     * Clicks the Login / Signup button.
     * Uses a simple, direct locator: the span text "Login / Signup".
     */
    public void clickLoginSignup() {
        By loginBtn = By.xpath("//span[contains(text(),'Login')]");
        WaitUtils.waitForElementClickable(driver, loginBtn).click();
        WaitUtils.hardWait(1500);
    }
}
