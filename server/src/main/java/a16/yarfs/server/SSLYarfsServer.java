/**
 * Created by nuno at 02/12/17
 */
package a16.yarfs.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Class SSLYarfsServer
 *  implements a YarfsServer using SSL
 */
public class SSLYarfsServer extends YarfsServer {
    private static final Logger logger = Logger.getLogger(SSLYarfsServer.class);


    public SSLYarfsServer(InetSocketAddress address) throws IOException {
        super(address);
    }

    protected void initSSLContext(SSLContext sslContext) {
        try {
            // init keystore
            char[] password = ServerConstants.SERVER_KEYSTORE_PASSWORD.toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream file = new FileInputStream(ServerConstants.SERVER_KEYSTORE);
            ks.load(file, password);

            Certificate cert = ks.getCertificate(ServerConstants.SERVER_CERT_ALIAS);
            //logger.info("certificate:" + cert);

            // init key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // init trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // setup the HTTPS context
            SecureRandom random = new SecureRandom();
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), random);
        } catch (KeyStoreException | KeyManagementException | UnrecoverableKeyException | CertificateException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    @Override
    protected HttpServer createHttpServer(InetSocketAddress address, int backlog) throws IOException {
        HttpsServer server = HttpsServer.create(address, backlog);
        String protocol = ServerConstants.SSL_PROTOCOL;
        try {
            SSLContext sslContext = SSLContext.getInstance(protocol);
            initSSLContext(sslContext);
            server.setHttpsConfigurator(getHttpsConfigurator(sslContext));
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            System.exit(1);
        }
        return server;
    }

    private HttpsConfigurator getHttpsConfigurator(SSLContext sslContext) {
        return new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                logger.debug("configure HTTPS conn for " + params.getClientAddress());
                try {
                    SSLContext context = SSLContext.getDefault();
                    SSLEngine engine = context.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    SSLParameters defaultParams = context.getDefaultSSLParameters();
                    params.setSSLParameters(defaultParams);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    System.exit(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    /**
     * get a list of available security providers. Some of these can be used in ServerConstants.SSL_PROTOCOL
     */
    public static List<String> getSecurityPoviders() {
        Provider[] providers = Security.getProviders();
        List<String> list = new ArrayList<>();
        for(Provider p : providers) {
            list.add(p.getInfo());
        }
        return list;
    }
}
