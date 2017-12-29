package com.hdfcUW.api.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.runner.RunWith;

import com.hdfcUW.api.functions.APIUtilities;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.junit.Cucumber;
import gherkin.formatter.model.DataTableRow;


@RunWith(Cucumber.class)
public class stepDefinitions {
	private final static Logger log = Logger.getLogger(APIUtilities.class);
	public static String strOrgAPIName = null;
	public static String apiMethod = null;
	public static String strUri = null;
	
	InputStream inputStream;
	
    @Given("^I have a \"([^\"]*)\" rest api$")
    public void i_have_a_something_method_api(String strAPIName) throws Throwable {
    	strOrgAPIName = strAPIName;	
    }

    @When("^I performs \"([^\"]*)\" request with \"([^\"]*)\"$")
    public void i_performs_something_request(String strRequest,String strRequestBody) throws Throwable {
    	
    	String strProjectDir = System.getProperty("user.dir");
		String strVal = null;
		//Fetching the value from the properties file
		File file = new File(strProjectDir + "\\resources\\apiConfig.properties");
		FileInputStream fileInput = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInput);
		fileInput.close();

		Enumeration enuKeys = properties.keys();
		while (enuKeys.hasMoreElements()) {
			strVal= properties.getProperty(strOrgAPIName);
			System.out.println(strOrgAPIName + ": " + strVal);
			if(strVal != null)
			{
				break;
			}
		}
			//Fetching the URI using the strArg value
			strUri = strVal;
			System.out.println("ServiceURL is :"+strUri);
			
	     if(strRequest.contains("POST"))
	     {
	    	 APIUtilities.restApiPostReqRes(strUri,strRequestBody);
	     }
    }
    
    @Then("^the response status should be \"([^\"]*)\"$")
    public void the_response_status_should_be_something(String strExpCode) throws Throwable {
    	int responseCode = APIUtilities.apiResCode();
    	String strActCode = Integer.toString(responseCode);
    	Assert.assertEquals(strExpCode, strActCode);
    	log.info("Response Code is : " +strActCode);
    }
    
    @And("^the response should contain in \"([^\"]*)\" object is:$")
    public void the_response_should_contain_in_something_object_is(String strXML,DataTable tableValues) throws Throwable {
    	String strObjVal = null;
		String strXMLtag  = null;
    	List<List<String>> data = tableValues.raw();
		final List<DataTableRow> rows = tableValues.getGherkinRows();
		final List<String> columns = rows.get(0).getCells();
		columns.size();
		for (int j = 1; j < columns.size(); j++) {
			for (int i = 0; i < rows.size(); i++) {
				strXMLtag = data.get(i).get(0);
				strObjVal = data.get(i).get(j);
				String objValue = APIUtilities.fetchValFromResponseXml(strXML,strXMLtag);
				Assert.assertEquals(strObjVal,objValue);
			}
		}
		log.info("Assertion passed");
    }
    
  }