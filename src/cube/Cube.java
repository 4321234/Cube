package cube;

import infrastructure.*;
import infrastructure.HTMLTableParser;

import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.json.JSONObject;




public class Cube {
	public static boolean DEBAG = true;

	public static void main(String[] args) throws Exception {
		if (args.length >= 8)
		{
			Constants.HOST = args[0];
			Constants.PORT = args[1];
			Constants.USERNAME = args[2];
			Constants.PASSWORD = args[3];
			Constants.DOMAIN = args[4];
			Constants.PROJECT = args[5];
			Constants.PARAM = args[6];
			Constants.ID = args[7];
			
			if (args.length == 9) {
				Constants.JSON_PARAM = args[8];
				Constants.CUSTOM_PARAM = true;
			}
			
			System.out.println(Constants.HOST + "|" + Constants.PORT + "|" + Constants.USERNAME + "|" + Constants.PASSWORD + "|" + Constants.DOMAIN + "|" + Constants.PROJECT + "|" + Constants.PARAM + "|" + Constants.ID + "|" + Constants.JSON_PARAM + "|" + Constants.CUSTOM_PARAM);
			switch (Constants.PARAM) {
            case "o":	
            		new Cube().createOrder(Constants.ID,
	        		"http://" + Constants.HOST + ":" +
	                        Constants.PORT + "/qcbin",
	                Constants.DOMAIN,
	                Constants.PROJECT,
	                Constants.USERNAME,
	                Constants.PASSWORD);
                     break;
            case "r":  
            		new Cube().checkOrder(Constants.ID,
	        		"http://" + Constants.HOST + ":" +
	                        Constants.PORT + "/qcbin",
	                Constants.DOMAIN,
	                Constants.PROJECT,
	                Constants.USERNAME,
	                Constants.PASSWORD);
            		break;
            default: 
            		System.out.println("syntax error" );
            		break;
			}
		
		}
		
		else
		{
			
			System.out.println("syntax error" );
			
		}
	}

private RestConnector con;

public Cube() {
con = RestConnector.getInstance();
}

/**
* @param username
* @param password
* @return true if authenticated at the end of this method.
* @throws Exception
*
* convenience method used by other examples to do their login
*/
public boolean login(String username, String password) throws Exception {

String authenticationPoint = this.isAuthenticated();
if (authenticationPoint != null) {
    return this.login(authenticationPoint, username, password);
}
return true;
}

/**
* @param loginUrl
*            to authenticate at
* @param username
* @param password
* @return true on operation success, false otherwise
* @throws Exception
*
* Logging in to our system is standard http login (basic authentication),
* where one must store the returned cookies for further use.
*/
public boolean login(String loginUrl, String username, String password)
throws Exception {

//create a string that lookes like:
// "Basic ((username:password)<as bytes>)<64encoded>"
byte[] credBytes = (username + ":" + password).getBytes();
String credEncodedString = "Basic " + Base64Encoder.encode(credBytes);

Map<String, String> map = new HashMap<String, String>();
map.put("Authorization", credEncodedString);

Response response = con.httpGet(loginUrl, null, map);

boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;

return ret;
}

//-------------------------------------------------------------------Get Data------------------------------------>
public String readData(String nrUslugi, final String serverUrl,
		  final String domain, final String project, String username,
		  String password) throws Exception {

	RestConnector con =
	        RestConnector.getInstance().init(
	                new HashMap<String, String>(),
	                serverUrl,
	                domain,
	                project);

	Cube start =
		    new Cube();


	String authenticationPoint = start.isAuthenticated();

	Assert.assertTrue(
			"response from isAuthenticated means we're authenticated. that can't be.",
			        authenticationPoint != null);
	
	boolean loginResponse =
			start.login(authenticationPoint, username, password);
	
			Assert.assertTrue("failed to login.", loginResponse);
	        Assert.assertTrue(
	        		 "login did not cause creation of Light Weight Single Sign On(LWSSO) cookie.",
	        		                con.getCookieString().contains("LWSSO_COOKIE_KEY"));
	        
	        Assert.assertNull(
	                "isAuthenticated returned not authenticated after login.",
	                start.isAuthenticated());
	
	        String qcsessionurl = 
	        		con.buildUrl("rest/site-session");
	        
	        Map<String, String> requestHeaders = 
	               new HashMap<String, String>();       
	        
	        requestHeaders.put("Content-Type", "application/xml");
	        requestHeaders.put("Accept", "application/xml");
	        
	        Response resp = 
	        con.httpPost(qcsessionurl, 
	        null, 
	        requestHeaders);
	        
	        //System.out.println("resp: "+resp.toString());
	        
	        String requirementsUrl = 
	        //con.buildEntityCollectionUrl("defect");
	        con.buildEntityCollectionUrl("requirement");
	        		     
	        String urlOfResourceWeWantToRead = 
	        		//requirementsUrl + "/" + 6;
	        		requirementsUrl + "/" + nrUslugi;
	        
	        //System.out.println("urlOfResourceWeWantToRead: " + urlOfResourceWeWantToRead);
	        
	        Response serverResponse = 
	        		con.httpGet(urlOfResourceWeWantToRead, 
	        		null, 
	        		requestHeaders);
	        
	        //System.out.println("serverResponse: " + serverResponse);
	        
	        
	       
	        String postedEntityReturnedXml = 
	        serverResponse.toString();
	        
	       
	        
	        Entity entity = (Entity)EntityMarshallingUtils.marshal(Entity.class, 
	               postedEntityReturnedXml);
	        
	        List<Entity.Fields.Field> fields = entity.getFields().getField();
	        
	        
	        /*
	        String reqHtmlString;
	        String reqNameField;
	        
	        for (int i = 1; i <= 87; i++)
	        {
	        	reqHtmlString = ((Entity.Fields.Field)fields.get(i)).getValue().toString();
	        	reqNameField = ((Entity.Fields.Field)fields.get(i)).getName().toString();
	        	
	        }*/
	        //control parameter
	        String defPRM = ((Entity.Fields.Field)fields.get(45)).getValue().toString();

	        Reader reader = new StringReader(defPRM);
	        HTMLEditorKit.Parser parser = new ParserDelegator();
	        parser.parse(reader, new HTMLTableParser(), true);
	        
	        ArrayList<String> controlParameterList = HTMLTableParser.htmlString;
	        System.out.println(controlParameterList);
	        String controlParameter = "";
	        
	        if (Constants.CUSTOM_PARAM) {
	        	JSONObject jsonObj = new JSONObject(Constants.JSON_PARAM);
	        	System.out.println(jsonObj.length());
	        	
	        }else {
	        	
		        for(int i=0;i<controlParameterList.size();i++){
		        	
		        	controlParameter = controlParameter + controlParameterList.get(i);
		        } 
	        	
	        }
	        

	        
	        
	        HTMLTableParser.htmlString.clear();
	        
	        reader.close();
	        
	        //System.out.println("DEF_PRM: " + controlParameter);
	        
	      //context
	        String context = ((Entity.Fields.Field)fields.get(44)).getValue().toString();
	        
	        context = context.replaceAll("\\<[^>]*>","").replaceAll("&nbsp;", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">");

	        byte[] encode = context.getBytes("UTF-8");
	        StringBuilder sb = new StringBuilder(encode.length * 2);
	        for(byte b: encode)
	        sb.append(String.format("%02x", b & 0xff));
	        
	        context = sb.toString();

	        //userName - Owner
	        String userName = username;

	        //Environment
	        String userZeroSiedem = ((Entity.Fields.Field)fields.get(84)).getValue().toString();
	        
	        //Service_class
	        String userZeroCztery = ((Entity.Fields.Field)fields.get(83)).getValue().toString();
	        
	        //req_id
	        String userZeroJeden = ((Entity.Fields.Field)fields.get(13)).getValue().toString();
	        
	        //ServiceName
	        String userZeroDwa = ((Entity.Fields.Field)fields.get(38)).getValue().toString();
	        
	        //ServiceType
	        String userZeroPiec = ((Entity.Fields.Field)fields.get(87)).getValue().toString();
	        
	        //ServiceStatus
	        String userZeroSzesc = ((Entity.Fields.Field)fields.get(81)).getValue().toString();
	        
	        //UsedTools
	        String userJedenCztery = ((Entity.Fields.Field)fields.get(60)).getValue().toString();
	        
	        //?
	        String userZeroTrzy = ((Entity.Fields.Field)fields.get(86)).getValue().toString();
	        
	        // Valid Date
	        SimpleDateFormat validdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        Date validdate = new Date();
	        String validdzisiaj = validdateFormat.format(validdate);
	        
	        //container
	        String userJedenPiec = ((Entity.Fields.Field)fields.get(62)).getValue().toString();
	        
	        String returnValue =
	        		
	        		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
	        		"<Entity Type=\"defect\">" +
	        				"<Fields>" +
	        					"<Field Name=\"detected-by\">" +
	        						"<Value>" + userName + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"creation-time\">" + 
	        						"<Value>" + validdzisiaj + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"severity\">" + 
	        						"<Value>1-Low</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"name\">" + 
	        						"<Value>Zamowienie uslugi</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-07\">" + 
	        						"<Value>" + userZeroSiedem.replace("[", "").replace("]", "") + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-13\">" + 
	        						"<Value>WAITING</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-04\">" + 
	        						"<Value>" + userZeroCztery.replace("[", "").replace("]", "") + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-01\">" + 
	        						"<Value>" + userZeroJeden.replace("[", "").replace("]", "") + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-02\">" + 
	        						"<Value>" + userZeroDwa.replace("[", "").replace("]", "") + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-05\">" + 
	        						"<Value>" + userZeroPiec.replace("[", "").replace("]", "") + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-06\">" + 
	        						"<Value>" + userZeroSzesc.replace("[", "").replace("]", "") + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"status\">" + 
	        						"<Value>New</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-14\">" + 
	        						"<Value>" + userJedenCztery.replace("[", "").replace("]", "") + "</Value>" +
	        					"</Field>" + 
	        					"<Field Name=\"user-15\">" + 
	        						"<Value>" + userJedenPiec.replace("[", "").replace("]", "") + "</Value>" + 
	        					"</Field>" +
	        					"<Field Name=\"user-03\">" + 
	        						"<Value>" + userZeroTrzy.replace("[", "").replace("]", "") + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"owner\">" + 
	        						"<Value>" + userName + "</Value>" + 
	        					"</Field>" + 
	        					"<Field Name=\"user-25\">" + 
	        						"<Value>" + controlParameter  + "</Value>" +
	        					"</Field>" +
	        					"<Field Name=\"user-26\">" + 
        							"<Value>" + context + "</Value>" + 
        						"</Field>" +
	        					"<Field Name=\"user-12\">" + 
	        						"<Value>" + validdzisiaj + "</Value>" + 
	        					"</Field>" + 
	        				"</Fields>" +
	        			"</Entity>";
	        
    
    return returnValue;
}
//---------------------------------------------------------------------------------------------------------------<
//-------------------------------------------------------------------Create Order-------------------------------->
public void createOrder(String nrUslugi, final String serverUrl,
		  final String domain, final String project, String username,
		  String password) throws Exception {
	
	RestConnector con =
	        RestConnector.getInstance().init(
	                new HashMap<String, String>(),
	                serverUrl,
	                domain,
	                project);

	Cube start =
		    new Cube();

	String authenticationPoint = start.isAuthenticated();

	Assert.assertTrue(
			"response from isAuthenticated means we're authenticated. that can't be.",
			        authenticationPoint != null);
	
	boolean loginResponse =
			start.login(authenticationPoint, username, password);
	
			Assert.assertTrue("failed to login.", loginResponse);
	        Assert.assertTrue(
	        		 "login did not cause creation of Light Weight Single Sign On(LWSSO) cookie.",
	        		                con.getCookieString().contains("LWSSO_COOKIE_KEY"));
	        
	        Assert.assertNull(
	                "isAuthenticated returned not authenticated after login.",
	                start.isAuthenticated());
	        
	        
	        String qcsessionurl = 
	        		con.buildUrl("rest/site-session");
	        
	        Map<String, String> requestHeaders = 
	               new HashMap<String, String>();       
	        
	        requestHeaders.put("Content-Type", "application/xml");
	        requestHeaders.put("Accept", "application/xml");
	        
	        Response resp = 
	        con.httpPost(qcsessionurl, 
	        null, 
	        requestHeaders);
	        
	        String defectsUrl = con.buildEntityCollectionUrl("defect");
	            
	        requestHeaders.put("Content-Type", "application/xml");
	        requestHeaders.put("Accept", "application/xml");
	        
	        String defectXml = 
	        			
	                new Cube().readData(nrUslugi,
	        		"http://" + Constants.HOST + ":" +
	                        Constants.PORT + "/qcbin",
	                Constants.DOMAIN,
	                Constants.PROJECT,
	                Constants.USERNAME,
	                Constants.PASSWORD);
	        
	        //System.out.println(defectXml);
	        		
		        
				Response postedEntityResponse = con.httpPost(defectsUrl, 
		        											 defectXml .getBytes(),
		        											 requestHeaders);
				//System.out.println(postedEntityResponse);
				
				
				String odp = ((Iterable<?>)postedEntityResponse.getResponseHeaders().get("Location")).toString();
				
				int pozycjaStr = odp.indexOf("defects");
				odp = odp.substring(pozycjaStr + 8, odp.length() - 1);

				System.out.println("OrderNumber: " + odp);
	        
	        
	   	 //-----------Exit-------------------------->   
	        start.logout();
	      //-------------------------------------------<
	
	
}
//---------------------------------------------------------------------------------------------------------------<
//-------------------------------------------------------------------Check Order--------------------------------->
public void checkOrder(String nrZam, final String serverUrl,
		  final String domain, final String project, String username,
		  String password) throws Exception {
	
	RestConnector con =
	        RestConnector.getInstance().init(
	                new HashMap<String, String>(),
	                serverUrl,
	                domain,
	                project);

	Cube start =
		    new Cube();


	String authenticationPoint = start.isAuthenticated();

	Assert.assertTrue(
			"response from isAuthenticated means we're authenticated. that can't be.",
			        authenticationPoint != null);
	
	boolean loginResponse =
			start.login(authenticationPoint, username, password);
	
			Assert.assertTrue("failed to login.", loginResponse);
	        Assert.assertTrue(
	        		 "login did not cause creation of Light Weight Single Sign On(LWSSO) cookie.",
	        		                con.getCookieString().contains("LWSSO_COOKIE_KEY"));
	        
	        Assert.assertNull(
	                "isAuthenticated returned not authenticated after login.",
	                start.isAuthenticated());
	
	        String qcsessionurl = 
	        		con.buildUrl("rest/site-session");
	        
	        Map<String, String> requestHeaders = 
	               new HashMap<String, String>();       
	        
	        requestHeaders.put("Content-Type", "application/xml");
	        requestHeaders.put("Accept", "application/xml");
	        
	        Response resp = 
	        con.httpPost(qcsessionurl, 
	        null, 
	        requestHeaders);
	        
	        //System.out.println("resp: "+resp.toString());
	        
	        String requirementsUrl = 
	        con.buildEntityCollectionUrl("defect");
	        
	        
	        String urlOfResourceWeWantToRead = 
	        		requirementsUrl + "/" + nrZam;
	        		
	        
	        //System.out.println("urlOfResourceWeWantToRead: " + urlOfResourceWeWantToRead);
	        
	        Response serverResponse = 
	        		con.httpGet(urlOfResourceWeWantToRead, 
	        		null, 
	        		requestHeaders);
	        
	        //System.out.println("serverResponse: " + serverResponse);
	        
	        
	       
	        String postedEntityReturnedXml = 
	        serverResponse.toString();
	        
	       
	        
	        Entity entity = (Entity)EntityMarshallingUtils.marshal(Entity.class, 
	               postedEntityReturnedXml);
	        
	        List<Entity.Fields.Field> fields = entity.getFields().getField();
	        /* 
	        String name;
	        String value;
	        
	        for (int i = 1; i <= 62; i++)
	        {
	        	value = ((Entity.Fields.Field)fields.get(i)).getValue().toString();
	        	name = ((Entity.Fields.Field)fields.get(i)).getName().toString();
	        	
	        	//System.out.println("field: " + i + " - " + name + " - " + value);
	        	
	        }
	        */
	        
	        
		   	 //-----------Exit-------------------------->
	        System.out.println("ID: " + ((Entity.Fields.Field)fields.get(6)).getValue());
	        System.out.println("STATUS: " + ((Entity.Fields.Field)fields.get(41)).getValue());
	        System.out.println("RESULT: " + ((Entity.Fields.Field)fields.get(21)).getValue());   
	        start.logout();
	        //-------------------------------------------<
	
	
}
//---------------------------------------------------------------------------------------------------------------<

/**
* @return true if logout successful
* @throws Exception
*             close session on server and clean session cookies on client
*/
public boolean logout() throws Exception {

//note the get operation logs us out by setting authentication cookies to:
// LWSSO_COOKIE_KEY="" via server response header Set-Cookie
Response response =
        con.httpGet(con.buildUrl("authentication-point/logout"),
        null, null);

return (response.getStatusCode() == HttpURLConnection.HTTP_OK);

}

/**
* @return null if authenticated.<br>
*         a url to authenticate against if not authenticated.
* @throws Exception
*/
public String isAuthenticated() throws Exception {

String isAuthenticateUrl = con.buildUrl("rest/is-authenticated");
String ret;

Response response = con.httpGet(isAuthenticateUrl, null, null);
int responseCode = response.getStatusCode();

//if already authenticated
if (responseCode == HttpURLConnection.HTTP_OK) {

    ret = null;
}

//if not authenticated - get the address where to authenticate
// via WWW-Authenticate
else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

    Iterable<String> authenticationHeader =
            response.getResponseHeaders().get("WWW-Authenticate");

    String newUrl =
        authenticationHeader.iterator().next().split("=")[1];
    newUrl = newUrl.replace("\"", "");
    newUrl += "/authenticate";
    ret = newUrl;
}

//Not ok, not unauthorized. An error, such as 404, or 500
else {

    throw response.getFailure();
}

return ret;
}

/**
 * @param collectionUrl
 *            the url of the collection of the relevant entity type.
 * @param postedEntityXml
 *            the xml describing an instance of said entity type.
 * @return the url of the newly created entity.
 */
public String createEntity(String collectionUrl, String postedEntityXml)
        throws Exception {

    Map<String, String> requestHeaders = new HashMap<String, String>();
    requestHeaders.put("Content-Type", "application/xml");
    requestHeaders.put("Accept", "application/xml");

    // As can be seen in the implementation below, creating an entity
    //is simply posting its xml into the correct collection.
    Response response = con.httpPost(collectionUrl,
            postedEntityXml.getBytes(), requestHeaders);

    Exception failure = response.getFailure();
    if (failure != null) {
        throw failure;
    }

    /*
     Note that we also get the xml of the newly created entity.
     at the same time we get the url where it was created in a
     location response header.
    */
    String entityUrl =
            response.getResponseHeaders().get("Location").iterator().next();

    return entityUrl;
}
}
