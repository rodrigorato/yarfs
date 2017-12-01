package a16.yarfs.server.domain;

import java.io.IOException;

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
     * @see FileManager#writeFileContents(String, byte[])
     * @see FileManager#writeFileMetadata(String, FileMetadata)
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
     * @see FileManager#readFileMetadata(String)
     * @see FileManager#readFileContents(String)
     */
    File readFile(long fileId);

    /**
     * This is the method that should be called
     * to write a file metadata to storage.
     * @param filename name of the file.
     * @param metadata metadata to write.
     * @throws IOException whenever there is an invalid IO operation (I know it doesn't help...)
     */
    void writeFileMetadata(String filename, FileMetadata metadata) throws IOException;

    /**
     * This is the method that should be called
     * to write a file's contents to storage.
     * @param filename name of the file.
     * @param contents contents to write.
     * @throws IOException whenever there is an invalid IO operation (I know it doesn't help...)
     */
    void writeFileContents(String filename, byte[] contents) throws IOException;

    /**
     * This is a method that should be called
     * to read a file's metadata from persistent storage.
     * @param filename name of the file
     * @return an instance of FileMetadata containing metadata of the file.
     * @throws IOException whenever there is an invalid IO operation.
     * @throws ClassNotFoundException whenever there is a format error in the file.
     * @see FileMetadata
     */
    FileMetadata readFileMetadata(String filename) throws IOException, ClassNotFoundException;

    /**
     * This is the method that should be called
     * to read a file's contents from storage.
     * @param filename name of the file.
     * @return contents of the file.
     * @throws IOException whenever there is an invalid IO operation.
     */
    byte[] readFileContents(String filename) throws IOException;
}

