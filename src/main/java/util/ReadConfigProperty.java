package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ReadConfigProperty class is used to read the configuration details from
 * config file
 * 
 * @author KaruppanadarJ
 *
 */
public class ReadConfigProperty {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReadConfigProperty.class);
	private static Map<String, String> map = new HashMap<>();
	private Properties properties = new Properties();
	private String configPath = System.getProperty(Constant.TEST_USER_DIR);

	public String getConfigValues(String elementValue) {

		if (properties.isEmpty()) {
			File file = new File(configPath + "\\config.properties");
			if (!file.exists()) {
				file = new File("config.properties");// Try to load the properties from class path
			}
			try (FileInputStream fileInput = new FileInputStream(file)) {
				properties = new Properties();
				properties.load(fileInput);

			} catch (IOException e) {
				LOGGER.error("IOException" + e.getMessage());
			}
		}
		return properties.getProperty(elementValue);
	}

	/**
	 * Read the config file and store in to map.
	 * 
	 * @return
	 */
	public Map<String, String> readConfigFile() {
		String line;
		if (properties.isEmpty()) {
			File file = new File(configPath + "\\config.properties");
			if (!file.exists()) {
				file = new File("config.properties");// Try to load the properties from class path
			}
			try (FileInputStream fileInput = new FileInputStream(file);
					BufferedReader buffReader = new BufferedReader(new InputStreamReader(fileInput))) {
				while ((line = buffReader.readLine()) != null) {

					String[] temp3 = line.split("=");
					if (temp3.length > 1) {
						String key = temp3[0];
						String value = temp3[1];

						if (Constant.TEST_SUITE_SHEET_NAME.contains(key)) {
							map.put(key, value);
						}
					}
				}
			} catch (IOException e) {
				LOGGER.getName();
				LOGGER.error(e.getLocalizedMessage());
				LOGGER.error(e.getMessage());
			}
		}
		return map;
	}

}
