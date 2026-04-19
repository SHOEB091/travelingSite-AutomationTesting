package com.goibibo.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * DriverManager - Creates a fresh browser for each scenario.
 * After every scenario the browser is closed (quitDriver).
 * The next scenario calls getDriver() which opens a brand-new Chrome window.
 *
 * Why fresh browser per scenario:
 *  - Goibibo detects automation after repeated navigation in the same session
 *    and starts returning plain "200 - OK" instead of the real page.
 *  - A fresh browser has no session history, no cookies, no bot fingerprint.
 */
public class DriverManager {

    private static WebDriver driver;

    private DriverManager() {}

    public static WebDriver getDriver() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
        }
        return driver;
    }

    /**
     * Closes the browser completely.
     * Called after every scenario so the next scenario gets a fresh browser.
     */
    public static void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Driver quit error (safe to ignore): " + e.getMessage());
            } finally {
                driver = null;
            }
        }
    }
}
