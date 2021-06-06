package Amazon;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class MobileSearch {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	System.setProperty("webdriver.chrome.driver", "chromedriver");

		WebDriver driver = new ChromeDriver();
		driver.get("https://www.amazon.in/");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(50000, TimeUnit.MILLISECONDS);
		
		 try {

			//JDBC Connection
			 	Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/amazon","root","root");
	            Statement stmt = con.createStatement();  
	            ResultSet rs = stmt.executeQuery("select * from search"); 
	            
	        //Select category  
	        WebElement AllCategoryDropdown = driver.findElement(By.xpath("//select[@title ='Search in']"));
	    	Select category = new Select(AllCategoryDropdown) ;
	    	
	    	//Search
	    	WebElement searchField = driver.findElement(By.xpath("//input[@name ='field-keywords']"));
	     	     
	    	while(rs.next()) {
                String cat = rs.getNString(1);
                category.selectByVisibleText(cat); //pass the extracted value from db to the category locator
                
                String searchValue = rs.getString(2);
                searchField.sendKeys(searchValue); // pass the extracted value from db to the search
	    	}        
	        
	    	WebElement submitValue = driver.findElement(By.xpath("//input[@type ='submit']")); //click on search
            submitValue.click();
	    	
            //get the total result count
	    	List<WebElement> resultList = driver.findElements(By.xpath("//*[@data-component-type='s-search-result']"));
	    	System.out.println("Total search count : " + resultList.size());
	   
	    	//Assert the result
	    	WebElement resultMsg = driver.findElement(By.xpath("//*[contains(text(), '1-24')]"));
	    	String assertionMsg = resultMsg.getText();
	    	String msg = "1-24 of over 60,000 results for";
	    	
	    	if(assertionMsg.equals(msg))
	    	{
	    		System.out.println("Assertion Pass");
	    	}
	    	
	    	else {
	    		
	    		System.out.println("Assertion Failed");
	    	}
	    	
	    	//get the name of the results
	    	List<WebElement> resultName = driver.findElements(By.xpath("//span[@class='a-size-medium a-color-base a-text-normal']"));
	    	
	    	for(int i=0;i <resultList.size();i++)  	
		    {
	    		String mobileName = resultName.get(i).getText();
	    		System.out.println("Mobile name : " + mobileName);
  }		  
		//Take the screenshot of the result
    	TakesScreenshot TsObj = (TakesScreenshot)driver;
    	File myFile = TsObj.getScreenshotAs(OutputType.FILE);
    	
    	
    	try {	
    		FileUtils.copyFile(myFile,  new File("searchresult.png"));
    	}
    	catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
    	
	    	driver.close();
	    	
		 } catch (SQLException e) {
	            
	           System.out.println("SQL Exception");
	        } 
		
	}

}

