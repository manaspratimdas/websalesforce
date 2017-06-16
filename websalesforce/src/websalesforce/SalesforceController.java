package websalesforce;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SalesforceController {
	
	static final String USERNAME = "manasdas@systemsplus.com";
	static final String PASSWORD = "Passw0rd!23";
	static final String LOGINURL = "https://ap5.salesforce.com";
	static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
	static final String CLIENTID = "3MVG9d8..z.hDcPINdzSLc.18_81q0lUvUfw0Iac.ZFL.ZtuWLr_wlXVtzKGPDTk.7Az6sf61wbcwCLqsrDIU";
	static final String CLIENTSECRET = "4436621224688664918";
	
	
	@RequestMapping(value = "/check", method = RequestMethod.GET)
    public String checkConnection() {
       
		StringBuilder msg=connect2SF();
        return "cneck done"+msg;
    }

	private StringBuilder connect2SF() {
		
		StringBuilder msg=new StringBuilder();
		
		HttpClient httpclient = HttpClientBuilder.create().build();
		String loginURL = LOGINURL + GRANTSERVICE + "&client_id=" + CLIENTID + "&client_secret=" + CLIENTSECRET
				+ "&username=" + USERNAME + "&password=" + PASSWORD;
		System.out.print(loginURL);

		HttpPost httpPost = new HttpPost(loginURL);
		HttpResponse response = null;
		try {
			// Execute the login POST request
			response = httpclient.execute(httpPost);
		} catch (ClientProtocolException cpException) {
			cpException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		final int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			msg=msg.append(statusCode);
			System.out.println("Error authenticating to Force.com: " + statusCode);
			// Error is in EntityUtils.toString(response.getEntity())
			return msg;
		}

		String getResult = null;
		try {
			getResult = EntityUtils.toString(response.getEntity());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		JSONObject jsonObject = null;
		String loginAccessToken = null;
		String loginInstanceUrl = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			loginAccessToken = jsonObject.getString("access_token");
			loginInstanceUrl = jsonObject.getString("instance_url");
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		}
		System.out.println(response.getStatusLine());
		System.out.println("Successful login");
		System.out.println("instance URL: " + loginInstanceUrl);
		System.out.println("access token/session ID: " + loginAccessToken);
		
		msg=msg.append(response.getStatusLine()).append("    ").append("Successful login   ").append(loginInstanceUrl).append("  ").append(loginAccessToken);

		// release connection
		httpPost.releaseConnection();
		return msg;
		
	}

}
