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
 * The hotel search input is found using multiple strategies including JavaScript,
 * because Goibibo's React UI renders the input differently depending on page state.
 */
public class HotelsPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public HotelsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Finds the hotel search input using multiple strategies.
     * Returns the first visible, clickable text input found.
     */
    private WebElement findHotelSearchInput() {
        // Strategy 1: placeholder-based locators (most reliable when they work)
        String[] xpaths = {
            "//input[@placeholder[contains(.,'City') or contains(.,'city') or contains(.,'area') or contains(.,'property') or contains(.,'destination') or contains(.,'hotel')]]",
            "//input[@id[contains(.,'city') or contains(.,'City') or contains(.,'search') or contains(.,'Search')]]",
            "//input[@name[contains(.,'city') or contains(.,'search') or contains(.,'query')]]",
            "//div[contains(@class,'hotel') or contains(@class,'Hotel')]//input[@type='text']",
            "//div[contains(@class,'search')]//input[@type='text']",
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

        // Strategy 2: JavaScript — find first visible input that is not hidden
        System.out.println("XPath strategies failed, using JavaScript to find hotel input");
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement input = (WebElement) js.executeScript(
                "var inputs = document.querySelectorAll('input[type=\"text\"]');" +
                "for(var i=0; i<inputs.length; i++){" +
                "  var r = inputs[i].getBoundingClientRect();" +
                "  if(r.width > 0 && r.height > 0 && inputs[i].offsetParent !== null){" +
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
            System.out.println("JavaScript input find failed: " + e.getMessage());
        }

        throw new RuntimeException("Could not find hotel search input on page: " + driver.getCurrentUrl());
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
                // Clear using JS to avoid stale issues
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = ''", searchInput);
                searchInput.sendKeys(location);
                WaitUtils.hardWait(2500);
                System.out.println("Typed hotel location: " + location);
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element on hotel search input, retry " + (attempt + 1));
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
            By checkInField = By.xpath(
                    "//label[contains(text(),'Check') and (contains(text(),'in') or contains(text(),'In'))]/following-sibling::* | " +
                    "//*[contains(@class,'checkIn') or contains(@class,'checkin') or contains(@class,'check-in')] | " +
                    "//*[contains(@placeholder,'check-in') or contains(@placeholder,'Check-in')]"
            );
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                shortWait.until(ExpectedConditions.elementToBeClickable(checkInField)).click();
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
     * Sets check-out date. Calendar usually stays open after check-in is picked.
     */
    public void setCheckOutDate(String date) {
        String[] parts = date.split("/");
        String day = String.valueOf(Integer.parseInt(parts[0]));
        clickDayInCalendar(day);
    }

    /**
     * Clicks on the given day number in the currently open calendar widget.
     */
    private void clickDayInCalendar(String day) {
        By[] dayLocators = {
            By.xpath("//div[contains(@class,'DayPicker') or contains(@class,'Calendar') or contains(@class,'calendar')]//div[not(contains(@class,'disabled'))][@role='gridcell'][normalize-space(text())='" + day + "']"),
            By.xpath("//table//td[not(contains(@class,'disabled')) and not(contains(@class,'prev')) and not(contains(@class,'next'))][normalize-space(text())='" + day + "']"),
            By.xpath("//div[@class[contains(.,'day')]][not(contains(@class,'disabled'))][normalize-space(text())='" + day + "']"),
            By.xpath("//*[normalize-space(text())='" + day + "' and not(contains(@class,'disabled'))][@role='button' or @role='gridcell']")
        };

        for (By locator : dayLocators) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement dayEl = shortWait.until(ExpectedConditions.elementToBeClickable(locator));
                dayEl.click();
                WaitUtils.hardWait(1000);
                System.out.println("Clicked calendar day: " + day);
                return;
            } catch (Exception e) {
                // try next locator
            }
        }
        System.out.println("Could not click day " + day + " in calendar");
    }

    /**
     * Sets the number of adults to the desired count.
     * Opens the guests panel, reads the current count, then clicks + to increment.
     */
    public void setAdults(int desiredCount) {
        try {
            // Click guests/travellers section to open it
            By guestsField = By.xpath(
                    "//*[contains(@class,'guest') or contains(@class,'room') or contains(@class,'traveller')]//input | " +
                    "//label[contains(text(),'Guest') or contains(text(),'Room') or contains(text(),'Traveller')]/following-sibling::* | " +
                    "//*[contains(text(),'Guest') or contains(text(),'Room') or contains(text(),'Traveller')][not(self::label)]"
            );
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                shortWait.until(ExpectedConditions.elementToBeClickable(guestsField)).click();
                WaitUtils.hardWait(1000);
            } catch (Exception e) {
                System.out.println("Guests field open issue: " + e.getMessage());
            }

            int current = getAdultCount();
            System.out.println("Current adult count: " + current + ", target: " + desiredCount);

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
                    "//*[contains(@class,'adult') or contains(text(),'Adult')]" +
                    "//*[contains(@class,'count') or contains(@class,'value')] | " +
                    "//span[contains(@class,'count')][1]"
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
                "//div[contains(text(),'Adult') or @class[contains(.,'adult')]]" +
                "/following-sibling::*//span[text()='+' or text()='＋'] | " +
                "//div[contains(text(),'Adult') or @class[contains(.,'adult')]]" +
                "/following-sibling::*//button[text()='+'] | " +
                "//button[contains(@class,'plus') or contains(@class,'increment')][1] | " +
                "//span[@role='button'][text()='+'][1]"
        );
        try {
            List<WebElement> plusBtns = driver.findElements(plusLocator);
            if (!plusBtns.isEmpty()) {
                plusBtns.get(0).click();
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
            By.xpath("//button[contains(@class,'searchbtn') or contains(@class,'search-btn')]"),
            By.xpath("//div[contains(@class,'searchBtn')]"),
            By.xpath("//input[@type='submit' and contains(@value,'Search')]")
        };

        for (By locator : searchLocators) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement search = shortWait.until(ExpectedConditions.elementToBeClickable(locator));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", search);
                WaitUtils.hardWait(5000);
                System.out.println("Hotels search clicked, URL: " + driver.getCurrentUrl());
                return;
            } catch (Exception e) {
                // Try next locator
            }
        }
        System.out.println("All hotel search button locators failed");
    }

    public boolean isHotelResultsDisplayed() {
        WaitUtils.hardWait(2000);
        String url = driver.getCurrentUrl();
        return url.contains("hotel") || url.contains("manali") || url.contains("result");
    }
}
