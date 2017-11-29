/**
 * Created by jorge at 11/11/17
 **/
package a16.yarfs.client.service;


import a16.yarfs.client.ClientConstants;
import a16.yarfs.client.service.exception.AlreadyExecutedException;
import a16.yarfs.client.service.exception.NotExecutedException;
import a16.yarfs.client.service.exception.ServiceResultException;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class AbstractHttpService
 * abstracts an http connection to be used by other HttpCommands
 **/
public abstract class AbstractHttpService {
    private static Logger logger = Logger.getLogger(AbstractHttpService.class);
    private URL url;
    private HttpURLConnection conn = null;
    private boolean connected = false;

    /**
     * set to true after the execute() method finishes
     */
    private boolean _executed = false;

    protected AbstractHttpService(String baseUrl, String endpoint) throws MalformedURLException {
        String fullUrl = baseUrl + endpoint;
        // FIXME should we check for stuff like "//" in fullUrl?
        this.url = new URL(fullUrl);
    }

    /**
     * connect to the remote endpoint. Must be called by the subclass after
     * setting the request parameters and before getting a result.
     *
     * @throws IOException
     */
    public void execute() throws IOException, AlreadyExecutedException {
        if (conn == null)
            conn = (HttpURLConnection) this.url.openConnection();
        conn.setConnectTimeout(ClientConstants.connectTimeout);
        conn.connect();
        connected = true;
        _executed = true;
    }


    private OutputStream getOutputStream() throws IOException, AlreadyExecutedException {
        if (connected) {
            throw new AlreadyExecutedException("Already connected. Can only get output stream before connecting");
        }
        if (conn == null)
            conn = (HttpURLConnection) this.url.openConnection();
        conn.setDoOutput(true);
        return conn.getOutputStream();
    }

    protected void setRequestParameters(JSONObject parameters) throws IOException, AlreadyExecutedException {
        OutputStream request = getOutputStream();
        OutputStreamWriter requestWriter;
        try {
            requestWriter = new OutputStreamWriter(request, "UTF-8");

            requestWriter.write(parameters.toString());
            requestWriter.flush();
            requestWriter.close();
            request.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected JSONObject getResponse() throws IOException, NotExecutedException, ServiceResultException {
        if (!connected) {
            throw new NotExecutedException("Not connected. Can only get response after connecting");
        }

        int code = conn.getResponseCode();
        InputStream error = conn.getErrorStream();
        logger.debug(conn.getResponseMessage());
        logger.debug("response code: " + code);
        logger.debug("getErrorStream: " + error);
        if (error != null) {
            InputStreamReader errorReader = new InputStreamReader(error);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            int result2 = errorReader.read();
            while (result2 != -1) {
                buf.write((byte) result2);
                result2 = errorReader.read();
            }

            errorReader.close();
            error.close();
            throw new ServiceResultException(code, buf.toString());
        } else {
            InputStream response = conn.getInputStream();
            InputStreamReader responseReader = new InputStreamReader(response);

            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            int result2 = responseReader.read();
            while (result2 != -1) {
                buf.write((byte) result2);
                result2 = responseReader.read();
            }

            responseReader.close();
            response.close();
            try {
                return new JSONObject(buf.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                throw new ServiceResultException("bad JSONObject");
            }
        }

    }

    /**
     * should be called by subclasses on their getResult methods
     */
    protected void assertExecuted() {
        if (!_executed) {
            throw new RuntimeException("Command was NOT executed!");
        }
    }

    protected static Logger getLogger() {
        return AbstractHttpService.logger;
    }
}
