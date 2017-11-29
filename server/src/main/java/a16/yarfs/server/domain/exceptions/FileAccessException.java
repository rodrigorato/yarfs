package a16.yarfs.server.domain.exceptions;


/**
 * Thrown when a disk/storage access fails
 */
public class FileAccessException extends AbstractYarfsDomainException {
    public FileAccessException(String s) {
        super(s);
    }
}
