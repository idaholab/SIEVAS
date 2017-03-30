/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * Class to handle http related requests
 * @author monejh
 */
public class RestController
{
    private HttpClient client = null;
    private static RestController controller;
    private HttpContext context = HttpClientContext.create();
    
    private static final String CONNECTION_HEADER = "Connection";
    private static final String CONNECTION_HEADER_VALUE = "close";
    private static final String LOCATION_HEADER = "Location";
    private static final String LOGIN_URL = "/login";
    private static final String CSS_URL = "/styles/Styles.css";
    private static final String LOGIN_FXML = "/fxml/Login.fxml";
    private static final String LOAD_CONTENT_FAILURE_MSG = "Failure loading content";
    private static final String LOGIN_WINDOW_TITLE = "SIEVAS Login";
    private static final String SSL_INSTANCE = "SSL";
    private static final String[] SSL_PROTOCOLS = { "TLSv1", "SSLv3" };
    private static final String HTTP_PROTOCOL_PREFIX = "http";
    private static final String HTTPS_PROTOCOL_PREFIX = "https";
    
    private RestController()
    {
        try
        {
            client = httpClientTrustingAllSSLCerts();
        }
        catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        catch (KeyManagementException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static RestController getInstance()
    {
        if (controller == null)
            controller = new RestController();
        
        return controller;
    }
    
    public HttpClient getClient()
    {
        return getClientTrustingAllSSLCerts();
    }
    
    
    public HttpResponse ProcessRequest(HttpRequestBase request)
            throws IOException
    {
        boolean processed = false;
        HttpResponse response = null;
        do
        {
            request.setHeader(CONNECTION_HEADER,CONNECTION_HEADER_VALUE);
            response = client.execute(request, context);
            if (CheckForLoginNeeded(request, response))
            {
                //handle login and retry
                try
                {
                    EntityUtils.consume(response.getEntity());
                }
                catch (IOException ex)
                {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                HandleLogin();

            }
            else
                processed = true;
        }
        while(!processed);
        return response;
    }
    
    
    private String getResponseURL(HttpRequestBase request, HttpResponse response)
    {
        URI finalUrl = request.getURI();
        RedirectLocations locations = (RedirectLocations) context.getAttribute(HttpClientContext.REDIRECT_LOCATIONS);
        if ((locations != null) && (locations.size()>0))
            finalUrl = locations.getAll().get(locations.getAll().size() - 1);
        
        return finalUrl.toString();
    }
    
    private boolean CheckForLoginNeeded(HttpRequestBase request, HttpResponse response)
    {
        Header[] headers = response.getHeaders(LOCATION_HEADER);
        boolean loginNeeded = false;
        if (headers.length>0)
        {
            for(Header header: headers)
            {
                if (header.getValue().endsWith(LOGIN_URL))
                    loginNeeded = true;
            }
        }
        if (loginNeeded)
            return true;
        else
        {
            String url = getResponseURL(request, response);
            return url.endsWith(LOGIN_URL);
        }
    }
    
    
    private void HandleLogin()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(LOGIN_FXML));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,LOAD_CONTENT_FAILURE_MSG, e);
            return;
        }
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(CSS_URL);
        
        Stage stage = new Stage();
        stage.setTitle(LOGIN_WINDOW_TITLE);
        stage.setScene(scene);
        LoginController controller2 = loader.getController();
        controller2.setRunning(true);
        
        stage.showAndWait();
    }
    
    private HttpClient getClientTrustingAllSSLCerts()
    {
        try
        {
            if (client == null)
                client = httpClientTrustingAllSSLCerts();
        }
        catch (NoSuchAlgorithmException | KeyManagementException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return client;
    }
    
    
    
    
    /**
     * Create a client that trusts self signed certs
     * @return HttpClient
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException 
     */
    private static HttpClient httpClientTrustingAllSSLCerts()
            throws NoSuchAlgorithmException, KeyManagementException
    {
        //HttpClient httpclient = HttpClientBuilder.create().build();

        /*SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, getTrustingManager(), new java.security.SecureRandom());

        SSLSocketFactory socketFactory = new SSLSocketFactory(sc);
        X509HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        socketFactory.setHostnameVerifier(hostnameVerifier);
        Scheme sch = new Scheme("https",  socketFactory, 8443);
        Scheme sch2 = new Scheme("https",  socketFactory, 443);
        httpclient.getConnectionManager().getSchemeRegistry().register(sch);
        httpclient.getConnectionManager().getSchemeRegistry().register(sch2);
        return httpclient;*/
        
        
        
        
        SSLContext sslcontext = SSLContext.getInstance(SSL_INSTANCE);
        sslcontext.init(new KeyManager[0], getTrustingManager(), new SecureRandom());
        SSLContext.setDefault(sslcontext);
        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslcontext, SSL_PROTOCOLS, null, new NoopHostnameVerifier());

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(HTTP_PROTOCOL_PREFIX, PlainConnectionSocketFactory.INSTANCE)
                .register(HTTPS_PROTOCOL_PREFIX, sslConnectionSocketFactory)
                .build();
        //PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        HttpClient httpClient = HttpClients.custom()
            //.setDefaultCredentialsProvider(credsProvider)
            //.setDefaultCookieStore(cookieStore)
            .setSSLSocketFactory(sslConnectionSocketFactory)
            //.setConnectionManager(cm)
            .build();
        
        return httpClient;
        

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
