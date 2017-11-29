/**
 * Created by jorge at 14/11/17
 */
package a16.yarfs.server.domain.exceptions;


/**
 * Class DuplicatedUsernameException
 * Thrown when registering an existing user
 */
public class DuplicatedUsernameException extends AbstractYarfsDomainException {
    public DuplicatedUsernameException(String fault) {
        super(fault);
    }
}
