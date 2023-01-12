package com.hairysnow.play.integrity.api.android;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sugood on 2017/7/20.
 */

public class HttpSslUtils {
    private static final String TAG = "HttpSslUtils";
    public static final String HTTPS_PREFIX = "https://";
    private static final String DEFAULT_SSL_ERROR_MSG = "The current connection to server is not safe. For your account security, please change to another wireless connection before using the App.";


    public static boolean isHttpsHost(String host) {
        return !TextUtils.isEmpty(host) && host.startsWith(HTTPS_PREFIX);
    }

    public static SSLBean getSSLContextTrustManager(final Context context) {
        return getSSLContextTrustManager(context, null);
    }

    public static SSLBean getSSLContextTrustManager(final Context context, String url) {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    if (chain != null && chain.length > 0) {
                        try {
                            for (X509Certificate cer :
                                    chain) {
                                cer.checkValidity();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
            SSLBean sslBean = new SSLBean();
            sslBean.mX509TrustManager = x509TrustManager;
            sslBean.mSSLContext = sslContext;
            return sslBean;

        } catch (Exception e) {
            Log.e("HttpSslUtils",e.getMessage());
        }
        return null;
    }

    private static void closeStream(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class SSLBean {
        public SSLContext mSSLContext;
        public X509TrustManager mX509TrustManager;
        public String url;

        @Override
        public String toString() {
            return "SSLBean{" +
                    "mSSLContext=" + mSSLContext +
                    ", mX509TrustManager=" + mX509TrustManager +
                    ", url='" + url + '\'' +
                    '}';
        }
    }


    public static SSLBean getTrustContextTrustManager() {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            sslContext.init(null, new TrustManager[]{x509TrustManager}, new SecureRandom());
            SSLBean sslBean = new SSLBean();
            sslBean.mX509TrustManager = x509TrustManager;
            sslBean.mSSLContext = sslContext;
            return sslBean;

        } catch (Exception e) {
            Log.e("HttpSslUtils",e.getMessage());
        }
        return null;
    }
}
