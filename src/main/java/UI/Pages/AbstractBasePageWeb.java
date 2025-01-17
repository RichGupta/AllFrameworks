package UI.Pages;

import UIHelper.DriverHandler;
import Utility.ExtentReport.ExtentLogger;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class AbstractBasePageWeb {

    public WebDriver driver;
    public static ExtentLogger log = ExtentLogger.getLogger();

    public AbstractBasePageWeb(){
        this.driver = DriverHandler.getWebDriver().get();
    }

    public static TestPage testPage;
    public static LandingPage landingPage;
    public static LoginPage loginPage;
    /**
     * Returns the object of the specified page/pages
     * User needs to pass exact page name
     *
     * @param pages - UI page name
     */
    public void returnPages(String... pages) {
        for (Field field : AbstractBasePageWeb.class.getDeclaredFields()) {
            for (String page : pages) {
                try {
                    if (field.getType().getName().split("\\.")[2].equals(page)) {
                        Binding binding = new Binding();
                        GroovyShell shell = new GroovyShell(binding);
                        shell.evaluate(String.valueOf(field).split(" ")[3]
                                + "= new "
                                + String.valueOf(field).split(" ")[2] + "();");
                        log.info("created object for:- \n"+ String.valueOf(field).split(" ")[3]+ " = new "+ String.valueOf(field).split(" ")[2] + "();");
						shell.evaluate(String.valueOf(field).split(" ")[3]+".driver = new com.akrivia.utility.DriverHandler().getDriver();");
                    }
                } catch (Exception e) {
//					Log.info(field.getType().getName()+" :: Error - "+e.getMessage());
                    // Escaping all non-page related objects
                }
            }
        }
    }

    public void goToURL(String URL){
        log.info("Opened Chrome");
        driver.manage().window().maximize();
        driver.get(URL);
    }

    public void openBrowser(String browser){
        driver = DriverHandler.getDriver(browser);
    }

    /**
     * Checks whether element is present or not using By locator
     * @param locator - By locator
     * @return - boolean
     */
    public boolean isElementPresent(By locator) {
        WebElement newEle = findByWait(locator);
        if (newEle == null)
            return false;
        else
            return true;
    }

    /**
     * Finds the web element using By locator
     * @param locatn - By locator
     * @return WebElement
     */
    public WebElement findByWait(By locatn) {
        WebElement element = null;
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {
            WebDriverWait wait = new WebDriverWait(driver, 60);
            wait.until(ExpectedConditions.presenceOfElementLocated(locatn));
            element = driver.findElement(locatn);
            log.info("found element " + element);
        } catch (Exception e) {
            log.info("not able to find element : " + e.getMessage());
        }
        return element;
    }

    /**
     * Enters input value on specified field
     * @param locator - By Locator
     * @param inputText - String Value
     */
    public void enterText(By locator, String inputText) {
        WebElement element = findByWait(locator);
        element.clear();
        element.sendKeys(inputText);
        log.info("typed text: " + inputText);
    }

    /**
     * Clicks on Web Element using WebElement
     * @param element - WebElement
     */
    public void clickElement(WebElement element) {
        log.info("clicking on element " + element);
        element.click();
    }

    /**
     * Clicks on web Element using By locator
     * @param loc - By locator
     */
    public void clickElement(By loc) {
        WebElement element = findByWait(loc);
        log.info("clicking on element " + element);
        element.click();
    }

    /**
     * Scrolls to certain element using By locator
     * @param locator - By locator
     */

    public void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        int i = 0;
        while (true) {
            if (driver.findElement(locator).isDisplayed())
                break;
            else if (i < 5) {
                log.info("searching element by scroll up");
                ((JavascriptExecutor) driver).executeScript("scroll(0,100)");
                i++;
            } else if (i < 10) {
                log.info("searching element by scroll down");
                ((JavascriptExecutor) driver).executeScript("scroll(0,100)");
                i++;
            } else {
                log.info("not able to find element by scrolling up and down");
                break;
            }
        }
    }
}
