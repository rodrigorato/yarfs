/**
 * Created by jorge at 15/11/17
 */
package a16.yarfs.server.exception.http;

/**
 *  Class BadRequestException
 *  jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class BadRequestException extends HttpException{
    private final int code = 400;
    private final String message = "Bad request";


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
