/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.live.livetestclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.http.Header;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author monejh
 * Simple LIVE2 Client application
 */
public class App
{
    public static String baseURL = "https://localhost:8443/";
    public static String loginURL = "login";
    public static String sessionListURL = "api/sessions/";
    public static String CLIENT_URL = "tcp://localhost:61616";
    
    /***
     * Main program for test client for LIVE2
     * @param args Takes no arguments
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws ClassNotFoundException
     * @throws JMSException
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException, ClassNotFoundException, JMSException, InterruptedException
    {
        //setup to allow self signed certs
        HttpClient client = httpClientTrustingAllSSLCerts();
        //handle login
        HttpPost request = new HttpPost(baseURL + loginURL);
        
        boolean authenticated = false;
        do
        {
            Scanner s = new Scanner(System.in);
            System.out.print("Enter username:");
            String username = s.next();
            System.out.print("Password:");
            String password  = new String(System.console().readPassword());


            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("username", username));
            nvps.add(new BasicNameValuePair("password", password));
            request.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response = client.execute(request);
            //printResponse(response);
            response.getEntity().consumeContent();

            boolean error = false;
            Header[] headers = response.getHeaders("Location");
            if (headers.length>0)
            {
                for(Header header: headers)
                {
                    if (header.getValue().endsWith("?error"))
                        error = true;
                }
            }

            if (error)
            {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE,"Login Failure");
                continue;
            }
            authenticated = true;
        }
        while(!authenticated);
        
        HttpGet getRequest = new HttpGet(baseURL + sessionListURL);
        HttpResponse response = client.execute(getRequest);
        //printResponse(response);
        evaluateSessions(response);
        
        
    }
    
    /***
     * Prints the response from HTTP client
     * @param response The response object to process
     * @throws IOException 
     */
    private static void printResponse(HttpResponse response) throws IOException
    {
        System.out.println("STATUS:" + response.getStatusLine());
//        for(Header header: response.getAllHeaders())
//        {
//            System.out.println("HEADER => NAME:" + header.getName() + " --- " + header.getValue());
//        }
        BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
        String line;
        while ((line = rd.readLine()) != null)
        {
            System.out.println(line);
        }
        
    }
    
    /***
     * Evaluate the response of session objects
     * @param response The HTTP response object
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws JMSException
     * @throws InterruptedException 
     */
    private static void evaluateSessions(HttpResponse response) throws IOException, ClassNotFoundException, JMSException, InterruptedException
    {
        ObjectMapper objMapper = new ObjectMapper();
        //read result as a JsonListResult<LIVESession>
        //NOTE: users and groups are missing in the local object since it is not needed
        JsonListResult<LIVESession> result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<LIVESession> >(){});
        //print out the menu of sessions
        System.out.println("Select a session below:\n");
        for(LIVESession session: result.getData())
        {
            System.out.println(session);
        }
        boolean found = false;
        //get user input
        do
        {
            System.out.print("Select Session by ID:");
            Scanner in = new Scanner(System.in);
            int id = in.nextInt();
            for(LIVESession session: result.getData())
            {
                if (id == session.getId())
                {
                    //if found exit and connect to server
                    found = true;
                    connectToLIVE(session);
                }
            }
        }
        while(!found);
    }
    
    
    /***
     * Connects to the ActiveMQ streams for display
     * @param session The LIVESession object to connect to.
     * @throws JMSException
     * @throws InterruptedException 
     */
    private static void connectToLIVE(LIVESession session) throws JMSException, InterruptedException
    {
        Logger.getLogger(App.class.getName()).log(Level.INFO,"Connecting");
        
        //connect to AMQ first
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(CLIENT_URL);
        Connection connection = factory.createConnection();
        Session amqSession = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
        
        //create data stream topic
        Destination dataDestination = amqSession.createTopic(session.getDataStreamName());
        MessageConsumer dataConsumer = amqSession.createConsumer(dataDestination);
        dataConsumer.setMessageListener(new MessageListener()
        {
            @Override
            public void onMessage(Message msg)
            {
                Logger.getLogger(App.class.getName()).log(Level.INFO,"Data Message:" + msg);
            }
        });
        
        //create control stream topic
        Destination controlDestination = amqSession.createTopic(session.getControlStreamName());
        MessageConsumer controlConsumer = amqSession.createConsumer(controlDestination);
        controlConsumer.setMessageListener(new MessageListener()
        {
            @Override
            public void onMessage(Message msg)
            {
                Logger.getLogger(App.class.getName()).log(Level.INFO,"Control Message:" + msg);
            }
        });
        //start!
        connection.start();
        Logger.getLogger(App.class.getName()).log(Level.INFO,"Done");
        //wait for a long time.
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.shutdown();
    }
    
    /**
     * Create a client that trusts self signed certs
     * @return HttpClient
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException 
     */
    private static DefaultHttpClient httpClientTrustingAllSSLCerts()
            throws NoSuchAlgorithmException, KeyManagementException
    {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, getTrustingManager(), new java.security.SecureRandom());

        SSLSocketFactory socketFactory = new SSLSocketFactory(sc);
        X509HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        socketFactory.setHostnameVerifier(hostnameVerifier);
        Scheme sch = new Scheme("https",  socketFactory, 8443);
        httpclient.getConnectionManager().getSchemeRegistry().register(sch);
        return httpclient;
    }

    /***
     * Gets the trust manager for self signed certs
     * @return array of TrustManagers
     */
    private static TrustManager[] getTrustingManager() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                // Do nothing
            }

        } };
        return trustAllCerts;
    }
    
}
