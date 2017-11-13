package a16.yarfs.server.domain;

/**
 * This interface defines what operations a FileManager should have.
 *
 * A FileManager is an abstraction (something
 * along the lines of a data-mapper) that allows
 * us to store/retrieve values on a disk/databases/whatever.
 */
public interface FileManager {
    /**
     * This is the method that should be called
     * to store a File somewhere.
     *
     * A possible implementation would be to
     * serialize this file.
     * @param file is the file to be stored
     */
    void writeFile(File file);

    /**
     * This is the method that should be called
     * to retrieve a file that is in storage.
     *
     * A possible implementation would be to
     * un-serialize a file from the disk.
     * @param fileId is an unique ID that represents the file.
     * @return the File that was once stored.
     */
    File readFile(long fileId);
}

