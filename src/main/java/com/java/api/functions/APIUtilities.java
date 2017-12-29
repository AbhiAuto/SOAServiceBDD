/**
 * Date : 02-June-2017 
 * Developed By: Abhilash G
 * Updated on : 02-August-2017
 */
package com.hdfcUW.api.functions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


public class APIUtilities 
{
	private final static Logger log = Logger.getLogger(APIUtilities.class);
	public static JSONObject mainJsonRequest =  new JSONObject();
	public static String getUri = null;
	public static HttpResponse httpsResponse= null;
	public static String responseString = null;
	public static String strXMlRequest = null;

	
	public static void restApiPostReqRes( String postUrl, String strReqfileName )
	{
		try{
			String strRequestBody = getstrRequestBody(strReqfileName);
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(postUrl);
		
		    StringEntity params =new StringEntity(strRequestBody);
		    
		    request.addHeader("content-type", "application/xml");
		    request.setEntity(params);
		    
		    httpsResponse = httpClient.execute(request);
		    System.out.println(httpsResponse);
		    responseValidation();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static String getstrRequestBody(String strReqfileName) {
		// To fetch the request body from the file.
		try{
			String strProjectDir = System.getProperty("user.dir");
			File file = new File(strProjectDir + "\\RequestXMLs\\"+strReqfileName+".txt");
			if(file.exists())
			{
				String strText = new String(Files.readAllBytes(Paths.get(strProjectDir + "\\RequestXMLs\\"+strReqfileName+".txt")));
				strXMlRequest = strText;
				log.info("XML request is successfuly fetched. FileName : "+strReqfileName);
			}
		}catch(Exception e)
			{
				log.info("Failed to fetch the request template from "+strReqfileName+" file");
			}
		return strXMlRequest;
	}

	public static int apiResCode()
	{
		int statusCode = httpsResponse.getStatusLine().getStatusCode();
		
		if(statusCode!=200)
		{
			Assert.fail("Status is :" + statusCode);
		}
		return statusCode;
	}

	public static void responseValidation()
	{
		try {
			responseString = EntityUtils.toString(httpsResponse.getEntity());
			log.info("Reponse : " +responseString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static String fetchValFromResponse(String strJsonObj, String strObj) {
		//To validate the value in the response 
		JSONObject mainJsonReponse = new JSONObject(responseString);
		JSONObject jsonReponseObj = mainJsonReponse.getJSONObject(strJsonObj);
		String strObjValue = jsonReponseObj.getString(strObj);
		return strObjValue;
	}
	public static String fetchValFromResponseXml(String strJsonObj, String strObj) {
		String strValue=null;
		String strXpath = strJsonObj + "/" + strObj;
		try{	
			Document dom;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(new InputSource(new ByteArrayInputStream(responseString.getBytes("utf-8"))));
			 	XPath xPath = XPathFactory.newInstance().newXPath();
	            Node node = (Node) xPath.evaluate(strXpath, dom, XPathConstants.NODE);
	            strValue = node.getTextContent();
	            log.info("Successfully found the Xpath : "+strXpath+" and Value : "+strValue );
	            
			      } catch(Exception e){
			    	  log.info("Failed to fetch the value for xpath from the response : "+strXpath);
			    	  Assert.fail("Failed to fetch the value for xpath from the response : " + strXpath);
			      }
			return strValue;
	   }

}
