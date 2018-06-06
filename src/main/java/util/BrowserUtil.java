package util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.EdgeDriverManager;
import io.github.bonigarcia.wdm.InternetExplorerDriverManager;

/**
 * This BrowserUtil class is used to load the browser based on the name
 * 
 * @author KaruppanadarJ
 *
 */
public class BrowserUtil {
	private static WebDriver driver = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(BrowserUtil.class);

	public enum Browsers {
		CHROME, IE, EDGE
	}

	public static WebDriver openBrowser(String value) {
		Browsers browserName;
		try {
			browserName = Browsers.valueOf(value.toUpperCase());
			switch (browserName) {
			case CHROME:
				ChromeDriverManager.getInstance().setup();
				LOGGER.info("New ChromeDriver instantiated");
				driver = new ChromeDriver();
				LOGGER.info("Web application launched successfully using Chrome");
				break;
			case IE:
				InternetExplorerDriverManager.getInstance().setup();
				LOGGER.info("New InternetExplorerDriver instantiated");
				driver = new InternetExplorerDriver();
				LOGGER.info("Web application launched successfully using InternetExplorer");
				break;
			case EDGE:
				EdgeDriverManager.getInstance().setup();
				LOGGER.info("New EdgeDriver instantiated");
				driver = new EdgeDriver();
				LOGGER.info("Web application launched successfully using InternetExplorer");
				break;
			default:
				LOGGER.info("New FirefoxDriver instantiated");
				firefoxProfile();
				break;

			}

		} catch (Exception e) {
			LOGGER.info("Class Utils | Method OpenBrowser | Exception desc : " + e.getMessage());
		}
		driver.manage().window().maximize();
		return driver;
	}

	/**
	 * Firefox profile will help in automatic download of files
	 */
	private static void firefoxProfile() {

		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.folderList", 1);
		profile.setPreference("browser.download.manager.showWhenStarting", false);

		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document");

		driver = new FirefoxDriver(profile);
		driver.manage().window().maximize();
		LOGGER.info("Web application launched successfully using Firefox");
	}

}
