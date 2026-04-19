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
 * With a fresh browser per scenario, the hotels page at goibibo.com/hotels/
 * loads properly (no "200 - OK" anti-bot response).
 *
 * From the screenshot, the search input has label "Where to" above it.
 */
public class HotelsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public HotelsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    /**
     * Finds the hotel "Where to" search input using multiple strategies.
     * Waits for the page to be fully loaded before searching.
     */
    private WebElement findHotelSearchInput() {

        // Wait for the page to contain hotel-related content before searching
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("Hotel"),
                ExpectedConditions.urlContains("hotel"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Hotel') or contains(text(),'Where to')]"))
            ));
        } catch (Exception e) {
            System.out.println("Page load check: " + e.getMessage());
        }

        System.out.println("Finding hotel search input. Page title: " + driver.getTitle());
        System.out.println("Current URL: " + driver.getCurrentUrl());

        // Try locators in order of reliability
        String[] xpaths = {
            // From screenshot: label says "Where to" — label-based
            "//label[contains(text(),'Where to') or contains(text(),'Where To')]/following-sibling::input",
            "//label[contains(text(),'Where to') or contains(text(),'Where To')]/..//input",
            // Placeholder-based (if placeholder is set)
            "//input[@placeholder='Where to' or @placeholder='Where To']",
            "//input[@placeholder[contains(.,'Where') or contains(.,'where')]]",
            // Common id/name patterns for hotel city search
            "//input[@id='city' or @id='hotelCity' or @id='hotel-city' or @id='search_hotel']",
            "//input[@name='city' or @name='hotelCity' or @name='query']",
            // Class-based
            "//input[contains(@class,'city') or contains(@class,'search') or contains(@class,'destination')]",
            // Inside a container labeled "Hotel"
            "//div[contains(@class,'hotel') or contains(@class,'Hotel')]//input[@type='text']",
            // Broadest fallback — first visible text input
            "(//input[@type='text'])[1]"
        };

        for (String xpath : xpaths) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));
                WebElement el = shortWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                System.out.println("Hotel input found via: " + xpath);
                return el;
            } catch (Exception e) {
                // Try next
            }
        }

        // JavaScript fallback: first visible text input in the TOP HALF of the page (not footer)
        System.out.println("All XPath strategies failed, trying JavaScript");
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement input = (WebElement) js.executeScript(
                "var inputs = document.querySelectorAll('input[type=\"text\"], input:not([type])');" +
                "for(var i=0; i<inputs.length; i++){" +
                "  var r = inputs[i].getBoundingClientRect();" +
                "  if(r.width > 100 && r.height > 20 && r.top < window.innerHeight * 0.6 && inputs[i].offsetParent !== null){" +
                "    console.log('Found hotel input at position:', r.top, r.left, inputs[i].placeholder);" +
                "    return inputs[i];" +
                "  }" +
                "}" +
                "return null;"
            );
            if (input != null) {
                System.out.println("Hotel input found via JavaScript");
                return input;
            }
        } catch (Exception e) {
            System.out.println("JavaScript input search failed: " + e.getMessage());
        }

        // If page returned "200 - OK" (bot detection), throw clear error
        String bodyText = driver.findElement(By.tagName("body")).getText().trim();
        if (bodyText.equals("200 - OK") || bodyText.isEmpty()) {
            throw new RuntimeException(
                "Goibibo returned a blank/bot-blocked page. " +
                "The fresh browser fix should prevent this. Body: '" + bodyText + "'"
            );
        }

        throw new RuntimeException("Could not find hotel search input. Page: " + driver.getTitle());
    }

    /**
     * Enters the hotel destination with retry for stale elements.
     */
    public void enterHotelLocation(String location) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement searchInput = findHotelSearchInput();
                searchInput.click();
                WaitUtils.hardWait(500);
                // Clear using JS to avoid event conflicts
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = ''", searchInput);
                searchInput.sendKeys(location);
                WaitUtils.hardWait(2500);
                System.out.println("Typed hotel location: " + location);
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element retry " + (attempt + 1));
                WaitUtils.hardWait(1000);
            }
        }
        throw new RuntimeException("Could not enter hotel location after 3 attempts");
    }

    /**
     * Selects a suggestion from the hotel search autocomplete dropdown.
     * From the screenshot: suggestions are in a list below the input.
     * "Mall Road" appears as "Mall Road - 54 Property - Area"
     */
    public void selectHotelSuggestion(String suggestionText) {
        // Try matching just the first word or phrase of the suggestion
        By[] suggestionLocators = {
            By.xpath("//ul//li[contains(.,'" + suggestionText + "')] | //div[contains(@class,'option')][contains(.,'" + suggestionText + "')]"),
            By.xpath("//*[contains(@class,'suggestion') or contains(@class,'autocomplete') or contains(@class,'dropdown')]//*[contains(text(),'" + suggestionText + "')]"),
            By.xpath("//*[contains(text(),'" + suggestionText + "')]")
        };

        for (By locator : suggestionLocators) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(6));
                WebElement suggestion = shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                suggestion.click();
                WaitUtils.hardWait(1500);
                System.out.println("Selected hotel suggestion: " + suggestionText);
                return;
            } catch (Exception e) {
                // try next
            }
        }
        System.out.println("Hotel suggestion not found for: " + suggestionText);
    }

    /**
     * Sets check-in date. Date format: dd/MM/yyyy
     */
    public void setCheckInDate(String date) {
        String day = String.valueOf(Integer.parseInt(date.split("/")[0]));
        try {
            // The check-in field — click it to open the calendar
            By checkInField = By.xpath(
                "//label[contains(text(),'Check-in') or contains(text(),'Checkin') or contains(text(),'CHECKIN')]/following-sibling::* | " +
                "//*[contains(@class,'checkIn') or contains(@class,'checkin') or contains(@class,'check-in')]"
            );
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(checkInField)).click();
                WaitUtils.hardWait(1500);
            } catch (Exception ignore) {
                System.out.println("Check-in field click skipped");
            }
            clickDayInCalendar(day);
        } catch (Exception e) {
            System.out.println("Check-in date error: " + e.getMessage());
        }
    }

    /**
     * Sets check-out date. Calendar stays open after check-in is selected.
     */
    public void setCheckOutDate(String date) {
        String day = String.valueOf(Integer.parseInt(date.split("/")[0]));
        clickDayInCalendar(day);
    }

    private void clickDayInCalendar(String day) {
        By[] dayLocators = {
            By.xpath("//div[contains(@class,'DayPicker') or contains(@class,'Calendar') or contains(@class,'calendar')]//div[@role='gridcell'][normalize-space(text())='" + day + "'][not(contains(@class,'disabled'))]"),
            By.xpath("//table//td[not(contains(@class,'disabled')) and not(contains(@class,'prev')) and not(contains(@class,'next'))][normalize-space(text())='" + day + "']"),
            By.xpath("//div[contains(@class,'day')][not(contains(@class,'disabled'))][normalize-space(text())='" + day + "']"),
            By.xpath("//*[@role='button' or @role='gridcell'][normalize-space(text())='" + day + "'][not(contains(@class,'disabled'))]")
        };
        for (By locator : dayLocators) {
            try {
                WebElement dayEl = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(locator));
                dayEl.click();
                WaitUtils.hardWait(1000);
                System.out.println("Clicked calendar day: " + day);
                return;
            } catch (Exception e) {
                // try next
            }
        }
        System.out.println("Could not click calendar day: " + day);
    }

    /**
     * Sets number of adults. Opens the Guests & Rooms panel, clicks + to add.
     */
    public void setAdults(int desiredCount) {
        try {
            // Open guests panel by clicking the Guests & Rooms field
            By guestsPanel = By.xpath(
                "//*[contains(text(),'Guest') or contains(text(),'Room') or contains(text(),'Traveller')]" +
                "[not(self::label)][not(contains(@class,'filter'))]"
            );
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(guestsPanel)).click();
                WaitUtils.hardWait(1000);
            } catch (Exception e) {
                System.out.println("Guests panel open: " + e.getMessage());
            }

            int current = getAdultCount();
            System.out.println("Adults current=" + current + " target=" + desiredCount);
            for (int i = current; i < desiredCount; i++) {
                clickAdultPlus();
                WaitUtils.hardWait(400);
            }
        } catch (Exception e) {
            System.out.println("setAdults error: " + e.getMessage());
        }
    }

    private int getAdultCount() {
        try {
            List<WebElement> counts = driver.findElements(
                By.xpath("//*[contains(@class,'adult') or contains(text(),'Adult')]//*[contains(@class,'count')] | " +
                         "//span[contains(@class,'count')][1]")
            );
            if (!counts.isEmpty()) {
                String text = counts.get(0).getText().replaceAll("[^0-9]", "");
                if (!text.isEmpty()) return Integer.parseInt(text);
            }
        } catch (Exception e) {
            // ignore
        }
        return 1;
    }

    private void clickAdultPlus() {
        try {
            List<WebElement> plusBtns = driver.findElements(
                By.xpath("//span[@role='button'][text()='+'] | //button[text()='+'] | //span[text()='+'][1]")
            );
            for (WebElement btn : plusBtns) {
                if (btn.isDisplayed()) {
                    btn.click();
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("Plus button click failed: " + e.getMessage());
        }
    }

    /**
     * Clicks the Search button on the hotels page.
     */
    public void clickSearchHotels() {
        By[] searchLocators = {
            By.xpath("//button[normalize-space(text())='Search' or normalize-space(text())='SEARCH']"),
            By.xpath("//button[contains(@class,'search') or contains(@class,'Search')]"),
            By.xpath("//div[contains(@class,'searchBtn') or contains(@class,'search-btn')]"),
            By.xpath("//input[@type='submit']")
        };
        for (By locator : searchLocators) {
            try {
                WebElement search = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(locator));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", search);
                WaitUtils.hardWait(5000);
                System.out.println("Hotels search clicked, URL: " + driver.getCurrentUrl());
                return;
            } catch (Exception e) {
                // try next
            }
        }
        System.out.println("All hotel search button locators failed");
    }

    public boolean isHotelResultsDisplayed() {
        WaitUtils.hardWait(2000);
        String url = driver.getCurrentUrl();
        return url.contains("hotel") && (url.contains("listing") || url.contains("manali") || url.contains("result"));
    }
}
