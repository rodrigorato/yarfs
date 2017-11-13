package a16.yarfs.server.domain;

import a16.yarfs.server.exception.FileAccessException;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;

/**
 * This is a possible FileManager implementation that
 * stores/retrieves files to/from the hard drive.
 * (@see a16.yarfs.server.domain.FileManager)
 *
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
    public void writeFile(File file) {
        try {
            // Getting started on the "onion" layered thing
            // we need to store files. Ew.

            String pathToStoreFile = ServerConstants.
                    FileSystem.buildFileStoragePath(Long.toString(file.getId()));

            FileOutputStream fos = new FileOutputStream(pathToStoreFile);

            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(file);

            oos.close();
            fos.close();

            logger.info("File '" + file.getName() + "' has been stored under '" + pathToStoreFile + "'.");

        } catch(FileNotFoundException ex) {
            logger.warn("The file either exists as a directory," +
                    " or it cannot be created or opened for some reason.", ex);
            System.exit(-1);

        } catch(IOException ex) {
            logger.warn("Some kind of I/O error occurred while trying to write to disk.", ex);
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
    public File readFile(long fileId) {
        String pathToFile = ServerConstants.
                FileSystem.buildFileStoragePath(Long.toString(fileId));

        File retrievedFile = null;

        try {
            FileInputStream fis = new FileInputStream(pathToFile);
            ObjectInputStream ois = new ObjectInputStream(fis);

            retrievedFile = (File) ois.readObject();

            ois.close();
            fis.close();

            logger.info("File '" + retrievedFile.getName() + "' has been retrieved from '" + pathToFile + "'.");

        } catch(ClassNotFoundException ex) {
            logger.warn("Couldn't un-serialize an object because it's" +
                    " class couldn't be found. Check those paths again!", ex);
            System.exit(-1);

        } catch(FileNotFoundException ex) {
            logger.warn("The file either exists as a directory," +
                    " or it cannot be read or opened for some reason.", ex);

        } catch(IOException ex) {
            logger.warn("Some kind of I/O error occurred while trying to read from disk.", ex);
        }

        if(retrievedFile != null)
            return retrievedFile;
        else
            throw new FileAccessException("Couldn't read from '" + pathToFile + "'.");
    }

}
