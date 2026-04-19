package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * GiftCardPage - Page Object for Goibibo Gift Cards.
 * Navigates via Login/Signup dropdown that shows Gift Cards option.
 */
public class GiftCardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Simple, reliable locator for Gift Cards text in the dropdown
    private By giftCardLinkLocator = By.xpath(
            "//a[contains(text(),'Gift Card') or contains(text(),'Gift Cards')] | " +
            "//*[contains(text(),'Gift Cards')]"
    );

    public GiftCardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Checks if the Gift Cards option is visible in the dropdown.
     */
    public boolean isGiftCardOptionVisible() {
        try {
            WaitUtils.hardWait(1000);
            List<WebElement> elements = driver.findElements(giftCardLinkLocator);
            for (WebElement el : elements) {
                if (el.isDisplayed()) {
                    System.out.println("Gift Cards found in menu: " + el.getText());
                    return true;
                }
            }
            System.out.println("Gift Cards option not found in menu");
            return false;
        } catch (Exception e) {
            System.out.println("isGiftCardOptionVisible error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clicks the Gift Cards link.
     */
    public void clickGiftCards() {
        try {
            List<WebElement> elements = driver.findElements(giftCardLinkLocator);
            for (WebElement el : elements) {
                if (el.isDisplayed()) {
                    el.click();
                    WaitUtils.hardWait(3000);
                    System.out.println("Clicked Gift Cards");
                    return;
                }
            }
            System.out.println("No visible Gift Cards link to click");
        } catch (Exception e) {
            System.out.println("clickGiftCards error: " + e.getMessage());
        }
    }

    /**
     * Returns true if the Gift Cards page is loaded.
     * Checks by URL or page heading.
     */
    public boolean isGiftCardPageDisplayed() {
        WaitUtils.hardWait(2000);
        String url = driver.getCurrentUrl();
        System.out.println("Gift card page check URL: " + url);
        if (url.contains("gift") || url.contains("giftcard") || url.contains("gift-card")) {
            return true;
        }
        // Also check by heading on page
        try {
            List<WebElement> headings = driver.findElements(
                    By.xpath("//*[contains(text(),'Gift Card') or contains(text(),'Gift cards')]"));
            for (WebElement h : headings) {
                if (h.isDisplayed()) return true;
            }
        } catch (Exception ignore) {}
        return false;
    }
}
