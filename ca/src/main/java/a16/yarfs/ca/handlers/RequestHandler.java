package a16.yarfs.ca.handlers;

import java.security.KeyPair;

import a16.yarfs.ca.KeyManager;
import org.apache.log4j.Logger;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class RequestHandler extends AbstractTcpHandler {
    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    private KeyPair kp;
    private KeyManager km;

    public RequestHandler(KeyPair kp, KeyManager km) {
        this.km = km;
        this.kp = kp;
    }

    public void handle() {
        logger.debug("RequestHandler loaded with PubKey: " + AbstractTcpHandler.getSha256InBase64(kp.getPublic().getEncoded()) +
                " and PrivateKey: " + AbstractTcpHandler.getSha256InBase64(kp.getPrivate().getEncoded()) + ".");
    }
}
