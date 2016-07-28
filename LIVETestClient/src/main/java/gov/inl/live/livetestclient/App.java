/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.live.livetestclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
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
import org.apache.http.params.HttpParams;

/**
 *
 * @author monejh
 */
public class App
{
    public static String baseURL = "https://localhost:8443/";
    public static String loginURL = "login";
    public static String sessionListURL = "api/sessions/";
    
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException, ClassNotFoundException, JMSException, InterruptedException
    {
        HttpClient client = httpClientTrustingAllSSLCerts();
        
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
    
    private static void evaluateSessions(HttpResponse response) throws IOException, ClassNotFoundException, JMSException, InterruptedException
    {
        ObjectMapper objMapper = new ObjectMapper();
        JsonListResult<LIVESession> result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<LIVESession> >(){});
        for(LIVESession session: result.getData())
        {
            System.out.println("SESSION:" + session);
        }
        boolean found = false;
        do
        {
            System.out.print("Select Session by ID:");
            Scanner in = new Scanner(System.in);
            int id = in.nextInt();
            for(LIVESession session: result.getData())
            {
                if (id == session.getId())
                {
                    found = true;
                    connectToLIVE(session);
                }
            }
        }
        while(!found);
    }
    
    private static void connectToLIVE(LIVESession session) throws JMSException, InterruptedException
    {
        Logger.getLogger(App.class.getName()).log(Level.INFO,"Connecting");
        String CLIENT_URL = "tcp://localhost:61616";
        
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(CLIENT_URL);
        Connection connection = factory.createConnection();
        Session amqSession = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
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
        connection.start();
        Logger.getLogger(App.class.getName()).log(Level.INFO,"Done");
        Thread.sleep(100000000L);
    }
    
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
