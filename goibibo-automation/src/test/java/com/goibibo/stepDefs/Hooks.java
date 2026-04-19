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
 * Hooks - Cucumber Before/After hooks.
 * Sets up browser, starts Extent test, captures screenshots on failure.
 */
public class Hooks {

    private WebDriver driver;

    @Before
    public void setUp(Scenario scenario) {
        // Initialize the WebDriver
        driver = DriverManager.getDriver();

        // Create an Extent test node for this scenario
        ExtentTest test = ExtentReportManager.getExtentReports()
                .createTest(scenario.getName());
        ExtentReportManager.setTest(test);
        test.log(Status.INFO, "Starting scenario: " + scenario.getName());
        test.log(Status.INFO, "Tags: " + scenario.getSourceTagNames());
    }

    @After
    public void tearDown(Scenario scenario) {
        ExtentTest test = ExtentReportManager.getTest();

        // Take screenshot after each scenario
        String screenshotPath = ScreenshotUtils.takeScreenshot(driver, scenario.getName() + "_end");

        if (scenario.isFailed()) {
            test.log(Status.FAIL, "Scenario FAILED: " + scenario.getName());
            try {
                test.fail("Screenshot on failure:",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } catch (Exception e) {
                System.err.println("Could not attach screenshot to report: " + e.getMessage());
            }
        } else {
            test.log(Status.PASS, "Scenario PASSED: " + scenario.getName());
            try {
                test.pass("Final screenshot:",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            } catch (Exception e) {
                System.err.println("Could not attach screenshot to report: " + e.getMessage());
            }
        }

        // Flush the report
        ExtentReportManager.flushReports();

        // Quit driver after each scenario to get a fresh browser for next scenario
        DriverManager.quitDriver();
    }

    @AfterStep
    public void afterEachStep(Scenario scenario) {
        // Take screenshot after every step and attach to report
        try {
            WebDriver currentDriver = DriverManager.getDriver();
            String screenshotPath = ScreenshotUtils.takeScreenshot(currentDriver, "step");
            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                test.log(Status.INFO, "Step screenshot:",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }
        } catch (Exception e) {
            System.err.println("AfterStep screenshot failed: " + e.getMessage());
        }
    }
}
