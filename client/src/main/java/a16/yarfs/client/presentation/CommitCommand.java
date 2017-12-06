/**
 * Created by jorge at 04/12/17
 */
package a16.yarfs.client.presentation;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.KeyManager;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.SecureLocalFileManager;
import a16.yarfs.client.service.dto.FileMetadata;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.ServiceExecutionException;
import a16.yarfs.client.service.file.CommitFileService;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 *  Class CommitCommand
 *  This class executes the commit command which will commit changes on a file to the server.
 */
public class CommitCommand extends Command{
    public CommitCommand(Shell sh, String name) {
        super(sh, name, "Commit a change of existing file.");
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

        String fileName = args[0];


        try {
            FileMetadata metadata = SecureLocalFileManager.getManager().getFileMetadata(fileName);
            byte[] contents;

            try{
                contents =  SecureLocalFileManager.getManager().getFileContents(fileName);
            }catch (FileNotFoundException e){
                contents = new byte[0];
            }

            byte[] newSignature = KeyManager.getManager().sign(DigestUtils.sha256(LocalFileManager.getManager().getFileContents(fileName)));
            getLogger().debug("Sending content " + Arrays.toString(contents));
            CommitFileService commitFileService =  new CommitFileService(ClientConstants.baseUrl,
                    shell.getActiveSessionid(), fileName, contents, newSignature, String.valueOf(metadata.getId()),
                    shell.getActiveUser());
            commitFileService.execute();

        } catch (IOException e) {
            shell.println("No such file "+fileName);
            return;
        } catch (AlreadyExecutedException e) {
            e.printStackTrace();
        } catch (ServiceExecutionException e) {
            shell.println("error: " + e.getMessage());
        }

    }


    @Override
    public String getUsage(){
        return "commit <filename>";
    }
}
