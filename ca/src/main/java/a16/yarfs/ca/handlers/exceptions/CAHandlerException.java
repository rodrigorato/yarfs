package a16.yarfs.ca.handlers.exceptions;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class CAHandlerException extends Exception {
    private String message;

    public CAHandlerException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
