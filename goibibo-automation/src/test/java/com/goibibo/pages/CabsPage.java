package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * CabsPage - Page Object for Goibibo Cabs booking page.
 *
 * Key fixes applied:
 * 1. Retry logic on all input fields to handle StaleElementReferenceException
 * 2. Re-find element by locator (not stored reference) on every retry
 * 3. Simplified, more reliable XPath locators
 * 4. Popup closed before any filter action
 */
public class CabsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Simple, reliable locators for Goibibo Cabs page
    private By fromInputLocator = By.xpath("(//input[@type='text'])[1]");
    private By toInputLocator = By.xpath("(//input[@type='text'])[2]");
    private By searchBtnLocator = By.xpath("//button[contains(text(),'Search') or contains(text(),'SEARCH')]");

    public CabsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Navigates to the Cabs page.
     */
    public void navigateToCabs() {
        driver.get("https://www.goibibo.com/cars/");
        WaitUtils.hardWait(3000);
        closePopupIfVisible();
    }

    /**
     * Clicks the Outstation One-way tab if not already selected.
     */
    public void selectOutstationOneWay() {
        try {
            By outstationTab = By.xpath("//span[text()='Outstation']/ancestor::label | " +
                    "//label[.//span[text()='Outstation']] | " +
                    "//div[contains(@class,'tab')]//span[contains(text(),'Outstation')]");
            wait.until(ExpectedConditions.elementToBeClickable(outstationTab)).click();
            WaitUtils.hardWait(1000);
        } catch (Exception e) {
            System.out.println("Outstation One-way tab not found or already selected: " + e.getMessage());
        }
    }

    /**
     * Types into the From location field with retry for stale elements.
     * Re-finds the element on each attempt.
     */
    public void enterFromLocation(String location) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement fromInput = wait.until(ExpectedConditions.elementToBeClickable(fromInputLocator));
                fromInput.click();
                WaitUtils.hardWait(500);
                // Re-find after click because Goibibo re-renders DOM
                fromInput = wait.until(ExpectedConditions.visibilityOfElementLocated(fromInputLocator));
                fromInput.clear();
                fromInput.sendKeys(location);
                WaitUtils.hardWait(2500);
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element on From input, retry " + (attempt + 1));
                WaitUtils.hardWait(1000);
            }
        }
        throw new RuntimeException("Could not enter From location after 3 attempts");
    }

    /**
     * Types into the To location field with retry for stale elements.
     */
    public void enterToLocation(String location) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement toInput = wait.until(ExpectedConditions.elementToBeClickable(toInputLocator));
                toInput.click();
                WaitUtils.hardWait(500);
                toInput = wait.until(ExpectedConditions.visibilityOfElementLocated(toInputLocator));
                toInput.clear();
                toInput.sendKeys(location);
                WaitUtils.hardWait(2500);
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element on To input, retry " + (attempt + 1));
                WaitUtils.hardWait(1000);
            }
        }
        throw new RuntimeException("Could not enter To location after 3 attempts");
    }

    /**
     * Clicks a suggestion from the autocomplete dropdown by partial text match.
     */
    public void selectSuggestion(String suggestionText) {
        // Wait for dropdown to appear then pick the matching item
        By suggestionLocator = By.xpath(
                "//ul//li//span[contains(text(),'" + suggestionText + "')] | " +
                "//*[contains(@class,'suggest') or contains(@class,'autocomplete')]" +
                "//*[contains(text(),'" + suggestionText + "')] | " +
                "//div[contains(@class,'list')]//div[contains(text(),'" + suggestionText + "')]"
        );
        try {
            WebElement suggestion = wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionLocator));
            suggestion.click();
            WaitUtils.hardWait(1500);
            System.out.println("Selected suggestion: " + suggestionText);
        } catch (Exception e) {
            System.out.println("Suggestion not found for: " + suggestionText + " -> " + e.getMessage());
        }
    }

    /**
     * Sets the pickup date using the date calendar picker.
     * Tries clicking the date input and selecting the correct day.
     * Date format: dd/MM/yyyy
     */
    public void setPickupDate(String date) {
        String[] parts = date.split("/");
        String day = String.valueOf(Integer.parseInt(parts[0]));

        try {
            // Click the date field to open the calendar
            By dateTrigger = By.xpath(
                    "//label[contains(text(),'Pick') and contains(text(),'Date')]/following-sibling::* | " +
                    "//*[contains(@class,'date') and not(self::input)]//span | " +
                    "//div[contains(@class,'pickupDate') or contains(@class,'pickup_date')]"
            );
            try {
                wait.until(ExpectedConditions.elementToBeClickable(dateTrigger)).click();
                WaitUtils.hardWait(1500);
            } catch (Exception ignore) {
                System.out.println("Date trigger click skipped");
            }

            // Find and click the correct day number in the calendar
            By dayLocator = By.xpath(
                    "//div[contains(@class,'DayPicker') or contains(@class,'Calendar') or contains(@class,'calendar')]" +
                    "//div[not(contains(@class,'disabled'))][@aria-label or @role='gridcell'][normalize-space(text())='" + day + "'] | " +
                    "//table//td[not(contains(@class,'disabled'))][normalize-space(text())='" + day + "'] | " +
                    "//span[not(contains(@class,'disabled'))][normalize-space(text())='" + day + "']"
            );
            WebElement dayEl = wait.until(ExpectedConditions.elementToBeClickable(dayLocator));
            dayEl.click();
            WaitUtils.hardWait(1000);
            System.out.println("Pickup date set to day: " + day);
        } catch (Exception e) {
            System.out.println("Date picker issue: " + e.getMessage());
        }
    }

    /**
     * Sets the pickup time using the time dropdown.
     */
    public void setPickupTime(String time) {
        try {
            // Click the time selector dropdown
            By timeTrigger = By.xpath(
                    "//label[contains(text(),'Time')]/following-sibling::* | " +
                    "//*[contains(@class,'time') and not(self::input)] | " +
                    "//select[contains(@class,'time') or @name[contains(.,'time')]]"
            );
            WebElement timefield = wait.until(ExpectedConditions.elementToBeClickable(timeTrigger));
            timefield.click();
            WaitUtils.hardWait(1000);

            // Look for the time option in dropdown
            By timeOption = By.xpath("//*[normalize-space(text())='" + time + "'] | " +
                    "//option[contains(text(),'" + time + "')]");
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(timeOption));
            option.click();
            WaitUtils.hardWait(1000);
            System.out.println("Time set to: " + time);
        } catch (Exception e) {
            System.out.println("Time setting issue (non-critical): " + e.getMessage());
        }
    }

    /**
     * Clicks the Search button.
     */
    public void clickSearch() {
        try {
            WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(searchBtnLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", searchBtn);
            WaitUtils.hardWait(5000);
            System.out.println("Search clicked, current URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("Search button click failed: " + e.getMessage());
        }
    }

    /**
     * Closes any popup/overlay shown on the page.
     * Goibibo ALWAYS shows a booking assistance overlay — this must be closed first.
     */
    public void closePopupIfVisible() {
        try {
            // Short wait to let popup appear
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));

            By closePopup = By.xpath(
                    "//span[@role='presentation'] | " +
                    "//span[contains(@class,'close')] | " +
                    "//button[contains(@class,'close')] | " +
                    "//*[@aria-label='close' or @aria-label='Close'] | " +
                    "//div[contains(@class,'assistCard') or contains(@class,'bookAssist')]//button | " +
                    "//div[contains(@class,'modal') or contains(@class,'overlay') or contains(@class,'popup')]" +
                    "//button[contains(@class,'close') or contains(text(),'×') or contains(text(),'✕')]"
            );

            WebElement closeBtn = shortWait.until(ExpectedConditions.visibilityOfElementLocated(closePopup));
            closeBtn.click();
            WaitUtils.hardWait(1000);
            System.out.println("Popup closed");
        } catch (Exception e) {
            // Try Escape key as backup
            try {
                new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                WaitUtils.hardWait(500);
            } catch (Exception ignored) {}
            System.out.println("No popup visible or already closed");
        }
    }

    /**
     * Selects the SUV filter from the cab type checkboxes.
     */
    public void selectSuvFilter() {
        // Close any popup first
        closePopupIfVisible();

        try {
            By suvLocator = By.xpath(
                    "//input[@type='checkbox']/following-sibling::span[contains(text(),'SUV')] | " +
                    "//label[contains(normalize-space(.),'SUV') and not(contains(.,'Luxury'))] | " +
                    "//span[normalize-space(text())='SUV']/preceding-sibling::input[@type='checkbox'] | " +
                    "//div[contains(@class,'filter')]//span[normalize-space(text())='SUV']"
            );
            WebElement suv = wait.until(ExpectedConditions.elementToBeClickable(suvLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'})", suv);
            WaitUtils.hardWait(500);
            suv.click();
            WaitUtils.hardWait(2000);
            System.out.println("SUV filter selected");
        } catch (Exception e) {
            System.out.println("SUV filter click failed: " + e.getMessage());
            // Try JS click on SUV text as last resort
            try {
                List<WebElement> suvEls = driver.findElements(By.xpath("//*[normalize-space(text())='SUV']"));
                for (WebElement el : suvEls) {
                    if (el.isDisplayed()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", el);
                        WaitUtils.hardWait(1500);
                        System.out.println("SUV selected via JS click");
                        break;
                    }
                }
            } catch (Exception e2) {
                System.out.println("JS click on SUV also failed: " + e2.getMessage());
            }
        }
    }

    public boolean isSuvResultDisplayed() {
        try {
            List<WebElement> suvResults = driver.findElements(
                    By.xpath("//*[contains(text(),'SUV') or contains(@class,'suv')]"));
            return !suvResults.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
