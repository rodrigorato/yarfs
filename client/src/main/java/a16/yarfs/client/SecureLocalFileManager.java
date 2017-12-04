/**
 * Created by jorge at 03/12/17
 */
package a16.yarfs.client;

import a16.yarfs.client.service.dto.FileDto;
import a16.yarfs.client.service.dto.FileMetadata;
import org.apache.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 *  Class SecureLocalFileManager
 *          Class which does the same as LocalFileManager but in a secure fashion according to the implementation
 *          standards.
 *          @see LocalFileManager
 */
public class SecureLocalFileManager extends LocalFileManager{

    private static Logger logger = Logger.getLogger(SecureLocalFileManager.class);
    private KeyManager keyManager = KeyManager.getManager();
    private static SecureLocalFileManager manager = null;

    @Override
    public void putFileContents(String fileName, byte[] content) throws IOException {
        logger.debug("Deciphering...");
        byte[] key = getFileMetadata(fileName).getKey();
        logger.debug("Deciphered key is "+ Arrays.toString(key));
        SecretKeySpec keyspec = new SecretKeySpec(key, ClientConstants.KeyStandards.SYMETRIC_ALGORITHM);
        try {
            Cipher c = Cipher.getInstance(ClientConstants.KeyStandards.SYMETRIC_ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, keyspec);
            byte[] plainContents = c.doFinal(content);
            super.putFileContents(fileName, plainContents);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            logger.warn("Invalid padding method.", e);
        } catch (InvalidKeyException e) {
            logger.warn("Invalid key.", e);
        } catch (BadPaddingException e) {
            logger.warn("Padding error.", e);
        } catch (IllegalBlockSizeException e) {
            logger.warn("Block size error.", e);
        }
    }

    @Override
    public FileDto getFile(String fileName) throws IOException{
        return new FileDto(getFileContents(fileName), getFileMetadata(fileName));
    }

    @Override
    public FileMetadata getFileMetadata(String fileName) throws IOException {
        FileMetadata metadata = super.getFileMetadata(fileName);
        return new FileMetadata(metadata.getId(), metadata.getName(), metadata.getOwner(), metadata.getSignature(),
                keyManager.decipher(metadata.getKey()));
    }

    @Override
    public byte[] getFileContents(String fileName) throws IOException {
        byte[] ciphered = super.getFileContents(fileName);
        byte[] key = keyManager.decipher(super.getFileMetadata(fileName).getKey());
        SecretKeySpec keyspec = new SecretKeySpec(key, ClientConstants.KeyStandards.SYMETRIC_ALGORITHM);
        try {
            Cipher c = Cipher.getInstance(ClientConstants.KeyStandards.SYMETRIC_STANDARD);
            c.init(Cipher.ENCRYPT_MODE, keyspec);
            return c.doFinal(ciphered);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            logger.warn("Invalid padding method.", e);
        } catch (InvalidKeyException e) {
            logger.warn("Invalid key.", e);
        } catch (BadPaddingException e) {
            logger.warn("Padding error.", e);
        } catch (IllegalBlockSizeException e) {
            logger.warn("Block size error.", e);
        }
        return null;
    }

    @Override
    public void putFileMetaData(String filename, FileMetadata fileMetadata) throws IOException {
        FileMetadata secureMetadata = new FileMetadata(fileMetadata.getId(), fileMetadata.getName(),
                fileMetadata.getOwner(), fileMetadata.getSignature(), keyManager.cipher(fileMetadata.getKey()));
        super.putFileMetaData(filename, secureMetadata);

    }

    @Override
    public void putFile(FileDto file) throws IOException {
        putFileMetaData(file.getName(), file.getFileMetadata());
        putFileContents(file.getName(), file.getContents());
    }


    public static SecureLocalFileManager getManager(){
        if( manager == null ){
            manager = new SecureLocalFileManager();
        }
        return manager;
    }


}
