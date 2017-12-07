package a16.yarfs.ca.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;

import a16.yarfs.ca.CAConstants;
import a16.yarfs.ca.KeyManager;
import a16.yarfs.ca.handlers.exceptions.CipherException;
import a16.yarfs.ca.handlers.exceptions.DecipherException;
import a16.yarfs.ca.handlers.messages.*;
import org.apache.log4j.Logger;
import org.json.JSONException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class RequestHandler extends AbstractTcpHandler {
    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    private KeyPair kp;
    private KeyManager km;

    public RequestHandler(KeyPair kp, KeyManager km) {
        this.km = km;
        this.kp = kp;
    }

    public void handle() {
        logger.debug("RequestHandler loaded with PubKey: " + AbstractTcpHandler.getSha256InBase64(kp.getPublic().getEncoded()) +
                " and PrivateKey: " + AbstractTcpHandler.getSha256InBase64(kp.getPrivate().getEncoded()) + ".");

        ServerSocket serverSocket = null;
        try {
            // Start listening...
            InetAddress addr = InetAddress.getByName(CAConstants.listenAddr);
            serverSocket = new ServerSocket(CAConstants.RequestService.getPort(), 50, addr);

            // TODO make this threaded (?)
            while(true) {

                try {
                    // Accept a connection and begin the publish process!
                    Socket clientSocket = serverSocket.accept();

                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                    // Get the InitialKeyRequestMessage
                    InitialKeyRequestMessage i = new InitialKeyRequestMessage(inputStream.readObject().toString());

                    // And parse it, discovering all the data in it
                    SecretKey sessionKey = getSessionKeyFromInitialKeyRequestMessage(i);
                    TargetUserAndNonce tun = i.getTargetUserAndNonce(sessionKey);
                    byte[] messageHash = i.getHash();
                    byte[] generatedHash = generateKeyRequestHash(sessionKey, tun.getNonce(), tun.getTargetUserName());

                    // FIXME nonces
                    if(Arrays.equals(messageHash, generatedHash)) {
                        // Hashes Check out
                        TargetUserAndPublicKey tup = new TargetUserAndPublicKey(tun.getNonce(),
                                tun.getTargetUserName(), km.getPublicKey(tun.getTargetUserName()));

                        PublicKey targetPubKey = km.getPublicKey(tun.getTargetUserName());

                        byte[] builtHash = buildResponseHash(tun.getNonce(), tun.getTargetUserName(), targetPubKey);
                        byte[] cipheredBuiltHash = AbstractTcpHandler.cipherWithKey(builtHash, kp.getPrivate());


                        TargetPubKeyMessage tpkm =
                                new TargetPubKeyMessage(AbstractMessage.cipherJSONObjectWithSymKey(tup, sessionKey),
                                        cipheredBuiltHash);

                        // Send Final message
                        try { outputStream.writeObject(tpkm.toString()); } catch (IOException e) {
                            logger.error("Coudln't send TargetPubKeyMessage to the client! " + e.getMessage());
                        }

                    } else {
                        // Hashes are bad!
                        //FIXME
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (DecipherException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (CipherException e) {
                    e.printStackTrace();
                }

            }


        } catch (IOException e) {
            logger.error("Something really bad happened. Exiting. " + e.getMessage());
        }


    }

    private byte[] buildResponseHash(long nonce1, String targetUsername, PublicKey targetPubKey) throws IOException {
        /*
            On the other end, the hash should be generated as we do here
         */

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(nonce1).array());
        byteStream.write(targetUsername.getBytes());
        byteStream.write(targetPubKey.getEncoded());

        return AbstractTcpHandler.sha256Digest(byteStream.toByteArray());
    }

    private byte[] generateKeyRequestHash(SecretKey sessionKey, long nonce1, String targetUsername) throws IOException {
        /*
            On the other end, the hash should be generated as we do here
         */

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(sessionKey.getEncoded());
        byteStream.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(nonce1).array());
        byteStream.write(targetUsername.getBytes());

        return AbstractTcpHandler.sha256Digest(byteStream.toByteArray());

    }

    private SecretKey getSessionKeyFromInitialKeyRequestMessage(InitialKeyRequestMessage i) throws DecipherException, JSONException {
        try {
            byte[] keyBytes = AbstractTcpHandler.decipherWithPrivateKey(i.getCAPublicCipheredKs(), kp.getPrivate());

            /*
                On the other end, this key should be generated with:
                SecretKey key = KeyGenerator.getInstance(..."AES"...).generateKey();
                byte[] keyBytes = key.getEncoded();

             */

            return new SecretKeySpec(keyBytes, CAConstants.PublishService.SYMMETRIC_CIPHER_ALGORITHM);


        } catch (DecipherException | JSONException e) {
            throw e;
        }
    }
}
