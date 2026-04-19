# Goibibo Automation Framework

A complete Java-based test automation project for [www.goibibo.com](https://www.goibibo.com) using:
- **Cucumber BDD** (Gherkin feature files)
- **Page Object Model (POM)**
- **TestNG** (test runner)
- **Extent Reports** (rich HTML reports with screenshots)
- **Selenium WebDriver 4** (browser automation)
- **WebDriverManager** (auto-downloads ChromeDriver)

---

## Project Structure

```
goibibo-automation/
├── pom.xml                                         <- Maven dependencies
├── testng.xml                                      <- TestNG suite config
├── src/
│   └── test/
│       ├── java/
│       │   └── com/goibibo/
│       │       ├── pages/                          <- Page Object classes
│       │       │   ├── HomePage.java
│       │       │   ├── CabsPage.java
│       │       │   ├── HotelsPage.java
│       │       │   └── GiftCardPage.java
│       │       ├── stepDefs/                       <- Cucumber Step Definitions
│       │       │   ├── GoibiboStepDefs.java
│       │       │   └── Hooks.java
│       │       ├── runner/                         <- TestNG + Cucumber runner
│       │       │   └── TestRunner.java
│       │       └── utils/                          <- Utilities
│       │           ├── DriverManager.java
│       │           ├── WaitUtils.java
│       │           ├── ScreenshotUtils.java
│       │           └── ExtentReportManager.java
│       └── resources/
│           ├── features/
│           │   └── GoibiboTest.feature             <- BDD Scenarios (Gherkin)
│           └── extent.properties                   <- Extent Reports config
└── test-output/
    ├── ExtentReport.html                           <- Rich HTML report
    ├── cucumber-report.html                        <- Cucumber HTML report
    ├── cucumber-report.json
    └── screenshots/                                <- All step screenshots
```

---

## Scenarios Automated

### 1. Cab Booking (`@CabBooking`)
- Opens Goibibo → Cabs
- Selects **Outstation One-way**
- **From**: Delhi Cantt Railway Junction
- **To**: Manali, Himachal Pradesh
- **Pickup Date**: June 10, 2026
- **Pickup Time**: 10:30 AM
- Searches, closes popup, selects **SUV** from filters

### 2. Hotel Booking (`@HotelBooking`)
- Opens Goibibo → Hotels
- Searches **Manali Mall Road**
- **Check-in**: June 11, 2026
- **Check-out**: June 15, 2026
- **Adults**: 4
- Searches for hotels

### 3. Gift Cards (`@GiftCard`)
- Opens Goibibo home
- Clicks **Login / Signup** button
- Finds **Gift Cards** in dropdown menu
- Clicks Gift Cards and verifies the page

---

## Prerequisites

1. **Java 11+** installed
2. **Maven** installed (`mvn --version`)
3. **Google Chrome** browser installed (latest version recommended)
4. Internet connection to access www.goibibo.com

> ChromeDriver is auto-managed by **WebDriverManager** — no manual download needed!

---

## How to Run

### Run all tests:
```bash
mvn test
```

### Run only cab booking:
```bash
mvn test -Dcucumber.filter.tags="@CabBooking"
```

### Run only hotel booking:
```bash
mvn test -Dcucumber.filter.tags="@HotelBooking"
```

### Run only gift card scenario:
```bash
mvn test -Dcucumber.filter.tags="@GiftCard"
```

---

## Test Reports

After running, reports are generated in `test-output/`:

| Report | Location |
|--------|----------|
| Extent HTML Report | `test-output/ExtentReport.html` |
| Cucumber HTML Report | `test-output/cucumber-report.html` |
| Cucumber JSON Report | `test-output/cucumber-report.json` |
| Screenshots | `test-output/screenshots/` |

Open `test-output/ExtentReport.html` in any browser to see the detailed report with screenshots attached.

---

## Framework Highlights

- **WebDriverManager**: Automatically downloads the correct ChromeDriver version
- **Explicit Waits**: All interactions use `WebDriverWait` — no `Thread.sleep` in page actions
- **Screenshots**: Captured after every step and on test failure, attached to Extent Report
- **Popup Handling**: Automatic handling of booking assistance popups on cab listing page
- **Robust Locators**: Multiple XPath fallbacks for dynamic Goibibo UI
