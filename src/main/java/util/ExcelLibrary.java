package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Reporter;

/**
 * The ExcelLibrary class is used to read the data from the excel sheet
 * 
 * @author KaruppanadarJ
 * @version 1.0
 *
 */
public class ExcelLibrary {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelLibrary.class);
	private static Map<String, Workbook> workbooktable = new HashMap<>();
	private static List<String> list = new ArrayList<>();
	private static ReadConfigProperty config = new ReadConfigProperty();

	/**
	 * To get the excel sheet workbook
	 */
	public static Workbook getWorkbook(String path) {
		Workbook workbook = null;
		if (workbooktable.containsKey(path)) {
			workbook = workbooktable.get(path);
		} else {
			try {
				File file = new File(path);

				workbook = WorkbookFactory.create(file);

				workbooktable.put(path, workbook);

			} catch (FileNotFoundException e) {
				LOGGER.info("FileNotFoundException" + e.getMessage());
			} catch (InvalidFormatException e) {
				LOGGER.info("InvalidFormatException" + e.getMessage());
			} catch (IOException e) {
				LOGGER.info("IOException" + e.getMessage());
			}
		}
		return workbook;
	}

	/**
	 * To get the number of sheets in excel suite
	 * 
	 * @param testPath
	 * @return
	 */
	public static List<String> getNumberOfSheetsinSuite(String testPath) {
		List<String> listOfSheets = new ArrayList<>();

		Workbook workbook = getWorkbook(testPath);
		if (null != workbook) {
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				listOfSheets.add(workbook.getSheetName(i));
			}
		}
		return listOfSheets;
	}

	/**
	 * To get the number of sheets in test data sheet
	 * 
	 * @param testPath
	 * @return
	 */
	public static List<String> getNumberOfSheetsinTestDataSheet(String testPath) {
		List<String> listOfSheets = new ArrayList<>();

		Workbook workbook = getWorkbook(testPath);
		if (null != workbook) {
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (!(workbook.getSheetName(i)).equalsIgnoreCase(config.getConfigValues(Constant.TEST_CASE_SHEET_NAME))) {
					listOfSheets.add(workbook.getSheetName(i));

				}
			}
		}
		return listOfSheets;

	}

	/**
	 * Get the total rows present in excel sheet
	 * 
	 * @param testSheetName
	 * @param pathOfFile
	 * @return
	 */
	public static int getRows(String testSheetName, String pathOfFile) {
		Workbook workbook = getWorkbook(pathOfFile);
		int lastRowNum = 0;
		if (null != workbook) {
			Reporter.log("getting total number of rows");

			Sheet sheet = workbook.getSheet(testSheetName);

			lastRowNum = sheet.getLastRowNum();
		}
		return lastRowNum;
	}

	/**
	 * Get the total columns inside excel sheet
	 * 
	 * @param testSheetName
	 * @param pathOfFile
	 * @return
	 */
	public static int getColumns(String testSheetName, String pathOfFile) {
		Workbook workbook = getWorkbook(pathOfFile);
		int lastCellNum = 0;
		if (null != workbook) {
			Reporter.log("getting total number of columns");
			Sheet sheet = workbook.getSheet(testSheetName);
			lastCellNum = sheet.getRow(0).getLastCellNum();
		}
		return lastCellNum;

	}

	/**
	 * Get the column names inside excel sheet
	 * 
	 * @param testSheetName
	 * @param pathOfFile
	 * @param count
	 * @return
	 */
	public static List<String> getColumnNames(String testSheetName, String pathOfFile, int count) {
		Workbook workbook = getWorkbook(pathOfFile);
		if (null != workbook) {
			Sheet sheet = workbook.getSheet(testSheetName);

			for (int i = 0; i <= count; i++) {
				if (sheet.getRow(0).getCell(i) != null) {
					list.add(sheet.getRow(0).getCell(i).getStringCellValue());
				}
			}
		}
		return list;

	}

	/**
	 * Get the total number of rows for each column inside excel sheet
	 * 
	 * @param testSheetName
	 * @param pathOfFile
	 */
	public static void getNumberOfRowsPerColumn(String testSheetName, String pathOfFile) {
		Workbook workbook = getWorkbook(pathOfFile);
		if (null != workbook) {
			Sheet sheet = workbook.getSheet(testSheetName);
			int totColumns = sheet.getRow(0).getLastCellNum();
			for (int i = 0; i <= totColumns; i++) {
				if (sheet.getRow(0).getCell(i) != null) {
					list.add(sheet.getRow(0).getCell(i).getStringCellValue());
				}
			}
		}
	}

	/**
	 * Read the content of the cell
	 * 
	 * @param rowNum
	 * @param colNum
	 * @param testSheetName
	 * @param pathOfFile
	 * @return
	 */
	public static String readCell(int rowNum, int colNum, String testSheetName, String pathOfFile) {
		Workbook workbook;
		String cellValue = null;

		workbook = getWorkbook(pathOfFile);
		if (null != workbook) {
			Sheet sheet = workbook.getSheet(testSheetName);
			Row row = sheet.getRow(rowNum);
			if (row != null) {
				Cell cell = row.getCell(colNum);
				if (cell != null) {
					DataFormatter dataFormatter = new DataFormatter();
					String data = dataFormatter.formatCellValue(cell);
					cellValue = data;
				}
			}
		}
		return cellValue;
	}

	/**
	 * To clear the worktable and list
	 */
	public void clean() {
		workbooktable.clear();
		list.clear();
	}

}