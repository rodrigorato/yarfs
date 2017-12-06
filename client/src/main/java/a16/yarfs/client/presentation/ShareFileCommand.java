package a16.yarfs.client.presentation;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.file.ShareFileService;

import java.io.IOException;
import java.net.MalformedURLException;

public class ShareFileCommand extends Command{

    public ShareFileCommand(YarfsShell sh, String name) {
        super(sh, name, "share a file with a user");
    }

    @Override
    void execute(String[] args) {

        YarfsShell shell = (YarfsShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("please login first");
            return;
        }

        // check (and parse) arguments

        if (args.length < 2 || args.length > 3) {
            shell.println(getUsage());
            return;
        }

        String user = args[0];
        String filename = args[1];

        try {
            shell.println("Sharing file "+filename+" with "+user);
            ShareFileService service = new ShareFileService(ClientConstants.baseServerUrl, filename, user, shell.getActiveSessionid());
            service.execute();
            if(service.successful()){
                shell.println("Successfully shared with "+user);
            }
            else{
                shell.println("Failed to share with "+user);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlreadyExecutedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public String getUsage(){
        return "share <username> <filename>";
    }
}
