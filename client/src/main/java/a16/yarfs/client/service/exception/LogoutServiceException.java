/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service.exception;

/**
 * Class LogoutServiceException
 *       Exception for the LogoutService class. The most likely thing to happen is that it will
 *       only be thrown there.
 */
public class LogoutServiceException extends  RuntimeException{
    public LogoutServiceException(String msg) {
        super(msg);
    }
}
