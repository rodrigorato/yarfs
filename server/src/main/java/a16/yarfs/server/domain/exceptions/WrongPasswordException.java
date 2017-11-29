/**
 * Created by nuno at 18/11/17
 */
package a16.yarfs.server.domain.exceptions;

/**
 * Class WrongPasswordException
 * means that the given password did not match the correct password
 */
public class WrongPasswordException extends AbstractYarfsDomainException {
    public WrongPasswordException(String msg) {
        super(msg);
    }
}
