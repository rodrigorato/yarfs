package a16.yarfs.client.presentation;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.ServiceExecutionException;
import a16.yarfs.client.service.file.RefreshService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public class RefreshCommand extends Command{

    public RefreshCommand(Shell sh, String name) {
        super(sh, name, "refreshes metadata from server.");
    }

    @Override
    void execute(String[] args) {
        // check prerequisites
        YarfsShell shell = (YarfsShell) getShell();
        if (!shell.isLoggedIn()) {
            shell.println("please login first");
            return;
        }

        try {
            RefreshService service = new RefreshService(ClientConstants.baseServerUrl, shell.getActiveSessionid());
            service.execute();
            List<String> newFiles = service.getNewFiles();
            if( newFiles.size() == 0 ){
                shell.println("No new files. ");
            }else{
                shell.println("New files were found.");
                for(String filename : newFiles){
                    shell.println(filename);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlreadyExecutedException e) {
            e.printStackTrace();
        } catch (ServiceExecutionException e) {
            shell.println("error: " + e.getMessage());
        }

    }
}
