/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.client.service.exception;

/**
 *  Class ServiceResultException
 *  thrown when the result method of the service fails to get a valid result
 */
public class ServiceResultException extends ServiceException {
    public ServiceResultException(String msg) {
        super(msg);
    }

    public ServiceResultException(int code, String msg) {
        super(code + ": " + msg);
    }
}
