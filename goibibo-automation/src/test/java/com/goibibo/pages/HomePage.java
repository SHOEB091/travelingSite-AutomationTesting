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

    /**
     * Navigates to the Goibibo home page.
     */
    public void openWebsite() {
        driver.get("https://www.goibibo.com");
        WaitUtils.hardWait(3000);
    }

    /**
     * Clicks the Hotels link from the home page navigation bar.
     * Uses the nav link rather than direct URL so the page context is correct.
     */
    public void clickHotels() {
        // Navigate from home page — click the Hotels nav tab
        driver.get("https://www.goibibo.com");
        WaitUtils.hardWait(2000);

        By hotelsNav = By.xpath(
                "//a[contains(@href,'hotel') and (contains(text(),'Hotel') or .//span[contains(text(),'Hotel')])] | " +
                "//span[normalize-space(text())='Hotels'] | " +
                "//li//a[contains(text(),'Hotels')]"
        );
        try {
            WebElement hotelsLink = wait.until(ExpectedConditions.elementToBeClickable(hotelsNav));
            hotelsLink.click();
            WaitUtils.hardWait(3000);
            System.out.println("Clicked Hotels nav link, URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("Hotels nav click failed, navigating by URL: " + e.getMessage());
            driver.get("https://www.goibibo.com/hotels/");
            WaitUtils.hardWait(3000);
        }
    }

    /**
     * Clicks the Login / Signup button to open the dropdown.
     * Tries multiple locator strategies + JavaScript click fallback.
     */
    public void clickLoginSignup() {
        // Strategy 1: by partial link text / inner span text
        By[] loginLocators = {
            By.xpath("//div[contains(@class,'userLogin') or contains(@class,'userIcon') or contains(@class,'login-wrapper')]"),
            By.xpath("//div[contains(@class,'header')]//button[contains(text(),'Login') or contains(text(),'Sign')]"),
            By.xpath("//*[@data-cy='login-btn' or @data-testid='login-btn' or @id='login-btn']"),
            By.xpath("//div[@class[contains(.,'user')]]//span | //div[@class[contains(.,'signIn')]]"),
            By.xpath("//nav//*[contains(text(),'Login') or contains(text(),'Sign in')]"),
            By.xpath("//*[normalize-space(text())='Login / Signup']"),
            By.xpath("//*[contains(text(),'Login') and contains(text(),'Signup')]")
        };

        for (By locator : loginLocators) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));
                WebElement loginBtn = shortWait.until(ExpectedConditions.elementToBeClickable(locator));
                loginBtn.click();
                WaitUtils.hardWait(1500);
                System.out.println("Clicked login via: " + locator);
                return;
            } catch (Exception e) {
                // try next locator
            }
        }

        // Strategy 2: JavaScript click — find first visible element with 'Login' text
        System.out.println("All XPath strategies failed, trying JavaScript to find Login button");
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "var els = document.querySelectorAll('*');" +
                "for(var i=0; i<els.length; i++){" +
                "  var t = els[i].innerText;" +
                "  if(t && (t.trim()==='Login / Signup' || t.trim()==='Login/Signup' || t.trim()==='Login') && els[i].offsetWidth > 0){" +
                "    els[i].click();" +
                "    break;" +
                "  }" +
                "}"
            );
            WaitUtils.hardWait(1500);
            System.out.println("Login button clicked via JavaScript text search");
        } catch (Exception e) {
            System.out.println("JavaScript Login click also failed: " + e.getMessage());
        }
    }

    /**
     * Returns a list of all visible text values in the currently open dropdown.
     */
    public List<WebElement> getDropdownItems() {
        By dropdownItems = By.xpath(
                "//div[contains(@class,'dropdown') or contains(@class,'menu') or contains(@class,'popup')]//a | " +
                "//ul[contains(@class,'drop')]//li"
        );
        return driver.findElements(dropdownItems);
    }
}
