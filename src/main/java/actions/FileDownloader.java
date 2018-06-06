package actions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The FileDownloader class is used to download the file from the browser
 * 
 * @author KaruppanadarJ
 *
 */
public class FileDownloader {

	private WebDriver driver;
	private String localDownloadPath = System.getProperty("java.io.tmpdir");

	private boolean mimicWebDriverCookieState = true;
	private int httpStatusOfLastDownloadAttempt = 0;

	public FileDownloader(WebDriver driverObject) {
		this.driver = driverObject;

	}

	/**
	 * Get the current location that files will be downloaded to.
	 * 
	 * @return The filepath that the file will be downloaded to.
	 */
	public String localDownloadPath() {
		return this.localDownloadPath;
	}

	/**
	 * Set the path that files will be downloaded to.
	 * 
	 * @param filePath
	 *            The filepath that the file will be downloaded to.
	 */
	public void localDownloadPath(String filePath) {
		this.localDownloadPath = filePath;
	}

	/**
	 * Download the file specified in the href attribute of a WebElement
	 * 
	 * @param webElement
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public String downloadFile(WebElement element) throws IOException, URISyntaxException {
		return downloader(element, "href");
	}

	/**
	 * Download the image specified in the src attribute of a WebElement
	 * 
	 * @param webElement
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public String downloadImage(WebElement element) throws IOException, URISyntaxException {
		return downloader(element, "src");
	}

	/**
	 * Gets the HTTP status code of the last download file attempt
	 * 
	 * @return
	 */
	public int getHTTPStatusOfLastDownloadAttempt() {
		return this.httpStatusOfLastDownloadAttempt;
	}

	/**
	 * Mimic the cookie state of WebDriver (Defaults to true) This will enable you
	 * to access files that are only available when logged in. If set to false the
	 * connection will be made as an anonymouse user
	 * 
	 * @param value
	 */
	public void mimicWebDriverCookieState(boolean value) {
		this.mimicWebDriverCookieState = value;
	}

	/**
	 * Load in all the cookies WebDriver currently knows about so that we can mimic
	 * the browser cookie state
	 * 
	 * @param seleniumCookieSet
	 * @return
	 */
	private BasicCookieStore mimicCookieState(Set<Cookie> seleniumCookieSet) {
		BasicCookieStore mimicWebDriverCookieStore = new BasicCookieStore();
		for (Cookie seleniumCookie : seleniumCookieSet) {
			BasicClientCookie duplicateCookie = new BasicClientCookie(seleniumCookie.getName(),
					seleniumCookie.getValue());
			duplicateCookie.setDomain(seleniumCookie.getDomain());
			duplicateCookie.setSecure(seleniumCookie.isSecure());
			duplicateCookie.setExpiryDate(seleniumCookie.getExpiry());
			duplicateCookie.setPath(seleniumCookie.getPath());
			mimicWebDriverCookieStore.addCookie(duplicateCookie);
		}

		return mimicWebDriverCookieStore;
	}

	/**
	 * Perform the file/image download.
	 * 
	 * @param webElement
	 * @param attribute
	 * @return
	 * @throws IOException
	 * @throws NullPointerException
	 */
	private String downloader(WebElement element, String attribute)
			throws IOException, URISyntaxException {

		String fileToDownloadLocation = element.getAttribute(attribute);

		if (fileToDownloadLocation.trim().equals(""))
			throw new NullPointerException("The element you have specified does not link to anything!");

		URL fileToDownload = new URL(fileToDownloadLocation);
		File downloadedFile = new File(this.localDownloadPath + fileToDownload.getFile().replaceFirst("/|\\\\", ""));
		if (downloadedFile.canWrite()) {
			downloadedFile.setWritable(true);
		}

		HttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
		BasicHttpContext localContext = new BasicHttpContext();

		if (this.mimicWebDriverCookieState) {
			localContext.setAttribute(HttpClientContext.COOKIE_STORE, mimicCookieState(this.driver.manage().getCookies()));
		}

		HttpGet httpget = new HttpGet(fileToDownload.toURI());
		
		HttpResponse response = client.execute(httpget, localContext);
		this.httpStatusOfLastDownloadAttempt = response.getStatusLine().getStatusCode();

		FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
		response.getEntity().getContent().close();

		return downloadedFile.getAbsolutePath();
	}

}