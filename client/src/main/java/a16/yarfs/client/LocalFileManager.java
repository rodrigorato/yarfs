/**
 * Created by nuno at 29/11/17
 */
package a16.yarfs.client;

import a16.yarfs.client.service.dto.FileDto;
import a16.yarfs.client.service.dto.FileMetadata;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.*;

/**
 *  Class LocalFileManager
 *  retrives and writes files from/to the local machine
 */
public class LocalFileManager {

    private static LocalFileManager localFileManager = null;
    private static Logger logger = Logger.getLogger(LocalFileManager.class);
    private String username="";

    protected LocalFileManager(){
        //Create required folders
        new File(ClientConstants.StorageStandards.BASE_FOLDER).mkdirs();
        new File(ClientConstants.StorageStandards.FILE_FOLDER).mkdirs();
        new File(ClientConstants.StorageStandards.KEY_FOLDER).mkdirs();
    }

    public static LocalFileManager getManager(){
        if (localFileManager == null){
            localFileManager =  new LocalFileManager();
        }
        return  localFileManager;
    }


    /**
     * read a file from the local machine
     * @param fileName the path of the file to read
     * @return the WHOLE contents of the files
     * @throws FileNotFoundException
     * @throws IOException
     */
    public byte[] getFileContents(String fileName) throws IOException {
        Path path = Paths.get(ClientConstants.StorageStandards.getHomeFolder(username) + fileName);
        return Files.readAllBytes(path);
    }

    /**
     * Reads a file's metadata from disk.
     * @param fileName filename of the file to read.
     * @return instance of FileMetaData containing the metadata of the file.
     * @throws IOException whenever an IO error occurs
     */
    public FileMetadata getFileMetadata(String fileName) throws IOException {
        ObjectInputStream ois = null;
        FileMetadata fm = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(ClientConstants.StorageStandards.
                    getHomeFolder(username) +
                    ClientConstants.StorageStandards.FILE_METADATA_PREFIX + fileName +
                    ClientConstants.StorageStandards.FILE_METADATA_SUFFIX));
            fm = (FileMetadata) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException e) {
            logger.error("Something really bad happened. File format error?", e);
        }
        return fm;
    }

    /**
     * Retrieves a file from disk.
     * @param fileName filename of the file to retrieve.
     * @return instance of FileDto representing the file.
     * @throws IOException whenever an IO error occurs.
     */
    public FileDto getFile(String fileName) throws IOException{
        FileMetadata fm = getFileMetadata(fileName);
        return new FileDto(fm.getId(), fm.getName(), fm.getOwner(), getFileContents(fileName), fm.getSignature(),
                fm.getKey(), fm.getLastModifiedBy());
    }

    /**
     * write a file to the local machine if it does not exist
     * @param fileName the path of the file to write
     * @param content the content to write
     * @throws FileAlreadyExistsException
     */
    public void putFileContents(String fileName, byte[] content)
            throws FileAlreadyExistsException, IOException {
        Path path = Paths.get(ClientConstants.StorageStandards.getHomeFolder(username) + fileName);
        try {
            Files.write(path, content, StandardOpenOption.CREATE); // don't override existing files. It overrides now (remove?)
        } catch (FileAlreadyExistsException e) {
            throw e;
        }

    }

    /**
     * Writes to disk the metadata of a file.
     * @param fileName filename of the file which the metadata belongs to.
     * @param metadata metadata of the file.
     * @throws IOException whenever an IO error occurs.
     */
    public void putFileMetaData(String fileName, FileMetadata metadata) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ClientConstants.StorageStandards.
                getHomeFolder(username) +
                ClientConstants.StorageStandards.FILE_METADATA_PREFIX + fileName +
                ClientConstants.StorageStandards.FILE_METADATA_SUFFIX));
        oos.writeObject(metadata);
        oos.close();
    }

    /**
     * Writes a file to disk.
     * @param file FileDto representing the file to be written.
     * @throws IOException
     */
    public void putFile(FileDto file) throws IOException {
        putFileContents(file.getName(), file.getContents());
        putFileMetaData(file.getName(), file.getFileMetadata());
    }

    public void createHomeFolder(String username) throws IOException {
        try {
            Files.createDirectory(Paths.get(ClientConstants.StorageStandards.getHomeFolder(username)));
        }catch( FileAlreadyExistsException e) {
            logger.debug("Home folder exists.");
        }
        this.username = username;
    }

    public void selfDestruct(){
        localFileManager = null;
    }

}
