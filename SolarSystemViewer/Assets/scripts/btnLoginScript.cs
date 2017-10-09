﻿/***
 * 

Copyright 2017 Idaho National Laboratory.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

***/

using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System.Net;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.IO;
using UnityEngine.Networking;
using System.Collections.Generic;
using Newtonsoft.Json;


delegate void WWWAction (UnityWebRequest request);

public class btnLoginScript : MonoBehaviour {

	public Canvas canvas;
	public Canvas cvsSessions;
	public Planet_Data_Loader loader;
	public InputField ifUsername;
	public InputField ifPassword;
	public InputField ifServerURL;
	public string sessionId = "";

	private string baseURL;
	private static string LOGIN_URL = "login";
	private static string SESSION_LIST_URL = "api/sessions/";
	private static string URL_PATH_SEPERATOR = "/";
	private string error = null;

	private const int REDIRECT_HTTP_STATUS = 302;
	private const string COOKIE_HEADER_NAME = "Cookie";
	private const string SET_COOKIE_HEADER_NAME = "Set-Cookie";
	private const string LOCATION_HEADER = "Location";

	private const string SPRING_SESSION_COOKIE_NAME = "JSESSIONID";
	private const char COOKIE_SEPARATOR = ';';
	private const char COOKIE_EQUALS = '=';

	private const string USERNAME_FIELD = "username";
	private const string PASSWORD_FIELD = "password";

	private const string ERROR_PARAM = "?error";


	private static bool TrustCertificate(object sender, X509Certificate x509Certificate, X509Chain x509Chain, SslPolicyErrors sslPolicyErrors)
	{
		// all Certificates are accepted
		return true;
	}


	// Use this for initialization
	void Start ()
	{
		ServicePointManager.ServerCertificateValidationCallback = TrustCertificate;

	}


	public void onClick()
	{
		baseURL = generateURL (ifServerURL.text);
		string url = baseURL + LOGIN_URL;


		WWWForm form = new WWWForm ();
		form.AddField (USERNAME_FIELD, ifUsername.text);
		form.AddField (PASSWORD_FIELD, ifPassword.text);

		UnityWebRequest request = UnityWebRequest.Post (url, form);
		//needed to handle redirect and get cookie
		request.redirectLimit = 0;
		StartCoroutine (WaitForRequest (request, onComplete));




		//canvas.gameObject.SetActive (false);
		//loader.startPlayback ();*/



	}


	private IEnumerator WaitForRequest(UnityWebRequest request, WWWAction onComplete)
	{
		yield return request.Send();

		// check for errors
		//this will return error on redirect limit right now
		if (!request.isNetworkError)
		{
			string results = request.downloadHandler.text;
			if (request.url.EndsWith (ERROR_PARAM))
			{
				ModalDialog tmp = new ModalDialog ("Login Error","Invalid username/password.", "OK",null);
				tmp.show ();
			}
			else
				onComplete (request);
		}
		else
		{
			error = request.error;
			print (error + " resultCode=" + request.responseCode);
			//check for the redirect
			if (request.responseCode == REDIRECT_HTTP_STATUS) {
				Dictionary<string,string> headers = request.GetResponseHeaders ();
				string location = headers [LOCATION_HEADER];
				if (!string.IsNullOrEmpty (location))
				{
					if (location.EndsWith (ERROR_PARAM))
					{
						ModalDialog tmp = new ModalDialog ("Login Error", "Invalid username/password.", "OK", null);
						tmp.show ();
					}
					else
						onComplete (request);
				}
				else
					onComplete (request);

			}
			else
			{
				ModalDialog tmp = new ModalDialog ("Unknown Error",error, "OK",null);
				tmp.show ();
			}
		}

	}

	private void onComplete(UnityWebRequest request)
	{

		print ("Login success");

		baseURL = generateURL (ifServerURL.text);
		string url = baseURL + SESSION_LIST_URL;


		ServicePointManager.ServerCertificateValidationCallback = TrustCertificate;
		sessionId = getSessionId (request, sessionId);
		UnityWebRequest request2 = UnityWebRequest.Get(url);
		setSessionInfo (request2, sessionId);
		request2.redirectLimit = 0;

		//now run the listing
		StartCoroutine (WaitForRequest2 (request2, onCompleteListing));


	}


	private IEnumerator WaitForRequest2(UnityWebRequest request, WWWAction onComplete)
	{
		yield return request.Send();

		// check for errors
		if (!request.isNetworkError)
		{
			string results = request.downloadHandler.text;
			onCompleteListing(request);
		}
		else
		{
			error = request.error;
			print (error + " resultCode=" + request.responseCode);
			ModalDialog tmp = new ModalDialog ("Session Listing Error",error, "OK",null);
			tmp.show ();
		}



	}


	private string generateURL(string baseURL)
	{
		string url = baseURL;
		if (!url.EndsWith (URL_PATH_SEPERATOR))
			url += URL_PATH_SEPERATOR;

		return url;
	}





	private string getSessionId(UnityWebRequest request, string currentSessionId)
	{
		string sessionId = currentSessionId;

		Dictionary<string,string> headers = request.GetResponseHeaders ();
		if (headers != null) {
			string cookies = headers [SET_COOKIE_HEADER_NAME];
			if (!string.IsNullOrEmpty (cookies)) {
				string[] parts = cookies.Split (COOKIE_SEPARATOR);
				if (parts != null)
				{
					foreach (string part in parts) 
					{
						string[] parts2 = part.Split (COOKIE_EQUALS);
						if ((parts2.Length == 2) && (parts2 [0] == SPRING_SESSION_COOKIE_NAME))
							sessionId = parts2 [1];
					}
				}
			}
		}
		return sessionId;
	}


	private void setSessionInfo(UnityWebRequest request, string sessionId)
	{
		request.SetRequestHeader (COOKIE_HEADER_NAME, SPRING_SESSION_COOKIE_NAME + COOKIE_EQUALS + sessionId);
	}
		



	public void onCompleteListing(UnityWebRequest request)
	{
		print ("LISTING success");
		print (request.downloadHandler.text);
		//ModalDialog tmp = new ModalDialog ("Login Success",request.downloadHandler.text, "OK",() => {loader.startPlayback (); return true;});
		//tmp.show ();
		//JsonList<LIVESession> list = JsonUtility.FromJson<JsonList<LIVESession>> (request.downloadHandler.text);
		JsonSerializerSettings settings = new JsonSerializerSettings();
		settings.MissingMemberHandling = MissingMemberHandling.Ignore;
		settings.CheckAdditionalContent = false;
		JsonList<SIEVASSession> list = JsonConvert.DeserializeObject<JsonList<SIEVASSession>> (request.downloadHandler.text, settings);
			
		print (list);
		canvas.gameObject.SetActive (false);
		GameObject tblSessions = cvsSessions.transform.Find ("tblSessions").gameObject;
		SessionViewController controller = tblSessions.GetComponent<SessionViewController> ();
		controller.sessionList = list.data;
		cvsSessions.gameObject.SetActive (true);

	}


	// Update is called once per frame
	void Update () {
	
	}
}
