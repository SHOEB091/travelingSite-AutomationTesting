package com.goibibo.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestRunner - Cucumber + TestNG runner class.
 *
 * This class ties together:
 *  - Feature files (in src/test/resources/features)
 *  - Step definitions (in com.goibibo.stepDefs package)
 *  - Extent Reports plugin (via extent.properties)
 *
 * To run all tests:   mvn test
 * To run only cabs:   mvn test -Dcucumber.filter.tags="@CabBooking"
 * To run only hotels: mvn test -Dcucumber.filter.tags="@HotelBooking"
 * To run gift card:   mvn test -Dcucumber.filter.tags="@GiftCard"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.goibibo.stepDefs"},
        tags = "@GoibiboTests",
        plugin = {
                "pretty",
                "html:test-output/cucumber-report.html",
                "json:test-output/cucumber-report.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * This override runs scenarios sequentially (not in parallel).
     * Remove the override to allow parallel execution.
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
