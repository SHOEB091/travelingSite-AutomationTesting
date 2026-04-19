package com.goibibo.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils - Takes screenshots and saves them to the screenshots folder.
 * Returns file path so Extent Reports can attach them.
 */
public class ScreenshotUtils {

    private static final String SCREENSHOT_FOLDER = "test-output/screenshots/";

    /**
     * Takes a screenshot and saves it with a timestamped name.
     *
     * @param driver   WebDriver instance
     * @param stepName Name describing the step (used in filename)
     * @return absolute file path of the saved screenshot
     */
    public static String takeScreenshot(WebDriver driver, String stepName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = stepName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
        String filePath = SCREENSHOT_FOLDER + fileName;

        try {
            File screenshotSrc = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destination = Paths.get(filePath);

            Files.createDirectories(destination.getParent());
            Files.copy(screenshotSrc.toPath(), destination);

            System.out.println("Screenshot saved: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }

        return filePath;
    }
}
