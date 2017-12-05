/**
 * Created by nuno at 05/12/17
 */
package a16.yarfs.client.service.exception;

/**
 *  Class ServiceExecutionException
 *  thrown when something bad happens while a service is being executed or prepared for execution
 */
public class ServiceExecutionException extends ServiceException {
    public ServiceExecutionException(String msg) {
        super(msg);
    }
}
