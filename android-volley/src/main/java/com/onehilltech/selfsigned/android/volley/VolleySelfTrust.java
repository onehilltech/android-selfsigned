package com.onehilltech.selfsigned.android.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Volley implementation support for self trust.
 */
public final class VolleySelfTrust
{
  public static final String DEFAULT_CA_TYPE = "X.509";
  public static final String DEFAULT_SSL_CONTEXT_TYPE = "TLS";

  private VolleySelfTrust ()
  {

  }

  /**
   * Create a new Volley RequestQueue.
   *
   * @param context
   * @param assetFile
   * @return
   * @throws CertificateException
   * @throws IOException
   * @throws KeyStoreException
   * @throws NoSuchAlgorithmException
   * @throws KeyManagementException
   */
  public static RequestQueue newRequestQueue (Context context, String assetFile)
      throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
  {
    return newRequestQueue (context, DEFAULT_CA_TYPE, assetFile);
  }

  /**
   * Create a new Volley RequestQueue.
   *
   * @param context
   * @param type
   * @param assetFile
   * @return
   * @throws CertificateException
   * @throws IOException
   * @throws KeyStoreException
   * @throws NoSuchAlgorithmException
   * @throws KeyManagementException
   */
  public static RequestQueue newRequestQueue (Context context, String type, String assetFile)
      throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException
  {
    CertificateFactory cf = CertificateFactory.getInstance (type);
    InputStream caInput = new BufferedInputStream (context.getAssets ().open (assetFile));

    try
    {
      Certificate ca = cf.generateCertificate (caInput);

      // Create a KeyStore containing our trusted CAs
      String keyStoreType = KeyStore.getDefaultType ();
      KeyStore keyStore = KeyStore.getInstance (keyStoreType);
      keyStore.load (null, null);
      keyStore.setCertificateEntry ("ca", ca);

      // Create a TrustManager that trusts the CAs in our KeyStore
      String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm ();
      TrustManagerFactory tmf = TrustManagerFactory.getInstance (tmfAlgorithm);
      tmf.init (keyStore);

      // Create an SSLContext that uses our TrustManager
      SSLContext sslContext = SSLContext.getInstance (DEFAULT_SSL_CONTEXT_TYPE);
      sslContext.init (null, tmf.getTrustManagers (), null);

      HurlStack hurlStack = new HurlStack (null, sslContext.getSocketFactory ());
      return Volley.newRequestQueue (context, hurlStack);
    }
    finally
    {
      caInput.close ();
    }
  }
}
