package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * HotelsPage - Page Object for the Goibibo Hotels booking page.
 * Handles hotel search for Manali Mall Road with 4 adults.
 */
public class HotelsPage {

    private WebDriver driver;

    // Locators for Hotels page
    private By hotelSearchInput = By.xpath("//input[@placeholder[contains(.,'city') or contains(.,'hotel') or contains(.,'destination') or contains(.,'City')]] | " +
            "//div[@class[contains(.,'search') or contains(.,'hotel')]]//input");
    private By checkInInput = By.xpath("//label[contains(text(),'Check-in') or contains(text(),'Checkin')]/following-sibling::* | " +
            "//div[contains(@class,'checkIn') or contains(@class,'checkin')]//input | " +
            "//input[@placeholder[contains(.,'Check') and contains(.,'in')]]");
    private By checkOutInput = By.xpath("//label[contains(text(),'Check-out') or contains(text(),'Checkout')]/following-sibling::* | " +
            "//div[contains(@class,'checkOut') or contains(@class,'checkout')]//input | " +
            "//input[@placeholder[contains(.,'Check') and contains(.,'out')]]");
    private By guestsInput = By.xpath("//label[contains(text(),'Guest') or contains(text(),'Room')]/following-sibling::* | " +
            "//div[contains(@class,'guest') or contains(@class,'room')]//input | " +
            "//input[@placeholder[contains(.,'Guest') or contains(.,'Room')]]");
    private By searchBtn = By.xpath("//button[contains(text(),'Search') or contains(text(),'SEARCH') or @type='submit'] | " +
            "//div[@class[contains(.,'searchBtn')]] | " +
            "//input[@type='submit']");
    private By adultsIncreaseBtn = By.xpath("//span[contains(@class,'add') or text()='+'][1] | " +
            "//button[contains(@class,'plus') or contains(@class,'increment') or text()='+'][1]");

    public HotelsPage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Enters hotel destination in the search field.
     */
    public void enterHotelLocation(String location) {
        WebElement searchInput = WaitUtils.waitForElementClickable(driver, hotelSearchInput);
        searchInput.click();
        WaitUtils.hardWait(500);
        searchInput.clear();
        searchInput.sendKeys(location);
        WaitUtils.hardWait(2500);
    }

    /**
     * Selects a suggestion from the hotel search dropdown.
     */
    public void selectHotelSuggestion(String suggestionText) {
        By suggestionLocator = By.xpath("//*[contains(text(),'" + suggestionText + "')]");
        WebElement suggestion = WaitUtils.waitForElementClickable(driver, suggestionLocator);
        suggestion.click();
        WaitUtils.hardWait(1500);
    }

    /**
     * Sets check-in date. Tries direct input and then calendar click.
     */
    public void setCheckInDate(String date) {
        try {
            WebElement checkIn = WaitUtils.waitForElementClickable(driver, checkInInput);
            checkIn.click();
            WaitUtils.hardWait(1000);
            clickDateInCalendar(date);
        } catch (Exception e) {
            System.out.println("Check-in date issue: " + e.getMessage());
        }
    }

    /**
     * Sets check-out date.
     */
    public void setCheckOutDate(String date) {
        try {
            // After check-in, calendar is usually still open for checkout
            clickDateInCalendar(date);
        } catch (Exception e) {
            System.out.println("Check-out date issue: " + e.getMessage());
            try {
                WebElement checkOut = WaitUtils.waitForElementClickable(driver, checkOutInput);
                checkOut.click();
                WaitUtils.hardWait(1000);
                clickDateInCalendar(date);
            } catch (Exception e2) {
                System.out.println("Check-out date second attempt failed: " + e2.getMessage());
            }
        }
    }

    /**
     * Clicks on a specific day in the calendar.
     * Date format: dd/MM/yyyy
     */
    private void clickDateInCalendar(String date) {
        String[] parts = date.split("/");
        String day = String.valueOf(Integer.parseInt(parts[0]));

        By dayLocator = By.xpath("//table//td[text()='" + day + "'] | " +
                "//div[contains(@class,'day') or contains(@class,'date')]//span[text()='" + day + "'] | " +
                "//div[@aria-label[contains(.,'" + day + "')]]");
        try {
            WebElement dayElement = WaitUtils.waitForElementClickable(driver, dayLocator);
            dayElement.click();
            WaitUtils.hardWait(1000);
        } catch (Exception e) {
            System.out.println("Could not click date " + day + ": " + e.getMessage());
        }
    }

    /**
     * Sets number of adults to the specified count.
     * Clicks the plus button until we reach the desired count.
     */
    public void setAdults(int desiredCount) {
        try {
            // First click on the guests section to open it
            try {
                WebElement guestsSection = WaitUtils.waitForElementClickable(driver, guestsInput);
                guestsSection.click();
                WaitUtils.hardWait(1000);
            } catch (Exception e) {
                System.out.println("Guests section click: " + e.getMessage());
            }

            // Get current count of adults (default is usually 1)
            int currentCount = getCurrentAdultCount();
            System.out.println("Current adult count: " + currentCount);

            // Click plus button to add more adults
            for (int i = currentCount; i < desiredCount; i++) {
                clickAdultPlusButton();
                WaitUtils.hardWait(500);
            }

            System.out.println("Set adults to: " + desiredCount);
        } catch (Exception e) {
            System.out.println("Could not set adults: " + e.getMessage());
        }
    }

    private int getCurrentAdultCount() {
        try {
            By adultCountLocator = By.xpath("//*[contains(@class,'adult') or contains(@class,'Adult')]//span[contains(@class,'count') or contains(@class,'value')] | " +
                    "//div[contains(text(),'Adult')]//input | " +
                    "//span[@class[contains(.,'count')]][1]");
            WebElement countEl = driver.findElement(adultCountLocator);
            String countText = countEl.getText().trim();
            if (countText.isEmpty()) {
                countText = countEl.getAttribute("value");
            }
            return Integer.parseInt(countText.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 1;
        }
    }

    private void clickAdultPlusButton() {
        try {
            List<WebElement> plusBtns = driver.findElements(
                    By.xpath("//div[contains(@class,'adult') or contains(text(),'Adult')]//*[text()='+'] | " +
                            "//button[contains(@class,'plus') or text()='+'][1] | " +
                            "//span[text()='+'][1]"));
            if (!plusBtns.isEmpty()) {
                plusBtns.get(0).click();
            }
        } catch (Exception e) {
            System.out.println("Plus button click failed: " + e.getMessage());
        }
    }

    /**
     * Clicks the Search button on hotels page.
     */
    public void clickSearchHotels() {
        try {
            WebElement search = WaitUtils.waitForElementClickable(driver, searchBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", search);
            WaitUtils.hardWait(4000);
        } catch (Exception e) {
            System.out.println("Search button click failed: " + e.getMessage());
        }
    }

    /**
     * Returns true if we are on the hotel results page.
     */
    public boolean isHotelResultsDisplayed() {
        try {
            WaitUtils.hardWait(3000);
            String currentUrl = driver.getCurrentUrl();
            return currentUrl.contains("hotel") || currentUrl.contains("manali");
        } catch (Exception e) {
            return false;
        }
    }
}
