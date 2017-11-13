package a16.yarfs.server.exception;

/**
 * This is the "template" exception for the Yarfs Server project.
 * Created by Rodrigo Rato on 11/13/17.
 */
public class YarfsServerException extends RuntimeException {

    private final String defaultFault = "¯\\_(ツ)_/¯";

    private String fault;

    /**
     * Just sets this exception's fault to the default one
     */
    public YarfsServerException() {
        this.fault = this.defaultFault;
    }

    /**
     * Sets the fault to the one provided
     * @param fault the fault to be stored on the exception
     */
    public YarfsServerException(String fault) {
        this.fault = fault;
    }

    /**
     * Stores the fault and the current Stack of Exceptions
     * @param fault the fault to be stored on the exception
     * @param t a @see Throwable to store the stack of exceptions so far
     */
    public YarfsServerException(String fault, Throwable t) {
        super(t);
        this.fault = fault;
    }

    /**
     * Gets the fault associated with this exception
     */
    public String getFault() {
        return this.fault;
    }

}
