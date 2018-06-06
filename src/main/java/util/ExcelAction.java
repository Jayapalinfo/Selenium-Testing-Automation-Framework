package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Reporter;

import actions.MethodType;
import model.CapturedObjectPropModel;
import model.MethodParameters;
import model.TestCase;

/**
 * The ExcelAction class is used to store the data from the excel into map
 * 
 * @author KaruppanadarJ
 *
 */
public class ExcelAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelAction.class);

	private static final String METHOD_TYPE = "methodType=";

	private ExcelLibrary excel = new ExcelLibrary();
	private static ReadConfigProperty config = new ReadConfigProperty();
	private Map<String, Object> testCaseSheet = new HashMap<>();

	private static Map<String, Object> testSuiteSheet = new HashMap<>();
	private Map<String, Map<String, List<String>>> testDataSheet = new HashMap<>();
	private Map<String, Map<String, CapturedObjectPropModel>> capObjPropSheet = new HashMap<>();

	private static List<String> listOfTestCases = new ArrayList<>();
	private MethodType methodtype = new MethodType();

	public static void main(String[] args) {
		ExcelAction action = new ExcelAction();
		action.readCapturedObjectProperties();
		action.readLocators("PAGE", "SEARCH_BOX");
	}

	public List<String> getListOfTestCases() {
		return listOfTestCases;
	}

	/**
	 * Read test data sheet
	 */
	public void readTestDataSheet() {

		String sheetName;
		String pathOFFile = config.getConfigValues(Constant.TEST_CONFIG_FILE_PATH)
				+ config.getConfigValues(Constant.TEST_CASE_FILE_NAME);
		List<String> list = ExcelLibrary.getNumberOfSheetsinTestDataSheet(pathOFFile);
		for (int i = 0; i < list.size(); i++) {
			sheetName = list.get(i);
			Map<String, List<String>> temp1 = new HashMap<>();

			Reporter.log("sheetName" + sheetName + "----" + "sheetName, pathOFFile" + pathOFFile);
			List<String> listColumnNames = ExcelLibrary.getColumnNames(sheetName, pathOFFile,
					ExcelLibrary.getColumns(sheetName, pathOFFile));
			// iterate through columns in sheet
			for (int j = 0; j < listColumnNames.size(); j++) {
				// get Last Row for each Column
				int row = 1;
				List<String> listColumnValues = new ArrayList<>();
				do {
					listColumnValues.add(ExcelLibrary.readCell(row, j, sheetName, pathOFFile));
					row++;
				} while ((ExcelLibrary.readCell(row, j, sheetName, pathOFFile)) != null);
				temp1.put((String) listColumnNames.get(j), listColumnValues);
			}
			listColumnNames.clear();

			testDataSheet.put(sheetName, temp1);
		}
	}

	/**
	 * Iterate over each row in testcase sheet and pass the data to execute method
	 * in MethodType.java
	 */
	public void testSuiteIterate(String tcName) {
		LOGGER.info("testSuiteIterate() called : ", tcName);
		String key = tcName;

		TestCase temp = (TestCase) testCaseSheet.get(key);
		List<String> testStepId = temp.getTestStepName();
		Reporter.log("size====" + testStepId.size());
		List<String> dataColValues = null;
		int noOfExecution = 0;
		for (int i = 0; i < testStepId.size(); i++) {
			if (!(temp.getTestData().get(i).isEmpty())) {
				if (temp.getTestData().get(i).contains(".")) {

					String data = temp.getTestData().get(i);
					String[] testDataArray = data.split("\\.");

					dataColValues = getColumnValue(testDataArray);

					noOfExecution = dataColValues.size();

					break;
				}
			} else {
				noOfExecution = 0;
			}
		}
		LOGGER.info("columnValue addedd newly numberOfTimesExecution=== ", dataColValues);
		LOGGER.info("testCaseExecution== ", noOfExecution);

		if (noOfExecution != 0) {
			for (int execution = 0; execution < noOfExecution; execution++) {
				for (int i = 0; i < testStepId.size(); i++) {

					String methodType = temp.getMethodType().get(i);
					String objectLocators = temp.getObjectNameFromPropertiesFile().get(i);
					String actionType = temp.getActionType().get(i);

					// Data Sheet logic
					if (!(temp.getTestData().get(i).isEmpty())) {
						if (temp.getTestData().get(i).contains(".")) {

							String data = temp.getTestData().get(i);
							String[] testDataArray = data.split("\\.");

							List<String> columnValue = getColumnValue(testDataArray);

							Reporter.log("column valueee======" + columnValue);
							Reporter.log("column value size===========" + columnValue.size());
							try {
								Reporter.log("testCaseExecution======================" + noOfExecution);
								List<String> list = readLocators(methodType, objectLocators);

								methodType = list.get(0);
								objectLocators = list.get(1);
								LOGGER.info(METHOD_TYPE, methodType);
								LOGGER.info("objectLocators as name=", objectLocators);

								methodtype.methodExecutor(methodType, objectLocators, actionType,
										columnValue.get(execution));

							} catch (IndexOutOfBoundsException e) {
								String s = e.getMessage();
								throw new IndexOutOfBoundsException(
										"data column is blank..Please enter value in datasheet" + s);
							}

						}

						if (execution == noOfExecution) {
							break;
						}
					} else {
						List<String> list = readLocators(methodType, objectLocators);
						methodType = list.get(0);
						objectLocators = list.get(1);
						LOGGER.info(METHOD_TYPE, methodType);
						methodtype.methodExecutor(methodType, objectLocators, actionType, null);
					}
				}
				if (execution == noOfExecution) {
					break;
				}
			}

		} else {
			for (int i = 0; i < testStepId.size(); i++) {

				String methodType = temp.getMethodType().get(i);
				String objectLocators = temp.getObjectNameFromPropertiesFile().get(i);
				String actionType = temp.getActionType().get(i);
				List<String> list = readLocators(methodType, objectLocators);
				methodType = list.get(0);
				objectLocators = list.get(1);
				LOGGER.info(METHOD_TYPE, methodType);
				LOGGER.info("objectLocators=", objectLocators);
				MethodParameters methodParameters = new MethodParameters();
				methodParameters.setData(null);

				methodtype.methodExecutor(methodType, objectLocators, actionType, null);
			}
		}
	}

	private List<String> getColumnValue(String[] testDataArray) {
		Map<String, List<String>> dataSheet = testDataSheet.get(testDataArray[0]);
		return dataSheet.get(testDataArray[1]);
	}

	/**
	 * populate data to testSuitedata and listOfTestCases to be executed
	 */
	public static void readTestSuite() {
		Map<String, String> readFromConfigFile = config.readConfigFile();

		for (String suiteName : readFromConfigFile.values()) {

			String testSuiteFilePath = config.getConfigValues(Constant.TEST_CONFIG_FILE_PATH)
					+ config.getConfigValues(Constant.TEST_SUITE_FILE_NAME);

			List<String> suiteSheets = ExcelLibrary.getNumberOfSheetsinSuite(testSuiteFilePath);

			for (int i = 0; i < suiteSheets.size(); i++) {
				String sheetName = suiteSheets.get(i);
				if (suiteName.trim().equalsIgnoreCase(sheetName)) {
					Map<String, Object> temp1 = new HashMap<>();
					for (int row = 1; row <= ExcelLibrary.getRows(sheetName, testSuiteFilePath); row++) {
						String testCaseName = ExcelLibrary.readCell(row, 0, suiteName.trim(), testSuiteFilePath);
						String testCaseState = ExcelLibrary.readCell(row, 1, suiteName.trim(), testSuiteFilePath);
						if (("No").equalsIgnoreCase(testCaseState)) {
							listOfTestCases.add(testCaseName);
						}
						temp1.put(testCaseName, testCaseState);
					}
					Reporter.log("listOfTestCases=============*****************" + listOfTestCases);
					testSuiteSheet.put(suiteName, temp1);
				}
			}
		}

	}

	/**
	 * Read the content of the excel testcase sheet and store the data in model and
	 * store this model in hashmap
	 */
	public void readTestCaseInExcel() {

		String pathOFFile = config.getConfigValues(Constant.TEST_CONFIG_FILE_PATH)
				+ config.getConfigValues(Constant.TEST_CASE_FILE_NAME);
		String testCaseSheetName = config.getConfigValues(Constant.TEST_CASE_SHEET_NAME);

		TestCase tc = null;
		for (int row = 1; row <= ExcelLibrary.getRows(testCaseSheetName, pathOFFile); row++) {

			if (!(ExcelLibrary.readCell(row, 0, testCaseSheetName, pathOFFile).isEmpty())) {

				tc = new TestCase();
				tc.setTestCaseName(ExcelLibrary.readCell(row, 0, testCaseSheetName, pathOFFile));
				tc.setTestStepName(ExcelLibrary.readCell(row, 2, testCaseSheetName, pathOFFile));
				tc.setMethodType(ExcelLibrary.readCell(row, 3, testCaseSheetName, pathOFFile));
				tc.setObjectNameFromPropertiesFile(ExcelLibrary.readCell(row, 4, testCaseSheetName, pathOFFile));
				tc.setActionType(ExcelLibrary.readCell(row, 5, testCaseSheetName, pathOFFile));
				tc.setOnFail(ExcelLibrary.readCell(row, 6, testCaseSheetName, pathOFFile));
				tc.setTestData(ExcelLibrary.readCell(row, 7, testCaseSheetName, pathOFFile));
				if (("No").equalsIgnoreCase(ExcelLibrary.readCell(row, 8, testCaseSheetName, pathOFFile))) {
					testCaseSheet.put(ExcelLibrary.readCell(row, 0, testCaseSheetName, pathOFFile), tc);
				}

			} else {
				if (("No").equalsIgnoreCase(ExcelLibrary.readCell(row, 8, testCaseSheetName, pathOFFile))
						&& null != tc) {
					tc.setTestStepName(ExcelLibrary.readCell(row, 2, testCaseSheetName, pathOFFile));
					tc.setMethodType(ExcelLibrary.readCell(row, 3, testCaseSheetName, pathOFFile));
					tc.setObjectNameFromPropertiesFile(ExcelLibrary.readCell(row, 4, testCaseSheetName, pathOFFile));
					tc.setActionType(ExcelLibrary.readCell(row, 5, testCaseSheetName, pathOFFile));
					tc.setOnFail(ExcelLibrary.readCell(row, 6, testCaseSheetName, pathOFFile));
					tc.setTestData(ExcelLibrary.readCell(row, 7, testCaseSheetName, pathOFFile));
				}
			}
		}

	}

	public void clean() {
		excel.clean();

	}

	/**
	 * Capture object properties in excel sheet
	 */
	public void readCapturedObjectProperties1() {

		String pathOFFile = config.getConfigValues(Constant.TEST_CONFIG_FILE_PATH)
				+ config.getConfigValues(Constant.TEST_CASE_FILE_NAME);
		String testCaseObjectSheetName = config.getConfigValues(Constant.TEST_CASE_OBJECT_SHEET_NAME);
		LOGGER.info("testCasePath==", pathOFFile);
		int totrows = ExcelLibrary.getRows(testCaseObjectSheetName, pathOFFile);
		LOGGER.info("total rows=", totrows);

		String prevPagename = null;
		Map<String, CapturedObjectPropModel> pageInfo = null;
		for (int j = 1; j <= totrows; j++) {
			String pagename = ExcelLibrary.readCell(j, 0, testCaseObjectSheetName, pathOFFile);
			String name = ExcelLibrary.readCell(j, 1, testCaseObjectSheetName, pathOFFile);
			String property = ExcelLibrary.readCell(j, 2, testCaseObjectSheetName, pathOFFile);
			String value = ExcelLibrary.readCell(j, 3, testCaseObjectSheetName, pathOFFile);
			pageInfo = new HashMap<>();
			CapturedObjectPropModel capModel = new CapturedObjectPropModel();
			capModel.setPage(pagename);
			capModel.setName(name);
			capModel.setProperty(property);
			capModel.setValue(value);
			pageInfo.put(name, capModel);
			prevPagename = pagename;

			if (prevPagename != null) {
				capObjPropSheet.put(prevPagename, pageInfo);
			}
		}
	}

	/**
	 * Capture object properties in excel sheet
	 */
	public void readCapturedObjectProperties() {

		String pathOFFile = config.getConfigValues(Constant.TEST_CONFIG_FILE_PATH)
				+ config.getConfigValues(Constant.TEST_CASE_FILE_NAME);
		String testCaseObjectSheetName = config.getConfigValues(Constant.TEST_CASE_OBJECT_SHEET_NAME);
		LOGGER.info("testCasePath==", pathOFFile);
		int totrows = ExcelLibrary.getRows(testCaseObjectSheetName, pathOFFile);
		LOGGER.info("total rows=", totrows);

		String prevPagename = "";
		Map<String, CapturedObjectPropModel> pageInfo = null;
		for (int j = 1; j <= totrows; j++) {
			String pagename = ExcelLibrary.readCell(j, 0, testCaseObjectSheetName, pathOFFile);

			if (null != prevPagename && prevPagename.equals(pagename) && null != pageInfo) {

				String page = ExcelLibrary.readCell(j, 0, testCaseObjectSheetName, pathOFFile);
				String name = ExcelLibrary.readCell(j, 1, testCaseObjectSheetName, pathOFFile);
				String property = ExcelLibrary.readCell(j, 2, testCaseObjectSheetName, pathOFFile);
				String value = ExcelLibrary.readCell(j, 3, testCaseObjectSheetName, pathOFFile);

				CapturedObjectPropModel capModel = new CapturedObjectPropModel();
				capModel.setPage(page);
				capModel.setName(name);
				capModel.setProperty(property);
				capModel.setValue(value);
				LOGGER.info(capModel.getPage() + "  " + capModel.getName() + "  " + capModel.getValue() + "  "
						+ capModel.getProperty());
				pageInfo.put(name, capModel);

			} else {
				if (prevPagename != null) {
					capObjPropSheet.put(prevPagename, pageInfo);
				}
				pageInfo = new HashMap<>();
				String name = ExcelLibrary.readCell(j, 1, testCaseObjectSheetName, pathOFFile);
				String property = ExcelLibrary.readCell(j, 2, testCaseObjectSheetName, pathOFFile);
				String value = ExcelLibrary.readCell(j, 3, testCaseObjectSheetName, pathOFFile);

				CapturedObjectPropModel capModel = new CapturedObjectPropModel();
				capModel.setPage(pagename);
				capModel.setName(name);
				capModel.setProperty(property);
				capModel.setValue(value);
				pageInfo.put(name, capModel);
				prevPagename = pagename;
			}

			if (prevPagename != null) {
				capObjPropSheet.put(prevPagename, pageInfo);
			}
		}
	}

	/**
	 * Capture object Locators in excel sheet
	 */
	public List<String> readLocators(String page, String name) {
		LOGGER.info(page);
		LOGGER.info(name);
		Map<String, CapturedObjectPropModel> temp = capObjPropSheet.get(page);
		List<String> locators = new ArrayList<>();
		LOGGER.info("objects" + capObjPropSheet.get(page));
		if (capObjPropSheet.get(page) != null) {

			LOGGER.info("name" + temp.get(name));
			CapturedObjectPropModel c = temp.get(name);
			LOGGER.info(c.getName());
			LOGGER.info("c.getPage()=" + c.getPage());

			if (c.getPage().equals(page) && c.getName().equals(name)) {
				locators.add(c.getProperty());
				locators.add(c.getValue());
				LOGGER.info("locators=", locators);
			}
		}
		LOGGER.info("size=", locators.size());
		return locators;
	}
}
