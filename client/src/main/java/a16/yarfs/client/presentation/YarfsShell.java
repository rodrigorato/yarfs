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
    private static Logger logger = Logger.getLogger(Shell.class);
    public YarfsShell(InputStream is, PrintStream w, boolean flush) {
        super("yarfs", is, w, flush);
        // TODO add commands
    }
}
