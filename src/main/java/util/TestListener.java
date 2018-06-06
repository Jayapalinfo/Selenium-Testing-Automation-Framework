package util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * The TestListener class is used to help in generating html Report
 * 
 * @author KaruppanadarJ
 *
 */
public class TestListener implements ITestListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestListener.class);
	private static final String LOG_TEXT = "About to end executing Test ";

	@Override
	public void onTestFailure(ITestResult result) {
		printTestResults(result);
		String methodName = result.getName().trim();
		takeScreenShot(methodName);
	}

	/**
	 * Capturing the screenshot of a page
	 * 
	 * @param methodName
	 */
	public void takeScreenShot(String methodName) {

		WebDriver driver = WebDriverClass.getDriver();
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String configPath = System.getProperty(Constant.TEST_USER_DIR);

		try {
			DateFormat dateFormat = new SimpleDateFormat("dd_MMM_yyyy__hh_mm_ssaa");
			Date date = new Date();

			FileUtils.copyFile(scrFile, new File(
					configPath + "\\SCREENSHOT\\OnFailure\\" + methodName + "__" + dateFormat.format(date) + ".png"));

		} catch (IOException e) {
			LOGGER.error("Io Exception occured"+e.getMessage());
		}
	}

	@Override
	public void onFinish(ITestContext arg0) {
		Reporter.log(LOG_TEXT + arg0.getName(), true);

	}

	@Override
	public void onStart(ITestContext arg0) {
		Reporter.log("About to begin executing Test " + arg0.getName(), true);

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		Reporter.log(LOG_TEXT + arg0.getName(), true);
	}

	@Override
	public void onTestSkipped(ITestResult arg0) {
		Reporter.log(LOG_TEXT + arg0.getName(), true);
	}

	@Override
	public void onTestStart(ITestResult arg0) {
		Reporter.log(LOG_TEXT + arg0.getName(), true);
	}

	@Override
	public void onTestSuccess(ITestResult arg0) {
		printTestResults(arg0);

	}

	private void printTestResults(ITestResult result) {

		if (result.getParameters().length != 0) {

			StringBuilder params = new StringBuilder();

			for (Object parameter : result.getParameters()) {

				params.append(parameter.toString()).append(",");
			}
			Reporter.log("Parameters: " + params);
		}

		String status = null;

		switch (result.getStatus()) {

		case ITestResult.SUCCESS:

			status = "Pass";

			break;

		case ITestResult.FAILURE:

			status = "Failed";

			break;

		case ITestResult.SKIP:

			status = "Skipped";

			break;

		default:
			status = "";
			break;

		}

		Reporter.log("Test Status: " + status, true);

	}

}