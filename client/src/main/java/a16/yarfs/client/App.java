package a16.yarfs.client;

import a16.yarfs.client.presentation.YarfsShell;

import java.io.IOException;

/**
 * The entry point for our yarfs client
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Starting yarfs client..." );
        YarfsShell shell = new YarfsShell(System.in, System.out, true);
        try {
            shell.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
