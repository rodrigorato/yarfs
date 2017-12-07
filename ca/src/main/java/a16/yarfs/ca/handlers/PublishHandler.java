package a16.yarfs.ca.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());

                    // Get the InitialRequestMessage from the stream
                    InitialRequestMessage i = (InitialRequestMessage) inputStream.readObject();

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
                        logger.debug("New challenge issued: " + challenge);
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

                        // Send Challenge FIXME ignore any errors on sending?
                        try { outputStream.writeObject(r.toString()); } catch (IOException e) {
                            logger.error("Coudln't send RequestChallengeMessage to the client! " + e.getMessage());
                        }

                        // And receive the client's answer
                        ChallengeResponseMessage challengeResponseMessage =
                                (ChallengeResponseMessage) inputStream.readObject();

                        ChallengeResponse challengeResponse = challengeResponseMessage
                                .getChallengeResponse(sessionKey);

                        // FIXME check NONCES
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
                                a.execute();
                                isAuthenticated = a.isAuthenticated();
                            } catch(AlreadyExecutedException | NotExecutedException | ServiceExecutionException | ServiceResultException e) {
                                logger.warn("Couldn't authenticate the user with the server! " + e.getMessage());
                                isAuthenticated = false;
                            }

                            FinalMessage fin = null;
                            if(isAuthenticated &&
                                    km.setPublicKey(challengeResponse.getUsername(), pkn.getClientPublicKey())) {
                                // Send message back saying OK
                                // AND PUBLISH KEY

                                fin = new FinalMessage("OK".getBytes());

                            } else {
                                // Send NOK
                                fin = new FinalMessage("NOK".getBytes());
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
            logger.error("Something really bad happened. Exiting. " + e.getMessage());
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

            return new SecretKeySpec(keyBytes, CAConstants.PublishService.SYMMETRIC_CIPHER_ALGORITHM);


        } catch (DecipherException | JSONException e) {
            throw e;
        }
    }

}
