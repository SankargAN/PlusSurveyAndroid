package Utilities;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;


public class Baseclass {

	public static String testSuiteName;
	public static String testCaseName;
	public static ExtentReports extentReports;
	public static ExtentSparkReporter sparkRepoter;
	public static ExtentTest extentTest;
	public static String screenshotPath = null;
	public static AndroidDriver driver;

	@BeforeSuite
	public void extendsReportsInstantiation(ITestContext context) {
		testSuiteName = context.getSuite().getName();
		testCaseName = context.getCurrentXmlTest().getName();

		extentReports = new ExtentReports();
		sparkRepoter = new ExtentSparkReporter(System.getProperty("user.dir")+"/ExtentReports/"+testSuiteName+"/"+testCaseName+"/"+testCaseName+timeStamp()+".html");
		extentReports.attachReporter(sparkRepoter);
	}

	@BeforeTest
	//Cross Browsing 
	public void AppInstantiation(ITestContext context) throws Exception {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "Appium");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
		capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "Android");
		capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, "14");
		capabilities.setCapability(MobileCapabilityType.APP, "C:/Users/Admin/OneDrive/Desktop/Roboxa(Dosing diary for subject)/app-release 1 (1).apk");
		String appiumServerUrl1 = "http://127.0.0.1:4723/wd/hub";
		@SuppressWarnings("deprecation")
		URL appiumServerURL = new URL(appiumServerUrl1);
		driver = new AndroidDriver(appiumServerURL, capabilities);
		driver.manage().timeouts().implicitlyWait(Duration.ofMillis(60000));

		//Object for Capabilities

		String deviceName = capabilities.getPlatformName().toString();
		String author = context.getCurrentXmlTest().getParameter("author");

		//System Environment
		extentReports.setSystemInfo("Application", "Subject Diary application");

		//Create test in Reports
		extentTest=extentReports.createTest(context.getName(), "Subject Diary App version1");
		extentTest.assignAuthor(author);
		extentTest.assignDevice(deviceName);


	}

	@AfterMethod
	public void checkStatus(ITestResult result, java.lang.reflect.Method m) throws Exception {
		//status Checking
		testCaseName = result.getTestContext().getName();
		if(result.getStatus()==ITestResult.SUCCESS) {
			extentTest.pass(m.getName() + " is pass");
			screenshotPath = captureScrrenshotPath(result.getTestContext().getName()+"-"+result.getMethod().getMethodName());
			extentTest.addScreenCaptureFromPath(screenshotPath);
		}else if (result.getStatus()==ITestResult.FAILURE) {
			screenshotPath = captureScrrenshotPath(result.getTestContext().getName()+"-"+result.getMethod().getMethodName());
			extentTest.addScreenCaptureFromPath(screenshotPath);
			extentTest.fail(result.getThrowable());
		}else if (result.getStatus() == ITestResult.SKIP) {
			extentTest.skip(m.getName()+ " is Skiped");
		}
		extentTest.assignCategory(m.getAnnotation(org.testng.annotations.Test.class).groups());
		File jsonfile = new File(System.getProperty("user.dir")+"/Resources/extent-reports-config.json");
		sparkRepoter.loadJSONConfig(jsonfile);
	}
	@AfterTest
	//close the browser
	public void closeApp() {
		//capabilities.close();
	}
	@AfterSuite
	public void generateExtentReports() {
		extentReports.flush();
	}

	//TimeStamp
	public static String timeStamp() {
		String timeStamp = null;
		LocalDateTime timedate = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyymmddhhss");
		timeStamp = timedate.format(format);
		return timeStamp;
	}
	//ScreenShot
	public static void screenShot(String fileName) {
		TakesScreenshot sh = (TakesScreenshot) driver;
		File sourcePath = sh.getScreenshotAs(OutputType.FILE);
		File destPath = new File(System.getProperty("use.dir")+"/Screenshots/"+fileName+timeStamp());

		try {
			FileUtils.copyFile(sourcePath, destPath);
		} catch (Exception e) {
			e.getMessage();
		}
	}
	//Screenshot path
	public static String captureScrrenshotPath(String fileName) {
		TakesScreenshot sh = (TakesScreenshot) driver;
		File sourcPath = sh.getScreenshotAs(OutputType.FILE);
		File destFilePath = new File(System.getProperty("user.dir")+"/Screenshots/"+testSuiteName+"/"+testCaseName+"/"+fileName+timeStamp()+".jpg");
		try {
			FileUtils.copyFile(sourcPath, destFilePath);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return destFilePath.getAbsolutePath();
	}


}
