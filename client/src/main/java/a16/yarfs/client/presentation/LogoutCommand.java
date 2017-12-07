package a16.yarfs.client.presentation;

import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.LocalFileManager;
import a16.yarfs.client.SecureLocalFileManager;
import a16.yarfs.client.service.exception.LogoutServiceException;
import a16.yarfs.client.service.exception.ServiceExecutionException;
import a16.yarfs.client.service.user.LogoutService;

import java.net.MalformedURLException;

public class LogoutCommand extends Command{

    /**
     * create a new login command and adds it to the shell
     * @param sh   the YarfsShell where the command will be invoked from
     * @param name the name used to invoke the command
     */
    public LogoutCommand(YarfsShell sh, String name) {
        super(sh, name, "ends targeted session");
    }

    @Override
    void execute(String[] args) {

        YarfsShell shell = (YarfsShell) getShell();
        String sessid = shell.getActiveSessionid();

        try {
            LogoutService service = new LogoutService(ClientConstants.baseServerUrl, sessid);
            service.execute();
            shell.setActiveSessionid(null);
            LocalFileManager.getManager().selfDestruct();
            SecureLocalFileManager.getManager().selfDestruct();
            shell.setActiveUser("");
            shell.println("Logout successful");


        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (LogoutServiceException e) {
            shell.println("Error logging out!");
        } catch (ServiceExecutionException e) {
            shell.println("error: " + e.getMessage());
        }
    }

    @Override
    public String getUsage() {
        return "Usage: "+ getName() + " <session_id>";
    }

}
