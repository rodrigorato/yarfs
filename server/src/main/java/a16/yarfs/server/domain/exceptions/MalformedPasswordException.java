/**
 * Created by nuno at 18/11/17
 */
package a16.yarfs.server.domain.exceptions;


/**
 * Class MalformedPasswordException is used when a password isn't allowed
 */
public class MalformedPasswordException extends AbstractYarfsDomainException {
    public MalformedPasswordException(String msg) {
        super(msg);
    }
}
