package testcases;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import util.ReadConfigProperty;
import util.SwingUI;

/**
 * The MainTestNG class is used to execute the jar This is the class from where
 * the execution gets started.
 * 
 * @author KaruppanadarJ
 *
 */
public class MainTestNG {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainTestNG.class);
	ReadConfigProperty config = new ReadConfigProperty();
	static SwingUI swing;

	public static void main(String[] args) {

		LOGGER.info("Logger Name: " + LOGGER.getName());

		/**
		 * SwingTest UI for properties file (Config.properties)
		 */
		//swing = new SwingUI()
		MainTestNG test = new MainTestNG();

		/**
		 * testNG execution starts here
		 */
		test.testng();

	}

	/**
	 * adding listners, setting test-output folder Mentioning the TestSuite Name
	 */
	public void testng() {
		// RegressionSuite
		LOGGER.info("testng");
		// TestListenerAdapter tla = new TestListenerAdapter()
		TestNG myTestNG = new TestNG();
		XmlSuite mySuite = new XmlSuite();
		mySuite.setName("Sample Suite");
		mySuite.addListener("org.uncommons.reportng.HTMLReporter");
		mySuite.addListener("org.uncommons.reportng.JUnitXMLReporter");

		mySuite.addListener("util.TestListener");

		myTestNG.setOutputDirectory("test-output");

		XmlTest myTest = new XmlTest(mySuite);
		myTest.setName("Sample Test");
		List<XmlClass> myClasses = new ArrayList<>();
		myClasses.add(new XmlClass("testcases.ExecuteTestCases"));
		myTest.setXmlClasses(myClasses);
		List<XmlTest> myTests = new ArrayList<>();
		myTests.add(myTest);
		mySuite.setTests(myTests);
		List<XmlSuite> mySuites = new ArrayList<>();

		mySuites.add(mySuite);
		myTestNG.setXmlSuites(mySuites);
		myTestNG.setUseDefaultListeners(true);

		//testng run method
		myTestNG.run();

		// Report generation--Generating XSLT report from ReportNG report
		// Report.report();
	}

}
