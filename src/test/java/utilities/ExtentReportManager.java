package utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import testBase.BaseClass;

public class ExtentReportManager implements ITestListener{
	
	public ExtentSparkReporter sparkreporter;  //UI of the Reports
	public ExtentReports extent;	//populate common info on the report
	public ExtentTest test;	//creating test case entries in the report and update status of the test methods
	
	String repName;
	
	public void onStart(ITestContext context) {
		
		/*SimpleDateFormat df =new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		Date dt=new Date();
		String currentdatetimestamp=df.format(dt);
		*/
	    
		String timeStamp=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		repName = "Test-Report-" + timeStamp + ".html";
		sparkreporter=new ExtentSparkReporter(".\\reports\\"+repName);
		
		sparkreporter.config().setDocumentTitle("opencart Automation Report"); //Title of Report
		sparkreporter.config().setReportName("opencart Functional Testing"); //name of the report
		sparkreporter.config().setTheme(Theme.DARK);
		
		extent=new ExtentReports();
		extent.attachReporter(sparkreporter);
		
		extent.setSystemInfo("Application", "opencart");
		extent.setSystemInfo("Module", "Admin");
		extent.setSystemInfo("Sub Module", "Customers");
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Environment", "QA");
		
		String os=context.getCurrentXmlTest().getParameter("os");
		extent.setSystemInfo("Operating System", os);
		
		String browser=context.getCurrentXmlTest().getParameter("browser");
		extent.setSystemInfo("browser", browser);
		
		List<String> includedGroups=context.getCurrentXmlTest().getIncludedGroups();
		if(!includedGroups.isEmpty())
		{
			extent.setSystemInfo("Groups", includedGroups.toString());
		}
	  }
	
	public void onTestSuccess(ITestResult result) {
	    
		test= extent.createTest(result.getTestClass().getName());	//create new entry in the report
		test.assignCategory(result.getMethod().getGroups());
		test.log(Status.PASS,result.getName()+"got successfully executed"); 	//update result p/f/s
	  }
	
	public void onTestFailure(ITestResult result) {
	    
		test= extent.createTest(result.getTestClass().getName());	//create new entry in the report
		test.assignCategory(result.getMethod().getGroups());
		test.log(Status.FAIL, result.getName()+"got failed"); 	//update result p/f/s
		test.log(Status.INFO, result.getThrowable().getMessage());
		
		try
		{
			String imgPath = new BaseClass().captureScreen(result.getName());
			test.addScreenCaptureFromPath(imgPath);
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	  }
	
	public void onTestSkipped(ITestResult result) {
	    
		test= extent.createTest(result.getTestClass().getName());	//create new entry in the report
		test.assignCategory(result.getMethod().getGroups());
		test.log(Status.SKIP, result.getName()+"got skipped"); 
		test.log(Status.INFO, result.getThrowable().getMessage()); 	//update result p/f/s
	  }

	public void onFinish(ITestContext context) {
	    
		extent.flush();
		
		String pathOfExtentReport = System.getProperty("user.dir")+"\\reports\\"+repName;
		File extentReport = new File(pathOfExtentReport);
		try
		{
			Desktop.getDesktop().browse(extentReport.toURI());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		/*try 
		{ 
			    URL url = new
				URL("file:///"+System.getProperty("user.dir")+"\\reports\\"+repName);    

				// Create the email message
				ImageHtmlEmail email = new ImageHtmlEmail();
				email.setDataSourceResolver(new DataSourceUrlResolver(url));
				email.setHostName("smtp.googlemail.com");
				email.setSmtpPort (465);
				email.setAuthenticator(new DefaultAuthenticator("pavanoltraining@gmail.com","password"));
				email.setSSLOnConnect(true);

				email.setFrom("pavanoltraining@gmail.com"); //Sender

				email.setSubject("Test Results");
				email.setMsg("Please find Attached Report....");
				email.addTo("pavankumar.busyqa@gmail.com"); //Receiver

				email.attach(url, "extent report", "please check report...");
				email.send(); // send the email

				}
				catch(Exception e) { e.printStackTrace(); }
				*/


		
		
		
	  }

}
