package a16.yarfs.ca.handlers;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;

import a16.yarfs.ca.CAConstants;
import a16.yarfs.ca.KeyManager;
import a16.yarfs.ca.handlers.exceptions.CipherException;
import a16.yarfs.ca.handlers.exceptions.DecipherException;
import a16.yarfs.ca.handlers.messages.*;
import a16.yarfs.ca.service.AuthenticateService;
import a16.yarfs.ca.service.exception.AlreadyExecutedException;
import a16.yarfs.ca.service.exception.NotExecutedException;
import a16.yarfs.ca.service.exception.ServiceExecutionException;
import a16.yarfs.ca.service.exception.ServiceResultException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Rodrigo Rato on 12/6/17.
 */
public class PublishHandler extends AbstractTcpHandler {
    private static final Logger logger = Logger.getLogger(PublishHandler.class);

    private KeyPair kp;
    private KeyManager km;

    public PublishHandler(KeyPair kp, KeyManager km) {
        this.km = km;
        this.kp = kp;
    }

    public void handle() {
        logger.debug("PublishHandler loaded with PubKey: " + AbstractTcpHandler.getSha256InBase64(kp.getPublic().getEncoded()) +
                " and PrivateKey: " + AbstractTcpHandler.getSha256InBase64(kp.getPrivate().getEncoded()) + ".");

        ServerSocket serverSocket = null;
        try {
            // Start listening...
            InetAddress addr = InetAddress.getByName(CAConstants.listenAddr);
            serverSocket = new ServerSocket(CAConstants.PublishService.getPort(), 50, addr);

            // TODO make this threaded (?)
            while(true) {


                try {
                    // Accept a connection and begin the publish process!
                    Socket clientSocket = serverSocket.accept();
                    logger.debug("Accepted client. Getting streams...");

                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    logger.debug("Got streams.");

                    // Get the InitialRequestMessage from the stream
                    Object o = inputStream.readObject();


                    logger.debug("Received request "+ o.toString());

                    InitialRequestMessage i = new InitialRequestMessage((String)o);


                    // And parse it, discovering all the data in it
                    SecretKey sessionKey = getSessionKeyFromInitialRequestMessage(i);
                    PubKeyAndNonce pkn = i.getPubKeyAndNonce(sessionKey);
                    byte[] messageHash = i.getHash();
                    byte[] generatedHash = generateInitialHash(sessionKey,
                            pkn.getNonce(), pkn.getClientPublicKey());


                    // Check if hashes check out before continuing
                    if(compareHashes(generatedHash, messageHash)) {
                        // Hash check out

                        // Issue a challenge to test if the client owns that public key
                        byte[] challenge = new byte[CAConstants.PublishService.CHALLENGE_SIZE];
                        new SecureRandom().nextBytes(challenge);
                        logger.debug("New challenge issued: " + Arrays.toString(challenge));
                        byte[] cipheredChallenge = cipherWithKey(challenge, pkn.getClientPublicKey());

                        // Get a new nonce for this message
                        long nonce2 = RandomUtils.nextLong();

                        // Create the challenge object that goes in the RequestChallengeMessage sent later
                        ChallengeAndClientPub chall = new ChallengeAndClientPub(pkn.getNonce(),
                                nonce2, cipheredChallenge, pkn.getClientPublicKey());

                        byte[] challengeHash = generateChallengeHash(pkn.getNonce(),
                                nonce2, cipheredChallenge, pkn.getClientPublicKey());

                        // The message that will be sent to the client
                        RequestChallengeMessage r = new RequestChallengeMessage(
                                AbstractMessage.cipherJSONObjectWithSymKey(chall, sessionKey),
                                AbstractTcpHandler.cipherWithKey(challengeHash, kp.getPrivate()));

                        ObjectOutputStream outputStream = new ObjectOutputStream(
                                new DataOutputStream(clientSocket.getOutputStream()));
                        // Send Challenge FIXME ignore any errors on sending?
                        try {
                            logger.debug("Sending message challenge...");
                            outputStream.writeObject(r.toString());
                            logger.debug("Sent");
                        } catch (IOException e) {
                            logger.error("Coudln't send RequestChallengeMessage to the client! " + e.getMessage());
                            outputStream.close();
                            inputStream.close();
                            clientSocket.close();
                        }

                        // And receive the client's answer
                        Object o2 = inputStream.readObject();
                        logger.debug("Received"+o2.toString());
                        ChallengeResponseMessage challengeResponseMessage = new ChallengeResponseMessage(o2.toString());

                        ChallengeResponse challengeResponse = challengeResponseMessage
                                .getChallengeResponse(sessionKey);


                        if(nonce2 != challengeResponse.getNonce2()){
                            logger.warn("Error checking nonce 2. Possible replay attack. Aborting...");

                        }

                        messageHash = challengeResponseMessage.getHash();
                        generatedHash = generateChallengeResponseHash(challengeResponse.getNonce2(),
                                challengeResponse.getNonce3(), challengeResponse.getChallengeAnswer(),
                                challengeResponse.getUsername(), challengeResponse.getSessionId());

                        if(compareHashes(messageHash, generatedHash)) {
                            // Hashes are the same
                            AuthenticateService a =
                                    new AuthenticateService(CAConstants.baseServerUrl + CAConstants.Endpoints.AUTHENTICATE,
                                    challengeResponse.getSessionId(), challengeResponse.getUsername());

                            boolean isAuthenticated = false;

                            try {
                                logger.debug("Checking authentication.");
                                a.execute();
                                isAuthenticated = a.isAuthenticated();
                                logger.debug("Authentication is "+isAuthenticated);
                            } catch(AlreadyExecutedException | NotExecutedException | ServiceExecutionException | ServiceResultException e) {
                                logger.warn("Couldn't authenticate the user with the server! " + e.getMessage());
                                isAuthenticated = false;
                            }

                            String fin = null;
                            if(isAuthenticated &&
                                    km.setPublicKey(challengeResponse.getUsername(), pkn.getClientPublicKey())) {
                                logger.debug("Published key: "+ Arrays.toString(pkn.getClientPublicKey().getEncoded())
                                + " For user "+ challengeResponse.getUsername());
                                // Send message back saying OK
                                // AND PUBLISH KEY

                                fin = Base64.encodeBase64String(AbstractMessage.cipherDataWithSymKey("OK".getBytes(),
                                        sessionKey));

                            } else {
                                logger.warn("Error publishing");
                                // Send NOK
                                fin = Base64.encodeBase64String(AbstractMessage.cipherDataWithSymKey("NOK".getBytes(),
                                        sessionKey));
                            }

                            // Send Final message
                            try { outputStream.writeObject(fin.toString()); } catch (IOException e) {
                                logger.error("Coudln't send FinalMessage to the client! " + e.getMessage());
                            }


                        } else {
                            // Hashes didn't check out.
                            throw new GeneralSecurityException("Message Hash wasn't correct. IT WAS TAMPERED!");
                        }

                    } else {
                        // Hashes didn't check out.
                        throw new GeneralSecurityException("Message Hash wasn't correct. IT WAS TAMPERED!");
                    }


                } catch (ClassNotFoundException e) { // FIXME send NOK messages?
                    logger.error("Wrong object type received! " + e.getMessage());
                    continue;
                } catch (DecipherException e) { // FIXME send NOK messages?
                    logger.error("Coudln't decipher the session key! Skipping to next request. " + e.getMessage());
                    continue;
                } catch (JSONException e) { // FIXME send NOK messages?
                    logger.error("Request had no session key! Skipping to next request." + e.getMessage());
                    e.printStackTrace();
                    continue;
                } catch (GeneralSecurityException e) { // FIXME send NOK messages?
                    logger.error("Message didn't check out! Skipping to next request." + e.getMessage());
                    continue;
                } catch (CipherException e) {
                    logger.error("Couldn't Cipher some data. " + e.getMessage());
                    continue;
                }
            }

        } catch (IOException e) {
            logger.error("Something really bad happened. Exiting. " + e.getMessage(), e);
        }
    }

    private byte[] generateInitialHash(SecretKey sessionKey, long nonce1, PublicKey clientPubKey) throws IOException {
        /*
            On the other end, the hash should be generated as we do here
         */

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(sessionKey.getEncoded());
        byteStream.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(nonce1).array());
        byteStream.write(clientPubKey.getEncoded());

        return AbstractTcpHandler.sha256Digest(byteStream.toByteArray());

    }

    private byte[] generateChallengeHash(long nonce1, long nonce2, byte[] cipheredChallenge, PublicKey clientPubKey) throws IOException {
        /*
            On the other end, the hash should be generated as we do here
         */

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(nonce1).array());
        byteStream.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(nonce2).array());
        byteStream.write(cipheredChallenge);
        byteStream.write(clientPubKey.getEncoded());

        return AbstractTcpHandler.sha256Digest(byteStream.toByteArray());

    }

    private byte[] generateChallengeResponseHash(long nonce2, long nonce3, byte[] challengeResponse, String username, String sessionId) throws IOException {
        /*
            On the other end, the hash should be generated as we do here
         */

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byteStream.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(nonce2).array());
        byteStream.write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(nonce3).array());
        byteStream.write(challengeResponse);
        byteStream.write(username.getBytes());
        byteStream.write(sessionId.getBytes());

        return AbstractTcpHandler.sha256Digest(byteStream.toByteArray());

    }


    private boolean compareHashes(byte[] hash1, byte[] hash2) {
        return Arrays.equals(hash1, hash2);
    }

    private SecretKey getSessionKeyFromInitialRequestMessage(InitialRequestMessage i) throws DecipherException, JSONException {
        try {
            byte[] keyBytes = AbstractTcpHandler.decipherWithPrivateKey(i.getCAPrivateCipheredKs(), kp.getPrivate());

            /*
                On the other end, this key should be generated with:
                SecretKey key = KeyGenerator.getInstance(..."A0ES"...).generateKey();
                byte[] keyBytes = key.getEncoded();

             */

            return new SecretKeySpec(keyBytes, "AES");


        } catch (DecipherException | JSONException e) {
            throw e;
        }
    }

}
