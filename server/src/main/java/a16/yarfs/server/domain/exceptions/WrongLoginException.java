/**
 * Created by nuno at 18/11/17
 */
package a16.yarfs.server.domain.exceptions;

/**
 * Class WrongLoginException
 * means that the given password did not match the correct password
 */
public class WrongLoginException extends AbstractYarfsDomainException {
    public WrongLoginException(String msg) {
        super(msg);
    }
}
