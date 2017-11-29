/**
 * Created by jorge at 15/11/17
 */
package a16.yarfs.server.exception.http;

/**
 *  Class MethodNotAllowed
 *  jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class MethodNotAllowedException extends HttpException{
    private final int code = 405;
    private final String message = "Method not allowed";

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
