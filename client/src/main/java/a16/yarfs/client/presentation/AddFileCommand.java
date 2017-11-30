/**
 * Created by nuno at 29/11/17
 */
package a16.yarfs.client.presentation;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import a16.yarfs.client.service.file.AddFileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

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
        try {
            content = LocalFileManager.getFileContents(localFilename);
            shell.println("read '" + localFilename + "' ("+ FileUtils.byteCountToDisplaySize(content.length)+")");
        } catch (FileNotFoundException e) {
            shell.println(localFilename + ": No such file");
            return;
        } catch (IOException e) {
            shell.println("error reading " + localFilename + ": " + e.getMessage());
            return;
        }

        // FIXME deal with encryption, keys, signature, etc

        byte[] signature = DigestUtils.sha256Hex(content).getBytes(); // FIXME change this to an HMAC
        byte[] key = "TODO-key".getBytes();

        // send the file to the server
        try {
            AddFileService service = new AddFileService(ClientConstants.baseUrl,
                    shell.getActiveSessionid(), remoteFilename, content, signature, key);

            service.execute();

            // check the result
            long fileId = service.getFileId();
            shell.println("added file '"+remoteFilename+"' with id " + fileId);
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
        }
    }


    @Override
    public String getUsage() {
        return "Usage: "+ getName() + " <local filename> [remote filename]" + System.lineSeparator() +
                "<remote filename> defaults to <local filename>";
    }
}
