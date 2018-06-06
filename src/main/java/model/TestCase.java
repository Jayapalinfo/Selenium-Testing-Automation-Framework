package model;

import java.util.ArrayList;
import java.util.List;

/**
 * The TestCase class is used as a bean class to get the details of each test case
 * @author KaruppanadarJ
 *
 */
public class TestCase {
	private String testCaseName = null;
	private List<String> testStepName = new ArrayList<>();
	private List<String> testStepId = new ArrayList<>();
	private List<String> methodType = new ArrayList<>();
	private List<String> ojectNameFromPropertiesFile = new ArrayList<>();
	private List<String> actionType = new ArrayList<>();
	private String onFail = null;
	private List<String> testData = new ArrayList<>();
	private List<String> skipTest = new ArrayList<>();
	
	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public void addTestStepId(String steps) {
		testStepId.add(steps);
	}

	public List<String> getTestStepId() {
		return testStepId;
	}

	public void setMethodType(String methodype) {
		methodType.add(methodype);
	}

	public List<String> getMethodType() {
		return methodType;
	}

	public void setObjectNameFromPropertiesFile(String name) {
		ojectNameFromPropertiesFile.add(name);
	}

	public List<String> getObjectNameFromPropertiesFile() {
		return ojectNameFromPropertiesFile;
	}

	public void setActionType(String actiontype) {
		actionType.add(actiontype);
	}

	public List<String> getActionType() {
		return actionType;
	}

	public void setTestData(String testdata) {
		testData.add(testdata);
	}

	public List<String> getTestData() {
		return testData;
	}

	public List<String> getTestStepName() {
		return testStepName;
	}

	public void setTestStepName(String stepName) {
		testStepName.add(stepName);
	}

	public String getOnFail() {
		return onFail;
	}

	public void setOnFail(String onFail) {
		this.onFail = onFail;
	}

	/**
	 * @return the skipTest
	 */
	public List<String> getSkipTest() {
		return skipTest;
	}

	/**
	 * @param skipTest the skipTest to set
	 */
	public void setSkipTest(String skipTest) {
		this.skipTest.add(skipTest);
	}

}
