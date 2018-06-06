package actions;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import model.MethodParameters;
import util.WebDriverClass;

/**
 * The MethodType class is used to identify the method specified in the testcase
 * excel and to perform the same action
 * 
 * @author KaruppanadarJ
 *
 */
public class MethodType {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodType.class);

	private List<WebElement> listOfElements = new ArrayList<>();

	private String alertText = null;
	private String titleOfPage = null;

	/**
	 * Object locators
	 * 
	 * @param methodType
	 * @param objectLocators
	 * @param actionType
	 * @param data
	 */
	public void methodExecutor(String methodType, String objectLocators, String actionType, String data) {
		MethodParameters mModel = new MethodParameters();
		mModel.setMethodType(methodType);
		mModel.setObjectLocators(objectLocators);
		mModel.setActionType(actionType);
		mModel.setData(data);

		if ("BMRC".equalsIgnoreCase(objectLocators)) {
			switchToChildWindow(mModel);
			maximizeWindow();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("methodType= ").append(methodType).append(" objectLocators=").append(objectLocators)
				.append("actionType=").append(actionType).append("data= ").append(data).toString();
		LOGGER.info("Inside methodExecutor :  ", sb);

		switch (methodType) {

		case "ID":
			findElementById(objectLocators);
			mModel.setElements(listOfElements);
			findMethod(actionType, mModel);
			break;
		case "NAME":
			findElementByName(objectLocators);
			mModel.setElements(listOfElements);
			findMethod(actionType, mModel);
			break;
		case "XPATH":
			findElementByXpath(objectLocators);
			mModel.setElements(listOfElements);
			findMethod(actionType, mModel);
			break;
		case "CSS":
			findElementByCssSelector(objectLocators);
			mModel.setElements(listOfElements);
			findMethod(actionType, mModel);
			break;
		case "LINKTEXT":
			findElementByLinkText(objectLocators);
			mModel.setElements(listOfElements);
			findMethod(actionType, mModel);
			break;
		case "GOTO":
			WebDriverClass.getDriver().get(data);
			break;
		default:
			if (actionType.contains(":")) {
				String[] actsplit = actionType.split(":");
				mModel.setActionType(actsplit[1]);
				actionType = actsplit[0];
			}
			findMethod(actionType, mModel);
			break;
		}

	}

	/**
	 * Identifying the method at run time
	 * 
	 * @param actionType
	 * @param data
	 * @param model
	 */
	public void findMethod(String actionType, MethodParameters model) {
		Class<?> cl = null;
		try {
			cl = Class.forName("actions.MethodType");
			actions.MethodType clName = (MethodType) cl.newInstance();
			Method[] methods = cl.getMethods();
			Method methodName = findMethods(actionType, methods);
			methodName.invoke(clName, (Object) model);
		} catch (Exception e) {
			LOGGER.info("exception occured in finding methods, method name is incorrect" + e);
		}

	}

	/**
	 * Find Element By CSS
	 * 
	 * @param objectLocators
	 */
	private void findElementByCssSelector(String objectLocators) {

		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(objectLocators)));

		List<WebElement> list1 = WebDriverClass.getInstance().findElements(By.cssSelector(objectLocators));
		listOfElements = list1;

	}

	/**
	 * Find Element By ID
	 * 
	 * @param objectLocators
	 */
	public void findElementById(String objectLocators) {

		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);
		List<WebElement> list1 = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(objectLocators)));

		listOfElements = list1;

	}

	/**
	 * Find Element By Xpath
	 * 
	 * @param objectLocators
	 */
	public void findElementByXpath(String objectLocators) {

		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);

		wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(objectLocators)));

		List<WebElement> list1 = wait

				.until(ExpectedConditions
						.visibilityOfAllElements(WebDriverClass.getDriver().findElements(By.xpath(objectLocators))));

		listOfElements = list1;

	}

	/**
	 * Find Element By Name
	 * 
	 * @param objectLocators
	 */
	public void findElementByName(String objectLocators) {
		LOGGER.info("findElementByName==", objectLocators);

		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);
		wait.until(ExpectedConditions
				.visibilityOfAllElements(WebDriverClass.getDriver().findElements(By.name(objectLocators))));
		LOGGER.info("element found==", objectLocators);

		List<WebElement> list1 = WebDriverClass.getInstance().findElements(By.name(objectLocators));
		LOGGER.info("list size=", list1.size());
		listOfElements = list1;

	}

	/**
	 * Find Element By Link text
	 * 
	 * @param objectLocators
	 */
	public void findElementByLinkText(String objectLocators) {
		LOGGER.info("findElementByLinkText==", objectLocators);

		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);
		wait.until(ExpectedConditions
				.visibilityOfAllElements(WebDriverClass.getDriver().findElements(By.linkText(objectLocators))));
		LOGGER.info("element found==", objectLocators);

		List<WebElement> list1 = WebDriverClass.getInstance().findElements(By.linkText(objectLocators));
		LOGGER.info("list size", list1.size());
		listOfElements = list1;

	}

	/**
	 * Find corresponding method name in existing methods
	 * 
	 * @param methodName
	 * @param methods
	 * @return
	 */
	public static Method findMethods(String methodName, Method[] methods) {

		for (int i = 0; i < methods.length; i++) {
			if (methodName.equalsIgnoreCase(methods[i].getName())) {
				return methods[i];
			}
		}
		return null;
	}

	/**
	 * Click on the element in the page
	 * 
	 * @param model
	 */
	public void click(MethodParameters model) {

		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);
		wait.until(ExpectedConditions.elementToBeClickable(model.getElements().get(0))).click();

		LOGGER.info("click method started" + model.getObjectLocators());
		LOGGER.info("click method completed");
	}

	/**
	 * Click on Submit button
	 * 
	 * @param model
	 */
	public void submit(MethodParameters model) {
		LOGGER.info("submit method started" + model.getObjectLocators());
		model.getElements().get(0).submit();
		LOGGER.info("submit method end");
	}

	/**
	 * Enter data into text field/text area
	 * 
	 * @param model
	 */
	public void enterText(MethodParameters model) {
		LOGGER.info(" inside enterText(), data to entered into the text==" + model.getData());
		model.getElements().get(0).sendKeys(model.getData());
		LOGGER.info("enterText() exit");
	}

	/**
	 * Read the value present in the text field
	 * 
	 * @param model
	 */
	public void readTextFieldValue(MethodParameters model) {
		LOGGER.info("inside readTextFieldValue()" + model.getObjectLocators());
		model.getElements().get(0).getAttribute("value");
		LOGGER.info("end of readTextFieldValue");
	}

	/**
	 * Alert accept meaning click on OK button
	 */
	public void alertAccept() {

		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);
		wait.until(ExpectedConditions.alertIsPresent());

		LOGGER.info("inside alertAccept()");

		wait1(2000);

		Alert alert = WebDriverClass.getInstance().switchTo().alert();
		wait1(2000);

		alert.accept();
		LOGGER.info("completed alertAccept()");
	}

	/**
	 * Alert dismiss meaning click on Cancel button
	 * 
	 * @param model
	 */
	public void alertDismiss(MethodParameters model) {
		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);
		wait.until(ExpectedConditions.alertIsPresent());

		LOGGER.info("inside alertDismiss()");
		wait1(2000);
		model.getElements().get(0).click();
		Alert alert = WebDriverClass.getInstance().switchTo().alert();
		wait1(2000);
		alert.dismiss();
	}

	/**
	 * Get the title of the page and verify the title
	 * 
	 * @param model
	 */
	public void verifyTitleOfPage(MethodParameters model) {
		LOGGER.info("inside verifyTitleOfPage()" + "title==" + WebDriverClass.getInstance().getTitle()
				+ "data from excel=" + model.getData());

		wait1(2000);
		String actual = WebDriverClass.getInstance().getTitle();
		String expected = model.getData();
		Assert.assertEquals(actual, expected);
		LOGGER.info("assert verification successful verifyTitleOfPage()");

	}

	/**
	 * Make the driver to wait for specified amount of time
	 * 
	 * @param time
	 */
	public void wait1(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			LOGGER.error("InvalidFormatException" + e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Select from the drop down list,if the drop down element tag is "SELECT" then
	 * use this method
	 * 
	 * @param model
	 */
	public void selectDropDownByVisibleText(MethodParameters model) {
		wait1(2000);
		LOGGER.info("inside selectDropDownByVisibleText");

		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 30);
		wait.pollingEvery(2, TimeUnit.SECONDS)
				.until(ExpectedConditions.elementToBeClickable(model.getElements().get(0)));
		Select sel = new Select(model.getElements().get(0));
		sel.selectByVisibleText(model.getData());
		wait1(2000);
	}

	/**
	 * Select the value from a dropdown list by its index
	 * 
	 * @param model
	 */
	public void selectDropDownByIndex(MethodParameters model) {
		LOGGER.info("inside selectDropDownByIndex");
		Select sel = new Select(model.getElements().get(0));
		sel.selectByIndex(Integer.parseInt(model.getData()));
	}

	/**
	 * Select the value from a dropdown list by its value
	 * 
	 * @param model
	 */
	public void selectDropDownByValue(MethodParameters model) {
		LOGGER.info("inside selectDropDownByValue");
		Select sel = new Select(model.getElements().get(0));
		sel.selectByValue(model.getData());
	}

	/**
	 * Switch To frame( html inside another html)
	 * 
	 * @param model
	 */
	public void switchToFrame(MethodParameters model) {
		LOGGER.info("inside switchToFrame");
		WebDriverClass.getInstance().switchTo().frame(model.getElements().get(0));

	}

	/**
	 * Switch back to previous frame or html
	 */
	public void switchOutOfFrame() {
		LOGGER.info("inside switchOutOfFrame");
		WebDriverClass.getInstance().switchTo().defaultContent();

	}

	/**
	 * Select the multiple value from a dropdown list
	 * 
	 * @param model
	 */
	public void selectFromListDropDown(MethodParameters model) {
		LOGGER.info("inside selectFromListDropDown");
		wait1(2000);
		for (WebElement element1 : model.getElements()) {

			if (element1.getText().equals(model.getData())) {
				element1.click();
				break;
			}
		}

		wait1(2000);
	}

	/**
	 * Navigate to next page
	 */
	public void moveToNextPage() {
		WebDriverClass.getInstance().navigate().forward();
	}

	/**
	 * Navigate to previous page
	 */
	public void moveToPreviousPage() {
		WebDriverClass.getInstance().navigate().back();
	}

	/**
	 * Maximize the window
	 */
	public void maximizeWindow() {
		WebDriverClass.getInstance().manage().window().maximize();
	}

	/**
	 * Reads the text present in the web element
	 * 
	 * @param model
	 */
	public void readText(MethodParameters model) {
		LOGGER.info(
				"getText() method called  and value of getText==*************" + model.getElements().get(0).getText());
		model.getElements().get(0).getText();
		LOGGER.info("readText completed");
	}

	/**
	 * Quit the application
	 */
	public void quit() {
		WebDriverClass.getInstance().quit();
	}

	/**
	 * Closes the driver
	 */
	public void close() {
		WebDriverClass.getInstance().close();
	}

	/**
	 * Checks that the element is displayed in the current web page
	 * 
	 * @param model
	 */
	public void isDisplayed(MethodParameters model) {
		model.getElements().get(0).isDisplayed();
	}

	/**
	 * Checks that the element is enabled in the current web page
	 * 
	 * @param model
	 */
	public void isEnabled(MethodParameters model) {
		model.getElements().get(0).isEnabled();
	}

	/**
	 * Selects a radio button
	 * 
	 * @param model
	 */
	public void selectRadioButton(MethodParameters model) {
		model.getElements().get(0).click();
	}

	/**
	 * Refresh the current web page
	 */
	public void refreshPage() {
		WebDriverClass.getInstance().navigate().refresh();
	}

	/**
	 * Switch back to the parent window
	 */
	public void switchToParentWindow() {
		String parentWindow = WebDriverClass.getInstance().getWindowHandle();
		WebDriverClass.getInstance().switchTo().window(parentWindow);
	}

	/**
	 * Switch to the child window
	 * 
	 * @param model
	 */
	public void switchToChildWindow(MethodParameters model) {

		String parent = WebDriverClass.getInstance().getWindowHandle();
		Set<String> windows = WebDriverClass.getInstance().getWindowHandles();

		if (windows.size() > 1) {
			for (String child : windows) {
				if (!child.equals(parent)
						&& WebDriverClass.getInstance().switchTo().window(child).getTitle().equals(model.getData())) {
					WebDriverClass.getInstance().switchTo().window(child);

				}
			}
		}

	}

	/**
	 * Scrolls down the page till the element is visible
	 * 
	 * @param model
	 */
	public void scrollElementIntoView(MethodParameters model) {
		wait1(1000);

		LOGGER.info("scrollElementIntoView started");
		((JavascriptExecutor) WebDriverClass.getDriver()).executeScript("arguments[0].scrollIntoView(true);",
				model.getElements().get(0));
		wait1(1000);

	}

	/**
	 * Scrolls down the page till the element is visible and clicks on the element
	 * 
	 * @param model
	 */
	public void scrollElementIntoViewClick(MethodParameters model) {
		Actions action = new Actions(WebDriverClass.getDriver());
		action.moveToElement(model.getElements().get(0)).click().perform();
	}

	/**
	 * Reads the url of current web page
	 */
	public void readUrlOfPage() {
		WebDriverClass.getInstance().getCurrentUrl();
	}

	/**
	 * Navigates to the specified url
	 * 
	 * @param model
	 */
	public void navigateToURL(MethodParameters model) {
		WebDriverClass.getInstance().navigate().to(model.getData());
	}

	public static WebElement waitForElement(By by) {
		int count = 0;
		WebDriverWait wait = null;
		while (!(wait.until(ExpectedConditions.presenceOfElementLocated(by)).isDisplayed())) {
			wait = new WebDriverWait(WebDriverClass.getInstance(), 60);
			wait.pollingEvery(5, TimeUnit.MILLISECONDS);
			wait.until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed();
			wait.until(ExpectedConditions.presenceOfElementLocated(by)).isDisplayed();
			count++;
			if (count == 100) {
				break;
			}
			return wait.until(ExpectedConditions.presenceOfElementLocated(by));
		}
		return wait.until(ExpectedConditions.presenceOfElementLocated(by));
	}

	/**
	 * Provide Login name for window authentication
	 * 
	 * @param model
	 */
	public static void windowAuthenticationLoginName(MethodParameters model) {

		Alert alert = WebDriverClass.getDriver().switchTo().alert();
		alert.sendKeys(model.getData());
	}

	/**
	 * Lets say there is header menu bar, on hovering the mouse, drop down should be
	 * displayed
	 * 
	 * @param model
	 */
	public void dropDownByMouseHover(MethodParameters model) {
		Actions action = new Actions(WebDriverClass.getInstance());

		action.moveToElement(model.getElements().get(0)).perform();
		WebElement subElement = WebDriverClass.getInstance().findElement(By.xpath(model.getData()));
		action.moveToElement(subElement);
		action.click().build().perform();

	}

	/**
	 * verifies the data present in the text field
	 * 
	 * @param model
	 */
	public void verifyTextFieldData(MethodParameters model) {
		Assert.assertEquals(model.getElements().get(0).getAttribute("value"), model.getData());
	}

	/**
	 * Read title of the page and verify it
	 * 
	 */
	public void readTitleOfPage() {
		if (titleOfPage != null) {
			titleOfPage = null;
		}
		titleOfPage = WebDriverClass.getInstance().getTitle();
	}

	/**
	 * File upload in IE browser.
	 * 
	 * @param model
	 */
	public void fileUploadinIE(MethodParameters model) {
		model.getElements().get(0).click();
		StringSelection ss = new StringSelection(model.getData());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		Robot r;
		try {
			r = new Robot();

			r.keyPress(KeyEvent.VK_ENTER);

			r.keyRelease(KeyEvent.VK_ENTER);

			r.keyPress(KeyEvent.VK_CONTROL);
			r.keyPress(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_V);
			r.keyRelease(KeyEvent.VK_CONTROL);

			r.keyPress(KeyEvent.VK_ENTER);
			r.keyRelease(KeyEvent.VK_ENTER);

		} catch (AWTException e) {
			LOGGER.error(e.getMessage());
		}

	}

	/**
	 * Verify the alert text
	 * 
	 * @param model
	 */
	public void verifyalertText(MethodParameters model) {
		model.getElements().get(0).click();
		wait1(1000);
		Alert alert = WebDriverClass.getInstance().switchTo().alert();
		wait1(1000);
		if (alertText != null) {
			alertText = null;
		}
		alertText = alert.getText();
		Assert.assertEquals(alertText, model.getData());
		alert.accept();
	}

	/**
	 * SSL errors that appear on IE browser can be resolved
	 */
	public void certificateErrorsIE() {

		WebDriverClass.getDriver().navigate().to("javascript:document.getElementById('overridelink').click()");
	}

	/**
	 * 
	 * @param model
	 */
	public void dragAndDrop(MethodParameters model) {
		String[] actType = model.getActionType().split("$");

		WebElement sourceElement = WebDriverClass.getDriver().findElement(By.xpath(actType[0]));
		WebElement destinationElement = WebDriverClass.getDriver().findElement(By.xpath(actType[1]));

		Actions action = new Actions(WebDriverClass.getDriver());
		action.dragAndDrop(sourceElement, destinationElement).build().perform();

	}

	/**
	 * Clear the content of the text field
	 * 
	 * @param model
	 */
	public void clear(MethodParameters model) {
		wait1(2000);
		WebDriverWait wait = new WebDriverWait(WebDriverClass.getDriver(), 60);
		wait.until(ExpectedConditions.visibilityOf(model.getElements().get(0)));
		model.getElements().get(0).clear();
	}

	/**
	 * Makes the driver to sleep for specified time
	 * 
	 * @param model
	 */
	public void sleep(MethodParameters model) {
		try {
			Integer i = Integer.parseInt(model.getData());
			Thread.sleep(i);

		} catch (InterruptedException e) {
			LOGGER.info("InterruptedException" + e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Verifies the Text present in the element
	 * 
	 * @param model
	 */
	public void verifyText(MethodParameters model) {
		LOGGER.info("model.getElements().get(0).getText()**********" + model.getElements().get(0).getText());
		LOGGER.info("model.getData()**********" + model.getData());

		Assert.assertEquals(model.getData(), model.getElements().get(0).getText());
		LOGGER.info("verify text completed");
	}

	/**
	 * Verifies that the particular file is exists or not
	 * 
	 * @param model
	 */
	public void verifyFileExists(MethodParameters model) {
		File file = new File(model.getData());
		if (file.exists() && !(file.isDirectory() && file.isFile())) {
			Assert.assertEquals(file.getAbsoluteFile(), model.getData());
		}
	}

	/**
	 * Downloads a file from IE browser
	 * 
	 * @param model
	 */
	public void downloadFileIE(MethodParameters model) {
		FileDownloader downloadTestFile = new FileDownloader(WebDriverClass.getDriver());
		String downloadedFileAbsoluteLocation;
		try {
			downloadedFileAbsoluteLocation = downloadTestFile.downloadFile(model.getElements().get(0));
			Assert.assertTrue(new File(downloadedFileAbsoluteLocation).exists());
		} catch (Exception e) {
			LOGGER.error("exception occured: ", e.getMessage());
		}

	}

	/**
	 * 
	 * @param model
	 */
	public void webTableClick(MethodParameters model) {

		String[] actType = model.getActionType().split("\\$");
		WebElement mytable = WebDriverClass.getDriver().findElement(By.xpath(actType[0]));
		List<WebElement> rowstable = mytable.findElements(By.tagName("tr"));
		int rowsCount = rowstable.size();
		for (int row = 0; row < rowsCount; row++) {
			List<WebElement> columnsRow = rowstable.get(row).findElements(By.tagName("td"));
			int columnscount = columnsRow.size();
			for (int column = 0; column < columnscount; column++) {
				String celtext = columnsRow.get(column).getText();
				celtext.getClass();
			}
		}
	}

	/**
	 * Select date from date picker
	 * 
	 * @param model
	 */
	public void selectDateFromCalendar(MethodParameters model) {

		String[] actionType = model.getActionType().split("$$");
		String[] data = model.getData().split("/");
		List<String> monthList = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December");

		int expMonth;
		int expYear;
		String expDate = null;
		// Calendar Month and Year
		String calMonth = null;
		String calYear = null;
		boolean dateNotFound;

		WebDriverClass.getDriver().findElement(By.xpath(actionType[0])).click();

		dateNotFound = true;

		// Set your expected date, month and year.
		expDate = data[0];
		expMonth = Integer.parseInt(data[1]);
		expYear = Integer.parseInt(data[2]);

		// This loop will be executed continuously till dateNotFound Is true.
		while (dateNotFound) {
			// Retrieve current selected month name from date picker popup.
			calMonth = WebDriverClass.getDriver().findElement(By.className("ui-datepicker-month")).getText();

			// Retrieve current selected year name from date picker popup.
			calYear = WebDriverClass.getDriver().findElement(By.className("ui-datepicker-year")).getText();

			/*
			 * If current selected month and year are same as expected month and year then
			 * go Inside this condition.
			 */
			if (monthList.indexOf(calMonth) + 1 == expMonth && (expYear == Integer.parseInt(calYear))) {
				/*
				 * Call selectDate function with date to select and set dateNotFound flag to
				 * false.
				 */
				selectDate(expDate);
				dateNotFound = false;
			}
			// If current selected month and year are less than expected month
			// and year then go Inside this condition.
			else if (monthList.indexOf(calMonth) + 1 < expMonth && (expYear == Integer.parseInt(calYear))
					|| expYear > Integer.parseInt(calYear)) {

				// Click on next button of date picker.
				WebDriverClass.getDriver().findElement(By.xpath(actionType[1])).click();
			}
			// If current selected month and year are greater than expected
			// month and year then go Inside this condition.
			else if (monthList.indexOf(calMonth) + 1 > expMonth && (expYear == Integer.parseInt(calYear))
					|| expYear < Integer.parseInt(calYear)) {

				// Click on previous button of date picker.
				WebDriverClass.getDriver().findElement(By.xpath(actionType[2])).click();
			}
		}
		wait1(3000);
	}

	/**
	 * Selects the Date
	 * 
	 * @param date
	 */
	public void selectDate(String date) {
		WebElement datePicker = WebDriverClass.getDriver().findElement(By.id("ui-datepicker-div"));
		List<WebElement> noOfColumns = datePicker.findElements(By.tagName("td"));

		// Loop will rotate till expected date not found.
		for (WebElement cell : noOfColumns) {
			// Select the date from date picker when condition match.
			if (cell.getText().equals(date)) {
				cell.findElement(By.linkText(date)).click();
				break;
			}
		}

	}

	/**
	 * Double clicks on the particular element
	 * 
	 * @param model
	 */
	public void doubleClick(MethodParameters model) {
		Actions action = new Actions(WebDriverClass.getDriver());
		action.doubleClick((WebElement) model.getElements()).perform();

	}

	/**
	 * Mouse hovering on the element is performed
	 * 
	 * @param model
	 */
	public void singleMouseHover(MethodParameters model) {
		Actions action = new Actions(WebDriverClass.getDriver());
		action.moveToElement((WebElement) model.getElements()).perform();

	}

	/**
	 * Right clicks on the element
	 * 
	 * @param model
	 */
	public void rightClick(MethodParameters model) {
		Actions action = new Actions(WebDriverClass.getDriver());
		action.contextClick((WebElement) model.getElements()).perform();

	}

	/**
	 * Un-check the all check boxes
	 * 
	 * @param model
	 */
	public void deselectAllCheckbox(MethodParameters model) {
		List<WebElement> list = model.getElements();

		for (WebElement element : list) {
			if (element.isSelected()) {
				element.click();
			}
		}
	}

	/**
	 * Selects all the check boxes
	 * 
	 * @param model
	 */
	public void selectAllCheckbox(MethodParameters model) {
		List<WebElement> list = model.getElements();

		for (WebElement element : list) {
			if (!element.isSelected()) {
				element.click();
			}
		}
	}

	/**
	 * Verifies that the particular check box is selected
	 * 
	 * @param model
	 */
	public void verifyCheckBoxSelected(MethodParameters model) {
		Assert.assertTrue(model.getElements().get(0).isSelected());
	}

	/**
	 * Verifies whether all the check box is selected
	 * 
	 * @param model
	 */
	public void verifyAllCheckBoxSelected(MethodParameters model) {
		for (WebElement element : model.getElements()) {
			Assert.assertTrue(element.isSelected(), "check box is selected");
		}
	}

	/**
	 * Verifies that all the check boxes is not selected
	 * 
	 * @param model
	 */
	public void verifyAllCheckBoxNotSelected(MethodParameters model) {
		for (WebElement element : model.getElements()) {
			Assert.assertFalse(element.isSelected(), "check box not selected");
		}
	}

	/**
	 * File download from Auto It
	 * 
	 * @param model
	 */
	public void filedownloadAUTOIT(MethodParameters model) {
		try {
			Runtime.getRuntime().exec(model.getData());
		} catch (IOException e) {
			LOGGER.error("Exception occured: ", e.getMessage());
		}
	}

}