/**
 * Created by nuno at 19/11/17
 */
package a16.yarfs.client.presentation;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.PrintStream;

/**
 *  Class YarfsShell
 *  implements a command Shell for our YARFS client
 */
public class YarfsShell extends Shell {
    private String activeSessionid = null;

    private static Logger logger = Logger.getLogger(Shell.class);
    public YarfsShell(InputStream is, PrintStream w, boolean flush) {
        super("yarfs", is, w, flush);

        new LoginCommand(this, "login");
        // TODO add commands
    }


    public void setActiveSessionid(String sessionid) {
        activeSessionid = sessionid;
    }

    public String getActiveSessionid() {
        return activeSessionid;
    }

    public boolean isLoggedIn() {
        return activeSessionid != null;
    }
}
