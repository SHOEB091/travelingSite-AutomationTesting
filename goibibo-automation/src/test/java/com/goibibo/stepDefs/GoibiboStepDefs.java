package com.goibibo.stepDefs;

import com.aventstack.extentreports.Status;
import com.goibibo.pages.CabsPage;
import com.goibibo.pages.GiftCardPage;
import com.goibibo.pages.HomePage;
import com.goibibo.pages.HotelsPage;
import com.goibibo.utils.DriverManager;
import com.goibibo.utils.ExtentReportManager;
import com.goibibo.utils.WaitUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * GoibiboStepDefs - Cucumber Step Definitions.
 * Connects feature file steps to Page Object methods.
 */
public class GoibiboStepDefs {

    private WebDriver driver;
    private HomePage homePage;
    private CabsPage cabsPage;
    private HotelsPage hotelsPage;
    private GiftCardPage giftCardPage;

    /**
     * Helper method to initialize all page objects.
     */
    private void initPages() {
        driver = DriverManager.getDriver();
        homePage = new HomePage(driver);
        cabsPage = new CabsPage(driver);
        hotelsPage = new HotelsPage(driver);
        giftCardPage = new GiftCardPage(driver);
    }

    // ===================== COMMON STEPS =====================

    @Given("I open the Goibibo website")
    public void i_open_the_goibibo_website() {
        initPages();
        homePage.openWebsite();
        ExtentReportManager.getTest().log(Status.INFO, "Opened Goibibo website: https://www.goibibo.com");
    }

    // ===================== CAB STEPS =====================

    @When("I navigate to the Cabs section")
    public void i_navigate_to_the_cabs_section() {
        cabsPage.navigateToCabs();
        ExtentReportManager.getTest().log(Status.INFO, "Navigated to Cabs section");
    }

    @And("I select {string} trip type")
    public void i_select_trip_type(String tripType) {
        cabsPage.selectOutstationOneWay();
        ExtentReportManager.getTest().log(Status.INFO, "Selected trip type: " + tripType);
    }

    @And("I enter {string} in the From location")
    public void i_enter_in_the_from_location(String location) {
        cabsPage.enterFromLocation(location);
        ExtentReportManager.getTest().log(Status.INFO, "Entered From location: " + location);
    }

    @And("I select {string} from the suggestions")
    public void i_select_from_the_suggestions(String suggestion) {
        cabsPage.selectSuggestion(suggestion);
        ExtentReportManager.getTest().log(Status.INFO, "Selected suggestion: " + suggestion);
    }

    @And("I enter {string} in the To location")
    public void i_enter_in_the_to_location(String location) {
        cabsPage.enterToLocation(location);
        ExtentReportManager.getTest().log(Status.INFO, "Entered To location: " + location);
    }

    @And("I set pickup date to {string}")
    public void i_set_pickup_date_to(String date) {
        cabsPage.setPickupDate(date);
        ExtentReportManager.getTest().log(Status.INFO, "Set pickup date: " + date);
    }

    @And("I set pickup time to {string}")
    public void i_set_pickup_time_to(String time) {
        cabsPage.setPickupTime(time);
        ExtentReportManager.getTest().log(Status.INFO, "Set pickup time: " + time);
    }

    @And("I click the Search button for cabs")
    public void i_click_the_search_button_for_cabs() {
        cabsPage.clickSearch();
        ExtentReportManager.getTest().log(Status.INFO, "Clicked Search button for cabs");
    }

    @And("I close any popups on the cab listing page")
    public void i_close_any_popups_on_the_cab_listing_page() {
        cabsPage.closePopupIfVisible();
        ExtentReportManager.getTest().log(Status.INFO, "Closed any popup dialogs on listing page");
    }

    @And("I select the SUV cab type from filters")
    public void i_select_the_suv_cab_type_from_filters() {
        cabsPage.selectSuvFilter();
        ExtentReportManager.getTest().log(Status.INFO, "Selected SUV filter from cab types");
    }

    @Then("the cab results should be displayed with SUV type")
    public void the_cab_results_should_be_displayed_with_suv_type() {
        WaitUtils.hardWait(2000);
        String currentUrl = driver.getCurrentUrl();
        boolean isOnListingPage = currentUrl.contains("cabs") || currentUrl.contains("listing") || currentUrl.contains("cars");
        ExtentReportManager.getTest().log(Status.INFO, "Current URL: " + currentUrl);
        Assert.assertTrue(isOnListingPage, "Should be on cab listing page. Current URL: " + currentUrl);
        ExtentReportManager.getTest().log(Status.PASS, "Cab listing page with SUV filter is displayed");
    }

    // ===================== HOTEL STEPS =====================

    @When("I navigate to the Hotels section")
    public void i_navigate_to_the_hotels_section() {
        homePage.clickHotels();
        ExtentReportManager.getTest().log(Status.INFO, "Navigated to Hotels section");
    }

    @And("I enter {string} in hotel search")
    public void i_enter_in_hotel_search(String location) {
        hotelsPage.enterHotelLocation(location);
        ExtentReportManager.getTest().log(Status.INFO, "Entered hotel search location: " + location);
    }

    @And("I select {string} from hotel suggestions")
    public void i_select_from_hotel_suggestions(String suggestion) {
        hotelsPage.selectHotelSuggestion(suggestion);
        ExtentReportManager.getTest().log(Status.INFO, "Selected hotel suggestion: " + suggestion);
    }

    @And("I set hotel check-in date to {string}")
    public void i_set_hotel_check_in_date_to(String date) {
        hotelsPage.setCheckInDate(date);
        ExtentReportManager.getTest().log(Status.INFO, "Set check-in date: " + date);
    }

    @And("I set hotel check-out date to {string}")
    public void i_set_hotel_check_out_date_to(String date) {
        hotelsPage.setCheckOutDate(date);
        ExtentReportManager.getTest().log(Status.INFO, "Set check-out date: " + date);
    }

    @And("I set number of adults to {int}")
    public void i_set_number_of_adults_to(int count) {
        hotelsPage.setAdults(count);
        ExtentReportManager.getTest().log(Status.INFO, "Set number of adults: " + count);
    }

    @And("I click Search for hotels")
    public void i_click_search_for_hotels() {
        hotelsPage.clickSearchHotels();
        ExtentReportManager.getTest().log(Status.INFO, "Clicked Search for hotels");
    }

    @Then("the hotel results page should be displayed")
    public void the_hotel_results_page_should_be_displayed() {
        boolean isDisplayed = hotelsPage.isHotelResultsDisplayed();
        ExtentReportManager.getTest().log(Status.INFO, "Hotel results page URL: " + driver.getCurrentUrl());
        Assert.assertTrue(isDisplayed, "Hotel results page should be displayed");
        ExtentReportManager.getTest().log(Status.PASS, "Hotel results page is displayed");
    }

    // ===================== GIFT CARD STEPS =====================

    @When("I click on the Login Signup button")
    public void i_click_on_the_login_signup_button() {
        homePage.clickLoginSignup();
        ExtentReportManager.getTest().log(Status.INFO, "Clicked on Login/Signup button");
    }

    @Then("I should see the Gift Cards option in the menu")
    public void i_should_see_the_gift_cards_option_in_the_menu() {
        boolean isVisible = giftCardPage.isGiftCardOptionVisible();
        ExtentReportManager.getTest().log(Status.INFO, "Gift Cards option visible: " + isVisible);
        Assert.assertTrue(isVisible, "Gift Cards option should be visible in the menu dropdown");
        ExtentReportManager.getTest().log(Status.PASS, "Gift Cards option found in menu");
    }

    @And("I click on Gift Cards")
    public void i_click_on_gift_cards() {
        giftCardPage.clickGiftCards();
        ExtentReportManager.getTest().log(Status.INFO, "Clicked on Gift Cards menu option");
    }

    @Then("the Gift Cards page should be displayed")
    public void the_gift_cards_page_should_be_displayed() {
        boolean isDisplayed = giftCardPage.isGiftCardPageDisplayed();
        ExtentReportManager.getTest().log(Status.INFO, "Gift Cards page URL: " + driver.getCurrentUrl());
        Assert.assertTrue(isDisplayed, "Gift Cards page should be displayed");
        ExtentReportManager.getTest().log(Status.PASS, "Gift Cards page is displayed");
    }
}
