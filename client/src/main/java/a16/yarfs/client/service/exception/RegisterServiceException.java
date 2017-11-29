package a16.yarfs.client.service.exception;

/**
 * This class represents Register Service's error
 * it should be thrown in the service and caught later to show the error to the user/shell
 */
public class RegisterServiceException extends RuntimeException {
    public RegisterServiceException(String msg) {
        super(msg);
    }
}
