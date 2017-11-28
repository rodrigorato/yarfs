/**
 * Created by jorge at 15/11/17
 */
package a16.yarfs.server.exception.http;

/**
 * Class HttpException
 * jorge is an IDIOT because it hasn't made documentation for this class.
 */
public abstract class HttpException extends RuntimeException {

    public abstract int getCode();

    @Override
    public abstract String getMessage();
}
