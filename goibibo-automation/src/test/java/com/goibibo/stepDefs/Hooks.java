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
 * - @Before: starts browser + Extent test node
 * - @AfterStep: takes screenshot after every step (attached to report)
 * - @After: marks test pass/fail, takes final screenshot, flushes report, closes browser
 */
public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
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

        // Quit after each scenario so the next one starts with a fresh browser
        DriverManager.quitDriver();
    }

    @AfterStep
    public void afterEachStep(Scenario scenario) {
        // Only take screenshots if driver is still alive
        try {
            WebDriver driver = DriverManager.getDriver();
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
