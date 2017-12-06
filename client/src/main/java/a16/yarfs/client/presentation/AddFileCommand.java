/**
 * Created by nuno at 29/11/17
 */
package a16.yarfs.client.presentation;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.KeyManager;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.service.dto.FileDto;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceExecutionException;
import a16.yarfs.client.service.exception.ServiceResultException;
import a16.yarfs.client.service.file.AddFileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 *  Class AddFileCommand
 *  uploads a (new) file to the yarfs server
 */
public class AddFileCommand extends Command {

    public AddFileCommand(YarfsShell sh, String name) {
        super(sh, name, "add a new file from your computer to " + sh.getName());
    }

    @Override
    void execute(String[] args) {
        // check prerequisites
        YarfsShell shell = (YarfsShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("please login first");
            return;
        }

        // check (and parse) arguments

        if (args.length < 1 || args.length > 2) {
            shell.println(getUsage());
            return;
        }

        String localFilename = args[0];
        String remoteFilename = args.length == 1 ? localFilename : args[1];

        if(localFilename.trim().isEmpty() || remoteFilename.trim().isEmpty()) { // can this happen? who knows...
            shell.println("file names must not be empty");
            return;
        }

        // read the file from local storage
        byte[] content;
        byte[] ciphered_content = null;
        byte[] secret = KeyManager.generateKey();
        byte[] ciphered_key = KeyManager.getManager().cipher(secret);
        getLogger().debug("Plain text key is "+ Arrays.toString(secret));
        getLogger().debug("Ciphered key is "+ Arrays.toString(ciphered_key));
        try {
            content = LocalFileManager.getManager().getFileContents(localFilename);
            try {
                Cipher cipher = Cipher.getInstance(ClientConstants.KeyStandards.SYMMETRIC_STANDARD);
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, ClientConstants.KeyStandards.SYMMETRIC_ALGORITHM));
                ciphered_content = cipher.doFinal(content);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException |
                    IllegalBlockSizeException e) {
                //e.printStackTrace(); // This shouldn't ever happen
                getLogger().error("SOMETHING REALLY WEIRD HAPPENING!", e);
            }
            shell.println("read '" + localFilename + "' ("+ FileUtils.byteCountToDisplaySize(content.length)+")");
        } catch (FileNotFoundException e) {
            shell.println(localFilename + ": No such file");
            return;
        } catch (IOException e) {
            shell.println("error reading " + localFilename + ": " + e.getMessage());
            return;
        }

        // FIXME deal with encryption, keys, signature, etc

        byte[] signature = KeyManager.getManager().sign(DigestUtils.sha256(content));

        // send the file to the server
        try {
            AddFileService service = new AddFileService(ClientConstants.baseUrl,
                    shell.getActiveSessionid(), remoteFilename, ciphered_content, signature, ciphered_key);

            service.execute();

            // check the result
            long fileId = service.getFileId();

            // create a file with all the info we have so we can write properly to disk
            FileDto fileDto = new FileDto(fileId, localFilename, shell.getActiveUser(), content, signature,
                    ciphered_key, shell.getActiveUser());
            shell.println("added file '"+remoteFilename+"' with id " + fileId);
            LocalFileManager.getManager().putFileMetaData(fileDto.getName(), fileDto.getFileMetadata());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ServiceResultException e) {
            shell.println("could not add file: " + e.getMessage());
        } catch (AlreadyExecutedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NotExecutedException e) {
            e.printStackTrace();
        } catch (ServiceExecutionException e) {
            shell.println("error: " + e.getMessage());
        }
    }


    @Override
    public String getUsage() {
        return "Usage: "+ getName() + " <local filename> [remote filename]" + System.lineSeparator() +
                "<remote filename> defaults to <local filename>";
    }
}
