package util;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;

/**
 * The WebDriverClass class is used to generate a single instance of driver to
 * launch the browser
 * 
 * @author KaruppanadarJ
 *
 */
public class WebDriverClass {
	private static WebDriver driver;

	private WebDriverClass() {
		WebDriverClass.getDriver();
	}

	/**
	 * Sets the amount of time to wait for a page load to complete before throwing
	 * an error. If the timeout is negative, page loads can be indefinite.
	 * 
	 * @return WebDriver
	 */
	public static WebDriver getDriver() {
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		return driver;
	}

	/**
	 * Set Web Driver
	 * 
	 * @param driver
	 */
	public static void setDriver(WebDriver driver) {
		WebDriverClass.driver = driver;
	}

	/**
	 * Getting the instance of the web driver
	 * 
	 * @return WebDriver
	 */
	public static WebDriver getInstance() {
		if (driver == null) {
			driver = (WebDriver) new WebDriverClass();
			return driver;
		} else {
			return driver;
		}
	}
}
