package util;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Report class is used to generate the report
 * 
 * @author KaruppanadarJ
 *
 */
public class Report {
	private static final Logger LOGGER = LoggerFactory.getLogger(Report.class);
	private static final String TEST_OUT_PUT = "\\test-output";

	private Report() {
	}

	public static void report() {

		String configPath = System.getProperty(Constant.TEST_USER_DIR);

		try (FileOutputStream os = new FileOutputStream(configPath + TEST_OUT_PUT + "\\report.html")) {

			System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(new StreamSource("xml/testng-results.xsl"));

			trans.setParameter("testNgXslt.outputDir", configPath + TEST_OUT_PUT);

			trans.setParameter("testNgXslt.cssFile", "xml/custom.css");
			trans.setParameter("testNgXslt.showRuntimeTotals", "true");
			trans.setParameter("testNgXslt.sortTestCaseLinks", "true");
			trans.setParameter("testNgXslt.testDetailsFilter", "FAIL,PASS,SKIP,CONF,BY_CLASS");

			trans.transform(new StreamSource(configPath + TEST_OUT_PUT + "\\testng-results.xml"), new StreamResult(os));

		} catch (TransformerException e) {
			LOGGER.info("TransformerException" + e);
		} catch (IOException e) {
			LOGGER.info("FileNotFoundException" + e);
		}
	}
}
