/**
 * Created by jorge on 06/12/17
 **/
package a16.yarfs.client.presentation;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import a16.yarfs.client.service.file.UnshareFileService;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Class Unsharecommand
 * jorge is an IDIOT because he didn't document this class.
 *
 **/
public class Unsharecommand extends Command{


    public Unsharecommand(Shell sh, String name) {
        super(sh, name, "Unshare a file with a user.");
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

        String targetUser = args[0];
        String filename = args[1];

        try {
            UnshareFileService unshareFileService =  new UnshareFileService(ClientConstants.baseServerUrl,
                    shell.getActiveSessionid(), filename, targetUser);
            unshareFileService.execute();
            if(unshareFileService.getSuccess()){
                shell.println("OK");
            }else{
                shell.println("Some problem happened.");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlreadyExecutedException e) {
            e.printStackTrace();
        } catch (ServiceResultException e) {
            shell.println(e.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NotExecutedException e) {
            e.printStackTrace();
        }



    }
    @Override
    public String getUsage(){
        return "unshare <username> <filename>";
    }
}
