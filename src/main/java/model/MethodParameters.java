package model;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

/**
 * The MethodParameters class is used as a bean class to get the details for
 * actions performed on the element
 * 
 * @author KaruppanadarJ
 * @version 1.0
 *
 */
public class MethodParameters {

	private List<WebElement> elements = new ArrayList<>();

	private String methodType;
	private String actionType;
	private String data;
	private String objectLocators;
	
	/**
	 * @return the elements
	 */
	public List<WebElement> getElements() {
		return elements;
	}
	/**
	 * @param elements the elements to set
	 */
	public void setElements(List<WebElement> elements) {
		this.elements = elements;
	}
	/**
	 * @return the methodType
	 */
	public String getMethodType() {
		return methodType;
	}
	/**
	 * @param methodType the methodType to set
	 */
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	/**
	 * @return the actionType
	 */
	public String getActionType() {
		return actionType;
	}
	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	/**
	 * @return the objectLocators
	 */
	public String getObjectLocators() {
		return objectLocators;
	}
	/**
	 * @param objectLocators the objectLocators to set
	 */
	public void setObjectLocators(String objectLocators) {
		this.objectLocators = objectLocators;
	}

}
