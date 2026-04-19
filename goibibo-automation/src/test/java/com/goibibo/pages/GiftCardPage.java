package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * GiftCardPage - Page Object for the Goibibo Gift Cards page.
 * Navigates to Gift Cards from the Login/Signup dropdown menu.
 */
public class GiftCardPage {

    private WebDriver driver;

    // Locators
    private By giftCardMenuOption = By.xpath("//*[contains(text(),'Gift Card') or contains(text(),'Gift Cards') or contains(text(),'GiftCard')]");
    private By giftCardPageHeading = By.xpath("//*[contains(text(),'Gift Card') or contains(text(),'gift card')]");

    public GiftCardPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Checks if the Gift Cards option is visible in the dropdown menu.
     */
    public boolean isGiftCardOptionVisible() {
        try {
            WaitUtils.hardWait(1000);
            List<WebElement> giftCardElements = driver.findElements(giftCardMenuOption);
            for (WebElement el : giftCardElements) {
                if (el.isDisplayed()) {
                    System.out.println("Gift Card option found in menu");
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Gift Card option check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clicks on the Gift Cards option in the dropdown.
     */
    public void clickGiftCards() {
        try {
            List<WebElement> giftCardElements = driver.findElements(giftCardMenuOption);
            for (WebElement el : giftCardElements) {
                if (el.isDisplayed()) {
                    el.click();
                    WaitUtils.hardWait(3000);
                    System.out.println("Clicked on Gift Cards");
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Clicking Gift Card failed: " + e.getMessage());
        }
    }

    /**
     * Returns true if the Gift Cards page is loaded.
     */
    public boolean isGiftCardPageDisplayed() {
        try {
            WaitUtils.hardWait(2000);
            String url = driver.getCurrentUrl();
            if (url.contains("gift") || url.contains("giftcard")) {
                return true;
            }
            List<WebElement> headings = driver.findElements(giftCardPageHeading);
            for (WebElement h : headings) {
                if (h.isDisplayed()) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Gift card page check failed: " + e.getMessage());
            return false;
        }
    }
}
