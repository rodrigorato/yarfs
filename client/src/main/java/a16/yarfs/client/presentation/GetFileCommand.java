/**
 * Created by nuno at 29/11/17
 */
package a16.yarfs.client.presentation;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.service.dto.FileDto;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import a16.yarfs.client.service.file.AddFileService;
import a16.yarfs.client.service.file.GetFileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;

/**
 *  Class AddFileCommand
 *  uploads a (new) file to the yarfs server
 */
public class GetFileCommand extends Command {

    public GetFileCommand(YarfsShell sh, String name) {
        super(sh, name, "get a file from " + sh.getName() + " to your computer");
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

        String remoteFilename = args[0];
        String localFilename = args.length == 1 ? remoteFilename : args[1];

        if(localFilename.trim().isEmpty() || remoteFilename.trim().isEmpty()) { // can this happen? who knows...
            shell.println("file names must not be empty");
            return;
        }


        // get the file from the server
        try {
            // FIXME remoteFilename is being converted to file id; don't forget to update getUsage()
            getLogger().warn("FIXME remoteFilename is being converted to file id");
            GetFileService service = new GetFileService(ClientConstants.baseUrl,
                    shell.getActiveSessionid(), remoteFilename);

            service.execute();

            // get the result
            FileDto file = service.getFile();
            shell.println("got file '"+file.getName()+"' with id " + file.getId()
                    + "' ("+ FileUtils.byteCountToDisplaySize(file.getContents().length)+")");

            // FIXME deal with encryption, keys, signature, etc (should probably be abstracted somewhere)

            // write the file to local storage
            try {
                /*try {
                    FileDto localFile = LocalFileManager.getManager().getFile(localFilename); This line was causing an error FIXME later

                    if (localFile.equals(file)) {
                        shell.println("Local file is up to date, no changes were made");
                        return;
                    }
                } catch (IOException e){
                    shell.println("Downloading new file " + localFilename);
                }*/

                //LocalFileManager.getManager().putFileContents(localFilename, file.getContents());
                LocalFileManager.getManager().putFile(new FileDto(file.getId(), file.getName(),
                        file.getOwner(), file.getContents(), file.getSignature(), file.getKey()));
                shell.println("written to '" + localFilename + "'");
            } catch ( FileAlreadyExistsException e) {
                shell.println(localFilename + ": File exists");
                return;
            } catch (IOException e) {
                getLogger().error("IO error", e);
                shell.println(localFilename + ": IO error: " + e.getMessage());
                return;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ServiceResultException e) {
            shell.println("could not get file: " + e.getMessage());
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
        return "Usage: "+ getName() + " <remote file id> [local filename]" + System.lineSeparator() +
                "<local filename> defaults to <remote file id>";
    }
}
