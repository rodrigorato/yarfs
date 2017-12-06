package a16.yarfs.ca.handlers;

import java.security.KeyPair;
import org.apache.log4j.Logger;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class RequestHandler extends AbstractTcpHandler {
    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    private KeyPair kp;

    public RequestHandler(KeyPair kp) {
        this.kp = kp;
    }

    public void handle() {
        logger.debug("RequestHandler loaded with PubKey: " + kp.getPublic().hashCode() +
                " and PrivateKey: " + kp.getPrivate().hashCode() + ".");
    }
}
