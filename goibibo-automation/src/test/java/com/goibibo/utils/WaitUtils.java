package com.goibibo.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtils - Explicit wait helpers with retry logic for stale elements.
 */
public class WaitUtils {

    private static final int DEFAULT_TIMEOUT = 20;

    public static WebElement waitForElementVisible(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForElementClickable(WebDriver driver, By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForElementVisible(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForElementClickable(WebDriver driver, WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Retries finding and interacting with an element up to 3 times.
     * Handles StaleElementReferenceException caused by React DOM re-renders.
     */
    public static void clickWithRetry(WebDriver driver, By locator) {
        for (int i = 0; i < 3; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElement on click, retrying attempt " + (i + 1));
                hardWait(500);
            }
        }
    }

    /**
     * Types text into a field with retry for stale elements.
     * Re-finds the element each time to avoid StaleElementReferenceException.
     */
    public static void sendKeysWithRetry(WebDriver driver, By locator, String text) {
        for (int i = 0; i < 3; i++) {
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.clear();
                element.sendKeys(text);
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElement on sendKeys, retrying attempt " + (i + 1));
                hardWait(500);
            }
        }
    }

    public static boolean waitForUrlContains(WebDriver driver, String partialUrl) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        return wait.until(ExpectedConditions.urlContains(partialUrl));
    }

    public static void hardWait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
