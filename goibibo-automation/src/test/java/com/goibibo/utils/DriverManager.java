package com.goibibo.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * DriverManager - Single browser instance shared across all scenarios.
 * Browser opens once at the start and closes only at the very end (JVM shutdown).
 * Between scenarios we just navigate back to the home page.
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
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            driver = new ChromeDriver(options);
            driver.manage().window().maximize();

            // Quit browser automatically when the JVM shuts down
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (driver != null) {
                    driver.quit();
                    driver = null;
                }
            }));
        }
        return driver;
    }

    /**
     * Navigate to home page instead of quitting — reused between scenarios.
     */
    public static void goHome() {
        if (driver != null) {
            driver.get("https://www.goibibo.com");
            WaitUtils.hardWait(2000);
        }
    }

    /**
     * Only call this if you truly want to close the browser.
     */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
