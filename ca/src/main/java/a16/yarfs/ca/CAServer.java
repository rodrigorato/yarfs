package a16.yarfs.ca;

import a16.yarfs.ca.handlers.PublishHandler;
import a16.yarfs.ca.handlers.RequestHandler;
import a16.yarfs.ca.handlers.exceptions.KeyStoreException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;

import org.apache.log4j.Logger;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class CAServer {
    private static final Logger logger = Logger.getLogger(CAServer.class);

    private KeyStore ks = null;
    private KeyPair kp = null;

    public CAServer() throws KeyStoreException {

        try {
            ks = importCAKeystore(CAConstants.Keys.KEYSTORE_FILE, CAConstants.Keys.KEYSTORE_PASSWORD.toCharArray());
            kp = buildCAKeyPair();

        } catch (KeyStoreException e) {
            logger.error("Error building CA's KeyPair! " + e.getMessage());
            throw e;
        }

        new PublishHandler(kp).handle();
        new RequestHandler(kp).handle();
    }

    private KeyStore importCAKeystore(String keystoreFilePath, char[] password) throws KeyStoreException {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(keystoreFilePath), password);

        } catch (GeneralSecurityException | IOException e) {
            logger.error("Error importing CA's KeyStore! " + e.getMessage());
            throw new KeyStoreException("Error importing CA's KeyStore! " + e.getMessage());
        }

        return ks;
    }

    private KeyPair buildCAKeyPair() throws KeyStoreException {
        return new KeyPair(getPublicKey(), getPrivateKey());
    }

    private PublicKey getPublicKey() throws KeyStoreException {
        if(ks == null) {
            throw new KeyStoreException("KeyStore hasn't been loaded yet!");

        } else {
            try {
                return ks.getCertificate(CAConstants.Keys.CERTIFICATE_ALIAS).getPublicKey();
            } catch (java.security.KeyStoreException e) {
                logger.error("Error retrieving CA's PublicKey from the KeyStore! " + e.getMessage());
                throw new KeyStoreException("Error retrieving CA's PublicKey from the KeyStore! " + e.getMessage());
            }
        }
    }

    private PrivateKey getPrivateKey() throws KeyStoreException {
        if(ks == null) {
            throw new KeyStoreException("KeyStore hasn't been loaded yet!");

        } else {
            try {
                return (PrivateKey) ks.getKey(CAConstants.Keys.PRIVATE_KEY_ALIAS,
                        CAConstants.Keys.PRIVATE_KEY_PASSWORD.toCharArray());

            } catch (NoSuchAlgorithmException | UnrecoverableKeyException | java.security.KeyStoreException e) {
                throw new KeyStoreException("Couldn't retrieve PrivateKey from KeyStore!");
            }
        }
    }
}
