/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.live.dvrcontrol;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author monejh
 */
public class RestController
{
    private DefaultHttpClient client = null;
    
    public DefaultHttpClient getClientTrustingAllSSLCerts()
    {
        try
        {
            if (client == null)
                client = httpClientTrustingAllSSLCerts();
        }
        catch (NoSuchAlgorithmException | KeyManagementException ex)
        {
            Logger.getLogger(RestController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return client;
    }
    
    
    
    
    /**
     * Create a client that trusts self signed certs
     * @return HttpClient
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException 
     */
    public static DefaultHttpClient httpClientTrustingAllSSLCerts()
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
    public static TrustManager[] getTrustingManager() {
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
