package actions;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import util.BrowserUtil;
import util.Constant;
import util.ReadConfigProperty;

/**
 * The FileDownloaderTest class is used to test the FileDownloader class
 * 
 * @author KaruppanadarJ
 *
 */
public class FileDownloadTest {

	private ReadConfigProperty config = new ReadConfigProperty();

	@Test
	public void downloadAFile() throws Exception {

		WebDriver driver;
		driver = BrowserUtil.openBrowser(config.getConfigValues(Constant.TEST_BROWSER_NAME));

		driver.get("http://iscls1apps/INFYDIR/");
		driver.findElement(By.id("_ctl0_ContentPlaceHolder1_txtSearch")).sendKeys("shilpashree_v");
		driver.findElement(By.id("_ctl0_ContentPlaceHolder1_lnkSearch")).click();

		WebElement downloadLink = null;
		int count = 0;
		while (downloadLink == null) {
			downloadLink = driver.findElement(By.id("_ctl0_ContentPlaceHolder1_lnkDownload"));
			count++;
			if (count == 50) {

				break;
			}
		}
		// WebElement downloadLink = driver.findElement(By
		// .id("_ctl0_ContentPlaceHolder1_lnkDownload"));
		FileDownloader downloadTestFile = new FileDownloader(driver);
		String downloadedFileAbsoluteLocation = downloadTestFile.downloadFile(downloadLink);
		System.out
				.println("downloadTestFile.downloadFile(downloadLink);" + downloadTestFile.downloadFile(downloadLink));

		Assert.assertTrue(new File(downloadedFileAbsoluteLocation).exists());
		// assertThat(downloadTestFile.getHTTPStatusOfLastDownloadAttempt(),
		// is(equalTo(200)));
	}

	@Test
	public void checkValidMD5Hash() throws Exception {
		CheckFileHash fileToCheck = new CheckFileHash();
		fileToCheck.fileToCheck(new File(System.getProperty("java.io.tmpdir")));

		fileToCheck.hashDetails("617bfc4b78b03a0f61c98188376d2a6d", HashType.MD5);
		Assert.assertTrue(fileToCheck.hasAValidHash());
	}
}
