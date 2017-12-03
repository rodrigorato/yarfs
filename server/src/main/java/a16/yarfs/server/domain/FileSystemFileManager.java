package a16.yarfs.server.domain;

import a16.yarfs.server.ServerConstants;
import a16.yarfs.server.domain.exceptions.FileAccessException;
import a16.yarfs.server.exception.AbstractYarfsRuntimeException;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * This is a possible FileManager implementation that
 * stores/retrieves files to/from the hard drive.
 * (@see a16.yarfs.server.domain.FileManager)
 * <p>
 * Created by Rodrigo Rato on 11/13/17.
 */
public class FileSystemFileManager implements FileManager {
    private static Logger logger = Logger.getLogger(FileSystemFileManager.class);

    /**
     * The idea here is to serialize the File objects
     * to disk and retrieve them later.
     *
     * @param file is the file to be stored
     */
    @Override
    public void writeFile(File file) {
        try {
            // Getting started on the "onion" layered thing
            // we need to store files. Ew.


            logger.debug("Writing file contents");
            writeFileContents(String.valueOf(file.getId()), file.getContent());
            logger.debug("Writing file metadata");
            writeFileMetadata(String.valueOf(file.getId()), file.getFileMetadata());


        } catch (FileNotFoundException ex) {
            logger.warn("The file either exists as a directory," +
                    " or it cannot be created or opened for some reason.", ex);
            System.exit(-1);

        } catch (IOException ex) {
            logger.warn("Some kind of I/O error occurred while trying to write to disk." + ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    /**
     * Retrieves the File object from disk by
     * "un-serializing" it and creating it again.
     *
     * @param fileId is an unique ID that represents the file.
     * @return the File that was once stored.
     */
    @Override
    public File readFile(long fileId) {


        try {
            logger.debug("Writing file with keys");
            return new ConcreteFile(readFileContents(String.valueOf(fileId)), readFileMetadata(String.valueOf(fileId)));


        } catch (ClassNotFoundException ex) {
            logger.warn("Couldn't un-serialize an object because it's" +
                    " class couldn't be found. Check those paths again!", ex);
            System.exit(-1);

        } catch (FileNotFoundException ex) {
            logger.warn("The file either exists as a directory," +
                    " or it cannot be read or opened for some reason.", ex);

        } catch (IOException ex) {
            logger.warn("Some kind of I/O error occurred while trying to read from disk.", ex);
            throw new FileAccessException("Couldn't read from '" + fileId + "'."); // FIXME wrong message
        }

        throw new AbstractYarfsRuntimeException(){  /// This should NEVER happen

        };
    }

    /**
     * Writes a file metadata to disk.
     * @param filename name of the file.
     * @param metadata metadata to write.
     * @throws IOException whenever an IO error happens.
     */
    @Override
    public void writeFileMetadata(String filename, FileMetadata metadata) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ServerConstants.FileSystem.buildFileStoragePath(
                ServerConstants.FileSystem.FILE_METADATA_PREFIX + filename +
                ServerConstants.FileSystem.FILE_METADATA_SUFFIX)));
        oos.writeObject(metadata);
        oos.close();

    }

    /**
     * Writes a file's contents to disk.
     * @param filename name of the file.
     * @param contents contents to write.
     * @throws IOException whenever an IO error occurs.
     */
    @Override
    public void writeFileContents(String filename, byte[] contents) throws IOException {
        String pathToStoreFile = ServerConstants.
                FileSystem.buildFileStoragePath(filename);
        logger.debug("File path is " + pathToStoreFile);
        java.io.File outputFile = new java.io.File(pathToStoreFile);
        outputFile.getParentFile().mkdirs();  // FIXME take care of return
        Files.write(Paths.get(pathToStoreFile), contents, StandardOpenOption.CREATE);

        logger.info("File '" + filename + "' has been stored under '" + pathToStoreFile + "'.");

    }

    /**
     * Reads a file metadata from disk.
     * @param filename name of the file
     * @return Metadata of the file in the form of domain class FileMetadata.
     * @throws IOException whenever an IO error occurs.
     * @throws ClassNotFoundException whenever the metadata file gets corrupted.
     */
    @Override
    public FileMetadata readFileMetadata(String filename) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ServerConstants.FileSystem.buildFileStoragePath(
                ServerConstants.FileSystem.FILE_METADATA_PREFIX + filename +
                ServerConstants.FileSystem.FILE_METADATA_SUFFIX)));
        FileMetadata fm = (FileMetadata) ois.readObject();
        ois.close();
        return fm;
    }

    /**
     * Reads the file contents from disk.
     * @param filename name of the file.
     * @return byte array with the file contents
     * @throws IOException whenever an IO error occurs.
     */
    @Override
    public byte[] readFileContents(String filename) throws IOException {
        String pathToFile = ServerConstants.
                FileSystem.buildFileStoragePath(filename);
        Path path = Paths.get(pathToFile);

        return Files.readAllBytes(path);
    }

}
