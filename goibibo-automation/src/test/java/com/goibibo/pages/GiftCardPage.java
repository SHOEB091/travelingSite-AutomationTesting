package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * GiftCardPage - Page Object for Goibibo Gift Cards.
 *
 * After clicking the Login/Signup header button, a dropdown appears with:
 *   goTribe | Offers | Student Go Pass | My Trips | goCash | Gift Cards
 *
 * This class finds "Gift Cards" in that dropdown and clicks it.
 */
public class GiftCardPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public GiftCardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Checks if the "Gift Cards" option is visible in the login dropdown.
     * Waits up to 5 seconds for it to appear after the dropdown opens.
     */
    public boolean isGiftCardOptionVisible() {
        try {
            WaitUtils.hardWait(1500);

            // Try to wait for Gift Cards text to appear in the page
            By giftCardLocator = By.xpath(
                "//a[contains(normalize-space(.),'Gift Card')] | " +
                "//span[contains(normalize-space(.),'Gift Card')] | " +
                "//div[contains(normalize-space(.),'Gift Card')][not(contains(@class,'content'))]"
            );

            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(giftCardLocator));

            List<WebElement> elements = driver.findElements(giftCardLocator);
            for (WebElement el : elements) {
                if (el.isDisplayed()) {
                    System.out.println("Gift Cards option found: " + el.getText());
                    return true;
                }
            }
            System.out.println("Gift Cards not visible in dropdown");
            return false;
        } catch (Exception e) {
            System.out.println("isGiftCardOptionVisible error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clicks the Gift Cards link in the dropdown menu.
     */
    public void clickGiftCards() {
        By[] locators = {
            By.xpath("//a[contains(normalize-space(.),'Gift Card')]"),
            By.xpath("//span[contains(normalize-space(.),'Gift Card')]/ancestor::a[1]"),
            By.xpath("//*[contains(normalize-space(.),'Gift Card')][not(contains(@class,'description') or contains(@class,'text'))]")
        };

        for (By locator : locators) {
            try {
                List<WebElement> elements = driver.findElements(locator);
                for (WebElement el : elements) {
                    if (el.isDisplayed()) {
                        el.click();
                        WaitUtils.hardWait(3000);
                        System.out.println("Clicked Gift Cards. URL: " + driver.getCurrentUrl());
                        return;
                    }
                }
            } catch (Exception e) {
                // try next locator
            }
        }

        // JS fallback — click the Gift Cards element
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "var els = document.querySelectorAll('a, button, span, div');" +
                "for(var i=0; i<els.length; i++){" +
                "  var t = els[i].innerText ? els[i].innerText.trim() : '';" +
                "  if((t === 'Gift Cards' || t === 'Gift Card') && els[i].offsetWidth > 0){" +
                "    els[i].click();" +
                "    console.log('Gift Cards clicked via JS');" +
                "    break;" +
                "  }" +
                "}"
            );
            WaitUtils.hardWait(3000);
        } catch (Exception e) {
            System.out.println("Gift Cards JS click failed: " + e.getMessage());
        }
    }

    /**
     * Returns true if the Gift Cards page is loaded (checks URL or page heading).
     */
    public boolean isGiftCardPageDisplayed() {
        WaitUtils.hardWait(2000);
        String url = driver.getCurrentUrl();
        System.out.println("Gift Card page URL: " + url);

        if (url.contains("gift") || url.contains("giftcard")) {
            return true;
        }

        // Check page heading
        try {
            List<WebElement> headings = driver.findElements(
                By.xpath("//h1[contains(text(),'Gift')] | //h2[contains(text(),'Gift')] | " +
                         "//*[contains(@class,'title') and contains(text(),'Gift')]")
            );
            for (WebElement h : headings) {
                if (h.isDisplayed()) return true;
            }
        } catch (Exception ignore) {}

        return false;
    }
}
