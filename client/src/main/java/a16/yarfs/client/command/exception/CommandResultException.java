/**
 * Created by nuno at 28/11/17
 */
package a16.yarfs.client.command.exception;

/**
 *  Class CommandResultException
 *  thrown when the result method of the command fails to get a valid result
 */
public class CommandResultException extends CommandException {
    public CommandResultException(String msg) {
        super(msg);
    }
}
