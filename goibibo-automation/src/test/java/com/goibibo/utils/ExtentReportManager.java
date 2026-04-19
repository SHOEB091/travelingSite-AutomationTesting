package com.goibibo.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * ExtentReportManager - Manages Extent Reports lifecycle.
 * Creates a single ExtentReports instance and provides ExtentTest for each test.
 */
public class ExtentReportManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    /**
     * Initializes Extent Reports with a Spark reporter.
     * Saves the report to test-output/ExtentReport.html
     */
    public static ExtentReports getExtentReports() {
        if (extent == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
            sparkReporter.config().setDocumentTitle("Goibibo Automation Test Report");
            sparkReporter.config().setReportName("Goibibo - BDD Cucumber Test Results");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setEncoding("UTF-8");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            extent.setSystemInfo("Application", "Goibibo");
            extent.setSystemInfo("URL", "https://www.goibibo.com");
            extent.setSystemInfo("Browser", "Chrome");
            extent.setSystemInfo("Framework", "Cucumber BDD + TestNG + Page Object Model");
            extent.setSystemInfo("Developer", "QA Automation Team");
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void setTest(ExtentTest test) {
        extentTest.set(test);
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}
