package com.onehilltech.selfsigned.android.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.onehilltech.selfsigned.android.AndroidSelfSigned;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;


/**
 * Volley implementation support for self trust.
 */
public final class VolleySelfSigned
{
  public static final String DEFAULT_CA_TYPE = "X.509";
  public static final String DEFAULT_SSL_CONTEXT_TYPE = "TLS";

  private VolleySelfSigned ()
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
    SSLContext sslContext = AndroidSelfSigned.newSSLContext (context, type, assetFile);
    HurlStack hurlStack = new HurlStack (null, sslContext.getSocketFactory ());
    return Volley.newRequestQueue (context, hurlStack);
  }
}
