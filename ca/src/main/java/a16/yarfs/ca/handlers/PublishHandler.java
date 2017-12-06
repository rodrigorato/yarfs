package a16.yarfs.ca.handlers;

import java.security.KeyPair;
import java.security.PublicKey;
import org.apache.log4j.Logger;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class PublishHandler extends AbstractTcpHandler {
    private static final Logger logger = Logger.getLogger(PublishHandler.class);

    private KeyPair kp;

    public PublishHandler(KeyPair kp) {
        this.kp = kp;
    }

    public void handle() {
        logger.debug("PublishHandler loaded with PubKey: " + kp.getPublic().hashCode() +
                     " and PrivateKey: " + kp.getPrivate().hashCode() + ".");
    }
}
