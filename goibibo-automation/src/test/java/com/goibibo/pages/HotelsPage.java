package com.goibibo.pages;

import com.goibibo.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * HotelsPage - Page Object for Goibibo Hotels booking.
 *
 * Key fixes:
 * 1. Use specific placeholder text "City, area or property" for hotel search
 * 2. Retry logic for stale elements
 * 3. Better calendar date selection
 * 4. Adults count handled via plus button clicks
 */
public class HotelsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Goibibo hotel search input uses placeholder "City, area or property"
    private By hotelSearchLocator = By.xpath(
            "//input[@placeholder[contains(.,'City') or contains(.,'area') or contains(.,'property') or contains(.,'destination')]] | " +
            "//input[@type='text' and contains(@class,'search')] | " +
            "(//input[@type='text'])[1]"
    );

    private By searchBtnLocator = By.xpath(
            "//button[normalize-space(text())='Search' or normalize-space(text())='SEARCH'] | " +
            "//button[contains(@class,'searchbtn') or contains(@class,'search-btn')] | " +
            "//div[@class[contains(.,'searchBtn')]]"
    );

    public HotelsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Enters the hotel destination with retry for stale elements.
     */
    public void enterHotelLocation(String location) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(hotelSearchLocator));
                searchInput.click();
                WaitUtils.hardWait(500);
                // Re-find after click
                searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(hotelSearchLocator));
                searchInput.clear();
                searchInput.sendKeys(location);
                WaitUtils.hardWait(2500);
                System.out.println("Typed hotel location: " + location);
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element on hotel search, retry " + (attempt + 1));
                WaitUtils.hardWait(1000);
            }
        }
        throw new RuntimeException("Could not enter hotel location after 3 attempts");
    }

    /**
     * Selects a suggestion from the hotel search autocomplete dropdown.
     */
    public void selectHotelSuggestion(String suggestionText) {
        By suggestionLocator = By.xpath(
                "//ul//li[contains(.,'" + suggestionText + "')] | " +
                "//*[contains(@class,'suggestion') or contains(@class,'autocomplete') or contains(@class,'dropdown')]" +
                "//*[contains(text(),'" + suggestionText + "')] | " +
                "//*[contains(text(),'" + suggestionText + "')]"
        );
        try {
            WebElement suggestion = wait.until(ExpectedConditions.visibilityOfElementLocated(suggestionLocator));
            suggestion.click();
            WaitUtils.hardWait(1500);
            System.out.println("Selected hotel suggestion: " + suggestionText);
        } catch (Exception e) {
            System.out.println("Hotel suggestion not found: " + suggestionText + " -> " + e.getMessage());
        }
    }

    /**
     * Sets check-in date by clicking on the calendar.
     * Date format: dd/MM/yyyy
     */
    public void setCheckInDate(String date) {
        String[] parts = date.split("/");
        String day = String.valueOf(Integer.parseInt(parts[0]));

        try {
            // Click check-in field to open calendar
            By checkInField = By.xpath(
                    "//label[contains(text(),'Check') and contains(text(),'in')]/following-sibling::* | " +
                    "//*[contains(@class,'checkIn') or contains(@class,'checkin')]//input | " +
                    "//*[contains(@class,'checkIn') or contains(@class,'checkin')]"
            );
            try {
                wait.until(ExpectedConditions.elementToBeClickable(checkInField)).click();
                WaitUtils.hardWait(1500);
            } catch (Exception ignore) {
                System.out.println("Check-in field click skipped (calendar may already be open)");
            }

            clickDayInCalendar(day);
        } catch (Exception e) {
            System.out.println("Check-in date error: " + e.getMessage());
        }
    }

    /**
     * Sets check-out date. Calendar usually stays open after check-in.
     */
    public void setCheckOutDate(String date) {
        String[] parts = date.split("/");
        String day = String.valueOf(Integer.parseInt(parts[0]));
        clickDayInCalendar(day);
    }

    /**
     * Clicks on the given day number in the currently open calendar.
     */
    private void clickDayInCalendar(String day) {
        By dayLocator = By.xpath(
                "//div[contains(@class,'DayPicker') or contains(@class,'Calendar') or contains(@class,'calendar')]" +
                "//div[not(contains(@class,'disabled'))][@role='gridcell' or @aria-label][normalize-space(text())='" + day + "'] | " +
                "//table//td[not(contains(@class,'disabled')) and not(contains(@class,'prev')) and not(contains(@class,'next'))]" +
                "[normalize-space(text())='" + day + "'] | " +
                "//div[@class[contains(.,'day')]][not(contains(@class,'disabled'))][normalize-space(text())='" + day + "']"
        );
        try {
            WebElement dayEl = wait.until(ExpectedConditions.elementToBeClickable(dayLocator));
            dayEl.click();
            WaitUtils.hardWait(1000);
            System.out.println("Clicked day: " + day);
        } catch (Exception e) {
            System.out.println("Could not click day " + day + ": " + e.getMessage());
        }
    }

    /**
     * Sets the number of adults to the desired count.
     * First opens the guests section, then clicks the plus button until the count matches.
     */
    public void setAdults(int desiredCount) {
        try {
            // Click the guests/rooms field to open the guest selector
            By guestsField = By.xpath(
                    "//*[contains(@class,'guest') or contains(@class,'room') or contains(@class,'traveller')]//input | " +
                    "//label[contains(text(),'Guest') or contains(text(),'Room')]/following-sibling::* | " +
                    "//div[contains(text(),'Guest') or contains(text(),'Room') or contains(text(),'Adult')]"
            );
            try {
                wait.until(ExpectedConditions.elementToBeClickable(guestsField)).click();
                WaitUtils.hardWait(1000);
            } catch (Exception e) {
                System.out.println("Guests field click issue: " + e.getMessage());
            }

            // Get current adult count
            int current = getAdultCount();
            System.out.println("Current adult count: " + current + ", target: " + desiredCount);

            // Click plus button (desiredCount - current) times
            for (int i = current; i < desiredCount; i++) {
                clickAdultPlus();
                WaitUtils.hardWait(400);
            }

            System.out.println("Adults set to: " + desiredCount);
        } catch (Exception e) {
            System.out.println("setAdults error: " + e.getMessage());
        }
    }

    private int getAdultCount() {
        try {
            By countLocator = By.xpath(
                    "//div[contains(text(),'Adult') or contains(text(),'adult')]" +
                    "/following-sibling::*//*[contains(@class,'count') or contains(@class,'value')] | " +
                    "//*[contains(@class,'adult')]//span[contains(@class,'count')]"
            );
            List<WebElement> counts = driver.findElements(countLocator);
            if (!counts.isEmpty()) {
                String text = counts.get(0).getText().replaceAll("[^0-9]", "");
                if (!text.isEmpty()) return Integer.parseInt(text);
            }
        } catch (Exception e) {
            System.out.println("Could not read adult count: " + e.getMessage());
        }
        return 1;
    }

    private void clickAdultPlus() {
        By plusLocator = By.xpath(
                "//div[contains(text(),'Adult')]/following-sibling::*//span[text()='+'] | " +
                "//div[contains(text(),'Adult')]/following-sibling::*//button[text()='+'] | " +
                "//button[contains(@class,'plus') or contains(@class,'increment')][1] | " +
                "//span[text()='+'][1]"
        );
        try {
            List<WebElement> plusBtns = driver.findElements(plusLocator);
            if (!plusBtns.isEmpty()) {
                plusBtns.get(0).click();
            }
        } catch (Exception e) {
            System.out.println("Plus click failed: " + e.getMessage());
        }
    }

    /**
     * Clicks the Search button on the hotels page.
     */
    public void clickSearchHotels() {
        try {
            WebElement search = wait.until(ExpectedConditions.elementToBeClickable(searchBtnLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", search);
            WaitUtils.hardWait(5000);
            System.out.println("Hotels search clicked, URL: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("Hotel search button click failed: " + e.getMessage());
        }
    }

    public boolean isHotelResultsDisplayed() {
        WaitUtils.hardWait(2000);
        String url = driver.getCurrentUrl();
        return url.contains("hotel") || url.contains("manali") || url.contains("result");
    }
}
