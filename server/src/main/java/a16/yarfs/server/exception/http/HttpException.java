/**
 * Created by jorge at 15/11/17
 */
package a16.yarfs.server.exception.http;

import a16.yarfs.server.exception.YarfsServerException;

/**
 *  Class HttpException
 *  jorge is an IDIOT because it hasn't made documentation for this class.
 */
public abstract class HttpException extends YarfsServerException{

    public abstract int getCode();
    public abstract String getMessage();
}
