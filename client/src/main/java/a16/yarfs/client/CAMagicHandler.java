/**
 * Created by jorge on 06/12/17
 **/
package a16.yarfs.client;

import a16.yarfs.ca.CAConstants;
import a16.yarfs.ca.handlers.AbstractTcpHandler;
import a16.yarfs.ca.handlers.messages.*;
import a16.yarfs.client.service.exception.MagicSecurityException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Class CAMagicHandler
 * jorge is an IDIOT because he didn't document this class.
 *
 **/
public class CAMagicHandler {

    private static Logger logger = Logger.getLogger(CAMagicHandler.class);


    public PublicKey getKey(String targetUser){
        // TODO
        return null;
    }


    public void publishKey(String username, String sessid) throws IOException, MagicSecurityException {
        logger.debug("Publishing key for "+username);
        // Open connection (server socket)
        Socket clientSocket = new Socket(ClientConstants.CA.address, ClientConstants.CA.getPublishPort());
        ObjectOutputStream oos =  new ObjectOutputStream(new BufferedOutputStream(new DataOutputStream(clientSocket.getOutputStream())));
        logger.debug("Streams are open!");


        SecretKey sessionKey = new SecretKeySpec(KeyManager.generateKey(), ClientConstants.KeyStandards.SYMMETRIC_ALGORITHM);

        logger.debug("Session key generated.");

        try {
            long nonce1 = new SecureRandom().nextLong();

            PublicKey CApubKey = loadPublicX509(ClientConstants.CA.CERTIFICATE_FILEPATH).getPublicKey();


            PubKeyAndNonce pkan = new PubKeyAndNonce(nonce1, KeyManager.getManager().getPublicKey());
            InitialRequestMessage irm = new InitialRequestMessage(KeyManager.AsymCipher(sessionKey.getEncoded(),
                    CApubKey.getEncoded()), AbstractMessage.cipherJSONObjectWithSymKey(pkan, sessionKey),
                    generateInitialHash(sessionKey, nonce1, KeyManager.getManager().getPublicKey()));

            logger.debug("Preparing to send message A");

            oos.writeObject(irm.toString());
            oos.flush();

            logger.debug("Sent message A");


            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            logger.debug("Waiting for message B...");
            RequestChallengeMessage rcm = new RequestChallengeMessage((String) ois.readObject());  //  {M2}Ks, {Hash(M2)}KprivCA
            logger.debug("Received message B"+rcm.toString());
            ChallengeAndClientPub ccp = rcm.getChallengeAndClientPub(sessionKey);                       // M2 = nonce1,nonce2,KpubUser,{challenge}KpubUser

            if( nonce1 != ccp.getNonce1()){                                 // Check nonces, with style!
                throw new MagicSecurityException("Wrong nonce 1 found!");
            }


            long nonce2 = ccp.getNonce2();  // nonce2

            byte[] challenge =  KeyManager.getManager().decipher(ccp.getCipheredChallenge()); // challenge ma bro

            PublicKey publicKey = ccp.getClientPublicKey();  // KpubUser

            byte[] myHash = generateChallengeHash(nonce1, nonce2, ccp.getCipheredChallenge(), ccp.getClientPublicKey());

            //if(Arrays.equals(myHash, KeyManager.unsign(rcm.getCipheredHash(), CApubKey.getEncoded()))){ // check the hash
            //    throw new MagicSecurityException("Hashes don't match. Tampering with data detected. Aborting...");
            //}

            // M3 = nonce2, nonce3, chall, username, sessionid
            // C = {M3}Ks, Hash(M3)


            long nonce3 = new SecureRandom().nextLong();

            ChallengeResponse cr = new ChallengeResponse(nonce2, nonce3, challenge, username, sessid);
            byte[] ciphered_cr = KeyManager.cipher(cr.toString().getBytes(), sessionKey.getEncoded());

            byte[] cr_hash = generateChallengeResponseHash(nonce2, nonce3, cr.getChallengeAnswer(), username, sessid);


            ChallengeResponseMessage crm = new ChallengeResponseMessage(ciphered_cr, cr_hash);

            logger.debug("Sending message...");

            oos.writeObject(crm.toString());
            oos.flush();
            logger.debug("Sent");


            String finalMessage = ((String) ois .readObject());
            byte[] ciphered_message = Base64.decodeBase64(finalMessage);
            byte[] message = KeyManager.decipher(ciphered_message, sessionKey.getEncoded());

            if(Arrays.equals(message, "OK".getBytes())){
                logger.debug("OK registering key.");
            }else{
                logger.debug("Something bad happened");
            }

            ois.close();
            oos.close();
            clientSocket.close();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    private X509Certificate loadPublicX509(String fileName)
            throws GeneralSecurityException {

        BufferedInputStream is = null;
//    InputStream is = null;
        X509Certificate crt = null;
       try {
            is = new BufferedInputStream(new FileInputStream(fileName));
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            crt = (X509Certificate)cf.generateCertificate(is);
        } catch (FileNotFoundException e) {
           e.printStackTrace();
       } finally {
            closeSilent(is);
        }
        return crt;
    }


    private void closeSilent(final InputStream is) {
        if (is == null) return;
        try { is.close(); } catch (Exception ign) {}
    }





    @SuppressWarnings("Duplicates")
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


    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
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


}
