package com.goibibo.stepDefs;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.goibibo.utils.DriverManager;
import com.goibibo.utils.ExtentReportManager;
import com.goibibo.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

/**
 * Hooks - Cucumber lifecycle hooks.
 *
 * Each scenario gets a FRESH browser:
 *   @Before  -> opens a new Chrome window
 *   @After   -> takes final screenshot, logs pass/fail, then CLOSES browser
 *
 * This prevents Goibibo's anti-bot detection from blocking hotel/gift card tests
 * that would otherwise fail with "200 - OK" blank pages.
 */
public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        // Opens a new browser (driver == null since previous scenario closed it)
        DriverManager.getDriver();

        ExtentTest test = ExtentReportManager.getExtentReports()
                .createTest(scenario.getName());
        ExtentReportManager.setTest(test);
        test.log(Status.INFO, "Starting: " + scenario.getName());
        test.log(Status.INFO, "Tags: " + scenario.getSourceTagNames());
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();
        ExtentTest test = ExtentReportManager.getTest();

        // Take final screenshot
        try {
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, scenario.getName() + "_end");

            if (scenario.isFailed()) {
                test.log(Status.FAIL, "Scenario FAILED");
                test.fail("Screenshot on failure:",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } else {
                test.log(Status.PASS, "Scenario PASSED");
                test.pass("Final screenshot:",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }
        } catch (Exception e) {
            test.log(Status.WARNING, "Could not attach screenshot: " + e.getMessage());
        }

        ExtentReportManager.flushReports();

        // Close browser — next scenario will open a fresh one
        DriverManager.quitDriver();
    }

    @AfterStep
    public void afterEachStep(Scenario scenario) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver == null) return;
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "step");
            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                test.log(Status.INFO, "Step completed:",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }
        } catch (Exception e) {
            // Silently skip if driver not available
        }
    }
}
