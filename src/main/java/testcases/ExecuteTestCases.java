package testcases;

import java.awt.event.WindowEvent;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import util.BrowserUtil;
import util.Constant;
import util.ExcelAction;
import util.ExcelLibrary;
import util.ReadConfigProperty;
import util.SwingUI;
import util.WebDriverClass;

/**
 * The ExecuteTestCases class is used to execute the test cases
 * 
 * @author KaruppanadarJ
 *
 */
public class ExecuteTestCases implements ITest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteTestCases.class);

	private ReadConfigProperty config = new ReadConfigProperty();
	private ExcelLibrary lib = new ExcelLibrary();

	private  ExcelAction act = new ExcelAction();

	protected String mTestCaseName = "";

	/**
	 * In this class, this is the first method to be executed. Reading testsuite ,
	 * test case sheet and data sheet and storing the values in Hashmap
	 **/
	@BeforeClass
	public void setup() {
		LOGGER.info(ExecuteTestCases.class.getName() + "   setup() method called");

		ExcelAction.readTestSuite();
		act.readTestCaseInExcel();
		act.readTestDataSheet();
		act.readCapturedObjectProperties();

		/**
		 * Selecting which browser to be executed
		 **/
		String browserName = config.getConfigValues(Constant.TEST_BROWSER_NAME);
		WebDriver driver = BrowserUtil.openBrowser(browserName);

		LOGGER.info(config.getConfigValues(Constant.TEST_BASE_URL));
		/**
		 * launching the url
		 **/
		driver.get(config.getConfigValues(Constant.TEST_BASE_URL));
		WebDriverClass.setDriver(driver);
	}

	/**
	 * To override the test case name in the report
	 * 
	 * @param testData
	 */
	@BeforeMethod(alwaysRun = true)
	public void testData(Object[] testData) {
		String testCase = "";
		if (testData != null && testData.length > 0) {
			String testName = null;
			// Check if test method has actually received required parameters
			for (Object tstname : testData) {
				if (tstname instanceof String) {
					testName = (String) tstname;
					break;
				}
			}
			testCase = testName;
		}
		this.mTestCaseName = testCase;
	}

	public String getTestName() {
		return this.mTestCaseName;
	}

	public void setTestName(String name) {
		this.mTestCaseName = name;
	}

	/**
	 * All the test cases execution will start from here, which will take the input
	 * from the data provider
	 * 
	 * @param testName
	 */
	@Test(dataProvider = "dp")
	public void testSample1(String testName) {
		LOGGER.info("Test method called  ", testName);

		try {
			this.setTestName(testName);
			LOGGER.info("testSuiteIterate() calling");
			act.testSuiteIterate(testName);
			Reporter.log("ex" + getTestName());

		} catch (Exception ex) {
			Assert.fail();
			stack(ex);

			Reporter.log("exception in execute testCase==" + ex);

		}
	}

	/**
	 * Set the rest result name
	 * 
	 * @param result
	 */
	@AfterMethod
	public void setResultTestName(ITestResult result) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, mTestCaseName);
			Reporter.log(bm.getMethodName());
			this.mTestCaseName = " ";

		} catch (Exception ex) {
			stack(ex);
			Reporter.log("ex" + ex.getMessage());
		}
	}

	/**
	 * Parameterization in testng
	 * 
	 * @return
	 */
	@DataProvider(name = "dp")
	public Object[][] regression() {
		List<String> listOfTestCases = act.getListOfTestCases();
		Object[][] data = new Object[listOfTestCases.size()][1];
		String caseses = Arrays.toString(data);
		LOGGER.info(" TestCases to be executed  ", caseses);
		for (int i = 0; i < data.length; i++) {
			data[i][0] = listOfTestCases.get(i);
		}
		return data;
	}

	/**
	 * Clean up the resource
	 */
	@AfterClass
	public void cleanup() {
		act.clean();
		lib.clean();
		SwingUI.frame.dispose();
		SwingUI.frame.dispatchEvent(new WindowEvent(SwingUI.frame, WindowEvent.WINDOW_CLOSING));

	}

	/**
	 * Log the stack trace
	 * 
	 * @param e
	 */
	public static void stack(Exception e) {
		LOGGER.info(ExecuteTestCases.class.getName() + " Exception occured  " + e.getCause());
	}
}
