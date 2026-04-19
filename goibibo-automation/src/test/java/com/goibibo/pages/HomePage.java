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
 * HomePage - Page Object for Goibibo home page navigation.
 */
public class HomePage {

    private WebDriver driver;
    private WebDriverWait wait;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void openWebsite() {
        driver.get("https://www.goibibo.com");
        WaitUtils.hardWait(3000);
    }

    /**
     * Navigates to the Hotels page via direct URL.
     * Fresh browser session ensures Goibibo serves the full page (not "200 - OK").
     */
    public void clickHotels() {
        driver.get("https://www.goibibo.com/hotels/");
        WaitUtils.hardWait(4000);
        System.out.println("Hotels page loaded: " + driver.getCurrentUrl());
    }

    /**
     * Clicks the Login / Signup button in the TOP-RIGHT header area.
     *
     * Problem from previous run: JavaScript text search found "Login Now & Save More"
     * links inside hotel/flight cards instead of the actual header button.
     *
     * Fix: Target only the header area — specifically the element near "Manage Booking".
     * The Goibibo header always shows "Manage Booking My Trips" to the LEFT of
     * "Login / Signup" — we use that anchor to find the correct button.
     */
    public void clickLoginSignup() {
        // Strategy 1: Find the element NEXT TO "Manage Booking My Trips" in the header
        // This uniquely identifies the Login/Signup button (not page content)
        By[] headerLoginLocators = {
            // Sibling of "Manage Booking" / "My Trips" container
            By.xpath("//div[.//span[contains(text(),'My Trips')] or .//text()[contains(.,'My Trips')]]/following-sibling::*[1]"),
            // Button/div with person icon and Login text specifically in header
            By.xpath("//div[contains(@class,'header')]//div[contains(text(),'Login') or contains(text(),'Signup')]"),
            By.xpath("//div[contains(@class,'header')]//span[contains(text(),'Login')]"),
            // Goibibo uses a structure where login is an anchor/div at top right
            By.xpath("//div[@class[contains(.,'user') or contains(.,'login') or contains(.,'auth')]]" +
                     "[not(ancestor::div[contains(@class,'result') or contains(@class,'listing') or contains(@class,'card')])]"),
            // Last anchor/button in the nav area that mentions Login
            By.xpath("(//nav//a[contains(text(),'Login')] | //nav//button[contains(text(),'Login')])[last()]")
        };

        for (By locator : headerLoginLocators) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement btn = shortWait.until(ExpectedConditions.elementToBeClickable(locator));
                btn.click();
                WaitUtils.hardWait(1500);
                System.out.println("Header Login button clicked via: " + locator);
                return;
            } catch (Exception e) {
                // try next strategy
            }
        }

        // Strategy 2: JavaScript — find elements with Login text but ONLY in the header/top area
        // Restricts to elements with y-position less than 150px (top of page)
        System.out.println("XPath strategies failed, using header-restricted JavaScript");
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "var els = document.querySelectorAll('a, button, div, span');" +
                "for(var i=0; i<els.length; i++){" +
                "  var rect = els[i].getBoundingClientRect();" +
                "  var t = els[i].innerText ? els[i].innerText.trim() : '';" +
                "  var isHeader = rect.top < 150 && rect.top > 0 && rect.width > 0;" +
                "  var hasLogin = t.indexOf('Login') !== -1 || t.indexOf('Signup') !== -1 || t.indexOf('Sign Up') !== -1;" +
                "  if(isHeader && hasLogin){" +
                "    console.log('Clicking header login element:', els[i].tagName, t);" +
                "    els[i].click();" +
                "    break;" +
                "  }" +
                "}"
            );
            WaitUtils.hardWait(1500);
            System.out.println("Header Login button clicked via JavaScript (top-150px restriction)");
        } catch (Exception e) {
            System.out.println("JavaScript Login click failed: " + e.getMessage());
        }
    }
}
