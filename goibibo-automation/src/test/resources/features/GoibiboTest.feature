@GoibiboTests
Feature: Goibibo Automation - Cab, Hotel and Gift Card booking flow

  Background:
    Given I open the Goibibo website

  @CabBooking
  Scenario: Book a one way outstation cab from Delhi Cantt to Manali and select SUV
    When I navigate to the Cabs section
    And I select "Outstation One-way" trip type
    And I enter "Delhi Cantt" in the From location
    And I select "Delhi Cantt Railway Junction" from the suggestions
    And I enter "Manali" in the To location
    And I select "Manali, Himachal Pradesh" from the suggestions
    And I set pickup date to "10/06/2026"
    And I set pickup time to "10:30 AM"
    And I click the Search button for cabs
    And I close any popups on the cab listing page
    And I select the SUV cab type from filters
    Then the cab results should be displayed with SUV type

  @HotelBooking
  Scenario: Search for hotels in Manali Mall Road with 4 adults
    When I navigate to the Hotels section
    And I enter "Manali Mall Road" in hotel search
    And I select "Mall Road, Manali" from hotel suggestions
    And I set hotel check-in date to "11/06/2026"
    And I set hotel check-out date to "15/06/2026"
    And I set number of adults to 4
    And I click Search for hotels
    Then the hotel results page should be displayed

  @GiftCard
  Scenario: Navigate to Gift Cards from the login menu
    When I click on the Login Signup button
    Then I should see the Gift Cards option in the menu
    And I click on Gift Cards
    Then the Gift Cards page should be displayed
