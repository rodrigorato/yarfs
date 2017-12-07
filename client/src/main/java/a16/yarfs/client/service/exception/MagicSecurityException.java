/**
 * Created by jorge on 06/12/17
 **/
package a16.yarfs.client.service.exception;

/**
 * Class MagicSecurityException
 * jorge is an IDIOT because he didn't document this class.
 *
 **/
public class MagicSecurityException extends MagicException{

    private static final String defaultMessage = "Security breach attempt in MAGIC protocol.";

    public MagicSecurityException(){
        super(defaultMessage);
    }

    public MagicSecurityException(Throwable cause){
        super(defaultMessage, cause);
    }

    public MagicSecurityException(String message) {
        super(message);
    }

    public MagicSecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
