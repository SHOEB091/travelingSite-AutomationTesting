package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * CabsPage - Page Object for the Goibibo Cabs booking page.
 * Handles outstation one-way cab search from Delhi Cantt to Manali.
 */
public class CabsPage {

    private WebDriver driver;

    // Locators for Cabs page
    private By outstationOneWayTab = By.xpath("//label[contains(text(),'Outstation') and contains(text(),'One-way')] | //span[contains(text(),'Outstation') and contains(text(),'One-way')] | //div[contains(text(),'Outstation One-way')]");
    private By fromLocationInput = By.xpath("//div[contains(text(),'From')]/following-sibling::div//input | //input[@placeholder[contains(.,'From') or contains(.,'from')]] | //label[contains(text(),'From')]/following-sibling::input | //div[@class[contains(.,'source')]]//input");
    private By toLocationInput = By.xpath("//div[contains(text(),'To')]/following-sibling::div//input | //input[@placeholder[contains(.,'To') or contains(.,'to')]] | //label[contains(text(),'To')]/following-sibling::input | //div[@class[contains(.,'destination')]]//input");
    private By pickupDateInput = By.xpath("//label[contains(text(),'Pick-up Date') or contains(text(),'Pickup Date') or contains(text(),'Date')]/following-sibling::* | //input[@placeholder[contains(.,'Date')]]  | //div[contains(@class,'date')]//input");
    private By pickupTimeInput = By.xpath("//label[contains(text(),'Pick-up Time') or contains(text(),'Pickup Time') or contains(text(),'Time')]/following-sibling::* | //select[contains(@class,'time')] | //div[contains(@class,'time')]//input");
    private By searchBtn = By.xpath("//button[contains(text(),'Search') or contains(text(),'SEARCH')] | //input[@type='submit' and contains(@value,'Search')]");

    // Cab listing page locators
    private By closePopupBtn = By.xpath("//button[contains(@class,'close') or contains(@aria-label,'close') or contains(@aria-label,'Close')] | //span[@class[contains(.,'close')]] | //*[@id='close'] | //button[@class[contains(.,'modal')]]//span[text()='×'] | //*[contains(@class,'bookingAssist')]//button[contains(@class,'close') or contains(text(),'×')] | //div[contains(@class,'popup')]//button | //div[contains(@class,'modal')]//button[contains(@class,'close')]");
    private By suvCheckbox = By.xpath("//label[contains(text(),'SUV')] | //input[@type='checkbox']/following-sibling::*[contains(text(),'SUV')] | //span[text()='SUV']/preceding-sibling::input | //div[contains(@class,'cab') or contains(@class,'filter')]//label[contains(text(),'SUV')]");

    public CabsPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Navigates to the Cabs page directly.
     */
    public void navigateToCabs() {
        driver.get("https://www.goibibo.com/cars/");
        WaitUtils.hardWait(3000);
    }

    /**
     * Selects the Outstation One-way tab.
     */
    public void selectOutstationOneWay() {
        try {
            WebElement tab = WaitUtils.waitForElementClickable(driver, outstationOneWayTab);
            tab.click();
            WaitUtils.hardWait(1000);
        } catch (Exception e) {
            // May already be selected by default
            System.out.println("Outstation One-way may already be selected: " + e.getMessage());
        }
    }

    /**
     * Enters the from location and waits for suggestions.
     */
    public void enterFromLocation(String location) {
        WebElement fromInput = WaitUtils.waitForElementClickable(driver, fromLocationInput);
        fromInput.click();
        WaitUtils.hardWait(500);
        fromInput.clear();
        fromInput.sendKeys(location);
        WaitUtils.hardWait(2000);
    }

    /**
     * Clicks on a suggestion from the dropdown by matching text.
     */
    public void selectSuggestion(String suggestionText) {
        By suggestionLocator = By.xpath("//*[contains(text(),'" + suggestionText + "')]");
        WebElement suggestion = WaitUtils.waitForElementClickable(driver, suggestionLocator);
        suggestion.click();
        WaitUtils.hardWait(1500);
    }

    /**
     * Enters the to location.
     */
    public void enterToLocation(String location) {
        WebElement toInput = WaitUtils.waitForElementClickable(driver, toLocationInput);
        toInput.click();
        WaitUtils.hardWait(500);
        toInput.clear();
        toInput.sendKeys(location);
        WaitUtils.hardWait(2000);
    }

    /**
     * Sets the pickup date. Tries direct input first, then calendar picker.
     */
    public void setPickupDate(String date) {
        try {
            // Try clicking the date field first
            WebElement dateField = WaitUtils.waitForElementClickable(driver, pickupDateInput);
            dateField.click();
            WaitUtils.hardWait(1000);

            // Try to set the value using JS if it is an input
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = ''", dateField);
            dateField.sendKeys(date);
            dateField.sendKeys(Keys.TAB);
            WaitUtils.hardWait(1000);
        } catch (Exception e) {
            System.out.println("Date field interaction issue: " + e.getMessage());
            // Try clicking on the correct date in the calendar
            clickDateInCalendar(date);
        }
    }

    /**
     * Clicks on a specific date in the calendar widget.
     * Date format expected: dd/MM/yyyy
     */
    private void clickDateInCalendar(String date) {
        String[] parts = date.split("/");
        String day = String.valueOf(Integer.parseInt(parts[0]));

        By dayLocator = By.xpath("//div[contains(@class,'calendar') or contains(@class,'picker')]//td[text()='" + day + "'] | " +
                "//div[contains(@class,'DayPicker') or contains(@class,'day')]//div[text()='" + day + "'] | " +
                "//span[text()='" + day + "' and @class[contains(.,'day')]]");
        try {
            WebElement dayElement = WaitUtils.waitForElementClickable(driver, dayLocator);
            dayElement.click();
            WaitUtils.hardWait(1000);
        } catch (Exception e2) {
            System.out.println("Could not click date in calendar: " + e2.getMessage());
        }
    }

    /**
     * Sets the pickup time using the time dropdown.
     */
    public void setPickupTime(String time) {
        try {
            // Try to find a time selector element
            By timeSelector = By.xpath("//select[contains(@class,'time') or @name[contains(.,'time')]] | " +
                    "//div[contains(@class,'time')]//input | " +
                    "//span[contains(text(),'AM') or contains(text(),'PM')]/parent::*");
            WebElement timeField = WaitUtils.waitForElementClickable(driver, timeSelector);
            timeField.click();
            WaitUtils.hardWait(1000);

            // Look for the specific time option
            By timeOption = By.xpath("//*[contains(text(),'" + time + "')]");
            WebElement option = WaitUtils.waitForElementClickable(driver, timeOption);
            option.click();
            WaitUtils.hardWait(1000);
        } catch (Exception e) {
            System.out.println("Time field interaction issue: " + e.getMessage());
        }
    }

    /**
     * Clicks the Search button.
     */
    public void clickSearch() {
        WebElement search = WaitUtils.waitForElementClickable(driver, searchBtn);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", search);
        WaitUtils.hardWait(4000);
    }

    /**
     * Closes any popup/modal that may appear on the listing page.
     */
    public void closePopupIfVisible() {
        try {
            // Try multiple close button strategies
            List<WebElement> closeButtons = driver.findElements(
                    By.xpath("//button[contains(@class,'close') or contains(@aria-label,'close') or contains(@aria-label,'Close')] | " +
                            "//span[text()='×'] | //*[@class[contains(.,'closeBtn')]] | " +
                            "//*[contains(@class,'bookAssist') or contains(@class,'assistCard')]//button"));

            for (WebElement btn : closeButtons) {
                try {
                    if (btn.isDisplayed()) {
                        btn.click();
                        WaitUtils.hardWait(1000);
                        System.out.println("Popup closed successfully");
                        break;
                    }
                } catch (Exception ignored) {
                }
            }

            // Also try pressing Escape key
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
            WaitUtils.hardWait(1000);
        } catch (Exception e) {
            System.out.println("No popup found or failed to close: " + e.getMessage());
        }
    }

    /**
     * Selects SUV from the cab type filters.
     */
    public void selectSuvFilter() {
        try {
            WebElement suvFilter = WaitUtils.waitForElementClickable(driver, suvCheckbox);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true)", suvFilter);
            WaitUtils.hardWait(500);
            suvFilter.click();
            WaitUtils.hardWait(2000);
            System.out.println("SUV filter selected");
        } catch (Exception e) {
            System.out.println("Could not select SUV filter: " + e.getMessage());
            // Try clicking by JS
            try {
                WebElement suvLabel = driver.findElement(By.xpath("//*[contains(text(),'SUV')]"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", suvLabel);
                WaitUtils.hardWait(2000);
            } catch (Exception e2) {
                System.out.println("JS click on SUV also failed: " + e2.getMessage());
            }
        }
    }

    /**
     * Returns true if SUV results are shown.
     */
    public boolean isSuvResultDisplayed() {
        try {
            List<WebElement> suvResults = driver.findElements(
                    By.xpath("//*[contains(text(),'SUV') or contains(@class,'suv') or contains(@alt,'SUV')]"));
            return !suvResults.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
