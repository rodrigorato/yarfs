/**
 * Created by jorge at 18/11/17
 */
package a16.yarfs.server.exception.api;

import a16.yarfs.server.exception.http.HttpException;

/**
 * Class LoginException
 * jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class LoginException extends HttpException {  //  FIXME wrong inheritance
    private int code = 600;
    private String message = "Wrong password or username. Who can guess?";


    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
