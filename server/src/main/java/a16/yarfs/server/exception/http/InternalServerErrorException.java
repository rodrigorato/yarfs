/**
 * Created by jorge at 15/11/17
 */
package a16.yarfs.server.exception.http;

/**
 *  Class InternalServerErrorException
 *  jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class InternalServerErrorException extends  HttpException{
    private final int code = 500;
    private final String message = "Internal Server Error";

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
