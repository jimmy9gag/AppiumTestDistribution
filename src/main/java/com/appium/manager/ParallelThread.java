package com.appium.manager;

import com.appium.cucumber.report.HtmlReporter;
import com.appium.executor.MyTestExecutor;
import com.appium.ios.IOSDeviceConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/*
 * This class picks the devices connected
 * and distributes across multiple thread.
 *
 * Thanks to @Thote_Gowda(thotegowda.gr@gmail.com)
 */

public class ParallelThread {
	protected int deviceCount;
	Map<String, String> devices = new HashMap<String, String>();
	AndroidDeviceConfiguration deviceConf = new AndroidDeviceConfiguration();
	IOSDeviceConfiguration iosDevice= new IOSDeviceConfiguration();
	AppiumParallelTest baseTest = new AppiumParallelTest();
	HtmlReporter htmlReporter = new HtmlReporter();
	MyTestExecutor myTestExecutor = new MyTestExecutor();
	public Properties prop = new Properties();
	public InputStream input = null;
	List<Class> testcases;

	@SuppressWarnings({ "rawtypes" })
	public void runner(String pack) throws Exception {
		File f = new File(System.getProperty("user.dir") + "/target/appiumlogs/");
		if (!f.exists()) {
			System.out.println("creating directory: " + "Logs");
			boolean result = false;
			try {
				f.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				System.out.println("DIR created");
			}
		}

		File adb_logs = new File(System.getProperty("user.dir") + "/target/adblogs/");
		if (!adb_logs.exists()) {
			System.out.println("creating directory: " + "ADBLogs");
			boolean result = false;
			try {
				adb_logs.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				System.out.println("DIR created");
			}
		}
		input = new FileInputStream("config.properties");
		prop.load(input);
		
		if (prop.getProperty("PLATFORM").equalsIgnoreCase("android")) {
			devices = deviceConf.getDevices();
			deviceCount = devices.size() / 3;
		} else if (prop.getProperty("PLATFORM").equalsIgnoreCase("ios")) {
			deviceCount=iosDevice.getIOSUDID().size();		
		}
		
		
		System.out.println("Total Number of devices detected::" + deviceCount);
		System.out.println("starting running tests in threads");

		testcases = new ArrayList<Class>();

		// final String pack = "com.paralle.tests"; // Or any other package
		PackageUtil.getClasses(pack).stream().forEach(s -> {
			if (s.toString().contains("Test")) {
				System.out.println("forEach: " + testcases.add((Class) s));
			}
		});

		//TODO: Add another check for OS on distribution and parallel
		if (prop.getProperty("RUNNER").equalsIgnoreCase("distribute")) {
			//myTestExecutor.distributeTests(deviceCount, testcases);
			myTestExecutor.runMethodParallelAppium(pack, deviceCount,"distribute");

		}//TODO: Add another check for OS on distribution and parallel
		else if (prop.getProperty("RUNNER").equalsIgnoreCase("parallel")) {
			myTestExecutor.runMethodParallelAppium(pack, deviceCount,"parallel");
		}

	}

}
