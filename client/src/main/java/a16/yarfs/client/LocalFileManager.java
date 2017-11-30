/**
 * Created by nuno at 29/11/17
 */
package a16.yarfs.client;

import java.io.*;
import java.nio.file.*;

/**
 *  Class LocalFileManager
 *  retrives and writes files from/to the local machine
 */
public class LocalFileManager {

    /**
     * read a file from the local machine
     * @param fileName the path of the file to read
     * @return the WHOLE contents of the files
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static byte[] getFileContents(String fileName) throws FileNotFoundException, IOException {
        Path path = Paths.get(fileName);
        return Files.readAllBytes(path);
    }

    /**
     * write a file to the local machine if it does not exist
     * @param fileName the path of the file to write
     * @param content the content to write
     * @throws FileAlreadyExistsException
     */
    public static void putFileContents(String fileName, byte[] content) throws FileAlreadyExistsException {
        Path path = Paths.get(fileName);
        try {
            Files.write(path, content, StandardOpenOption.CREATE_NEW); // don't override existing files
        } catch (FileAlreadyExistsException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
