/**
 * Created by jorge at 01/12/17
 */
package a16.yarfs.client;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *  Class KeyManager
 *  jorge is an IDIOT because it hasn't made documentation for this class.
 */
public class KeyManager {

    private static KeyManager manager;
    private static Logger logger = Logger.getLogger(KeyManager.class);
    private PublicKey publicKey;
    private PrivateKey privateKey;

    //FIXME this should be used in a further implementation
/*    public KeyManager(String passphrase){

    }*/

/*    public KeyManager(){
        try {
            loadKeys();
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM);
            generator.initialize(ClientConstants.KeyStandards.ASSYMETRIC_SIZE);
            KeyPair keyPair = generator.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
            logger.debug("Writing asymmetric keys");
            writePubKey();
            writePrivKey();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Invalid algorithm " + ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM, e);
        } catch (FileNotFoundException e){
            logger.error("File not found." + e.getMessage());

        } catch (IOException e){
            logger.error("Some IO error....", e);
        }

    }*/

    /**
     * Writes a public key to persistent storage. This key will stored in <i>username</i>.pub
     * @param username username of the user loading the keys.
     * @throws FileNotFoundException whenever the file with the keys doesn't exist for some reason.
     * @throws IOException I....I dunno man...this can happen in a plethora of situations.
     */
    private void writePubKey(String username) throws FileNotFoundException, IOException {
        logger.trace("Writing public key to storage.");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                ClientConstants.StorageStandards.KEY_FOLDER + username +
                        ClientConstants.KeyStandards.ASYMETRIC_PUBLIC_SUFFIX));
        bos.write(publicKey.getEncoded());
        logger.trace("Successfully wrote public key. Can be found at "+
                ClientConstants.StorageStandards.KEY_FOLDER+username+
                ClientConstants.KeyStandards.ASYMETRIC_PUBLIC_SUFFIX);
        bos.close();
    }

    /**
     * Writes a private key to persistent storage. The name of the file will be <i>username</i>.priv.
     * @param username username of the user writing the key.
     * @throws FileNotFoundException whenever the file with the key doesn't exist.
     * @throws IOException whenever IO error happens...
     */
    private void writePrivKey(String username) throws FileNotFoundException, IOException {
        logger.trace("Writing private key to storage");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                ClientConstants.StorageStandards.KEY_FOLDER + username +
                        ClientConstants.KeyStandards.ASYMETRIC_PRIVATE_SUFFIX));
        bos.write(privateKey.getEncoded());
        logger.trace("Successfully wrote public key. Can be found at "+
                ClientConstants.StorageStandards.KEY_FOLDER+username +
                ClientConstants.KeyStandards.ASYMETRIC_PRIVATE_SUFFIX);
        bos.close();
    }

    /**
     * Reads a public key from persistent storage. Depends on username.
     * @param username username of the user owner of the key.
     * @return the public key of the user.
     * @throws FileNotFoundException if the file with the public key does not exist.
     * @throws IOException whenever it feels like having a stroke.
     */
    public PublicKey readPubKey(String username) throws FileNotFoundException, IOException {
        logger.trace("Reading public key");
        File pubkeyFile = new File(ClientConstants.StorageStandards.KEY_FOLDER + username
                + ClientConstants.KeyStandards.ASYMETRIC_PUBLIC_SUFFIX);
        byte[] key = FileUtils.readFileToByteArray(pubkeyFile);
        try {
            return KeyFactory.getInstance(ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM).
                    generatePublic(new X509EncodedKeySpec(key));
        } catch (InvalidKeySpecException e) {
            logger.warn("The key that was read was invalid. Maybe format error?");
            //e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm " + ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM, e);
            //e.printStackTrace();
        }
        return null;

    }

    /**
     * Reads a private key from persistent storage. The filename depends on the username.
     * @param username username of the user owner of this private key.
     * @return the private key belonging to the user.
     * @throws FileNotFoundException if the file with the key does not exist.
     * @throws IOException whenever the system gets a cosmic ray.
     */
    // PUBLIC access? o.O
    public PrivateKey readPrivKey(String username) throws FileNotFoundException, IOException {
        File privKeyFile = new File(ClientConstants.StorageStandards.KEY_FOLDER
                + username + ClientConstants.KeyStandards.ASYMETRIC_PRIVATE_SUFFIX);
        byte[] key = FileUtils.readFileToByteArray(privKeyFile);
        try {
            return KeyFactory.getInstance(ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM).
                    generatePrivate(new PKCS8EncodedKeySpec(key));
        } catch (InvalidKeySpecException e) {
            logger.warn("The key that was read was invalid. Maybe format error?");
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm " + ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM, e);
        }
        return null;
    }

    /**
     * Ciphers a content (has to be a small content) with the user public key.
     * @param content content to be ciphered.
     * @return ciphered content.
     */
    @SuppressWarnings("Duplicates")
    public byte[] cipher(byte[] content){
        try {
            Cipher cipher = Cipher.getInstance(ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm " + ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM, e);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            logger.warn("The key that was read was invalid. Maybe format error?");
        } catch (BadPaddingException e) {
            logger.warn("Padding error?", e);
        } catch (IllegalBlockSizeException e) {
            logger.warn("Block size error?", e);
        }
        return null;
    }

    /**
     * Deciphers a really small content with the user's private key.
     * @param ciphered the ciphered content.
     * @return deciphered content.
     */
    @Deprecated
    @SuppressWarnings("Duplicates")
    public byte[] decipher(byte[] ciphered){
        try {
            Cipher cipher = Cipher.getInstance(ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(ciphered);
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm " + ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM, e);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            logger.warn("The key that was read was invalid. Maybe format error?");
        } catch (BadPaddingException e) {
            logger.warn("Padding error?", e);
        } catch (IllegalBlockSizeException e) {
            logger.warn("Block size error?", e);
        }
        return null;
    }

    /**
     * Signs a hash with the user's private key.
     * @param hash hash to be signed.
     * @return signed hash.
     */
    @SuppressWarnings("Duplicates")
    public byte[] sign(byte[] hash){
        try {
            Cipher cipher = Cipher.getInstance(ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm " + ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM, e);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            logger.warn("The key that was read was invalid. Maybe format error?");
        } catch (BadPaddingException e) {
            logger.warn("Padding error?", e);
        } catch (IllegalBlockSizeException e) {
            logger.warn("Block size error?", e);
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    public byte[] unsign(byte[] hash){
        try {
            Cipher cipher = Cipher.getInstance(ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm " + ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM, e);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            logger.warn("The key that was read was invalid. Maybe format error?");
        } catch (BadPaddingException e) {
            logger.warn("Padding error?", e);
        } catch (IllegalBlockSizeException e) {
            logger.warn("Block size error?", e);
        }
        return null;
    }


    /**
     * Generates a symmetric key randomly (supposedly).
     * @return a random key.
     */
    public static byte[] generateKey(){
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[ClientConstants.KeyStandards.SYMMETRIC_KEY_SIZE];
        random.nextBytes(key);
        return key;
    }

    /**
     * Loads the asymmetric keys of a user.
     * @param username username of the user to load the asymmetric keys.
     * @throws IOException whenever NSA is watching us.
     * @throws NoSuchAlgorithmException whenever the algorithm doesn't exist. Shouldn't happen, only if implementation
     * changes.
     */
    public void loadKeys(String username) throws IOException, NoSuchAlgorithmException {
        try {
            publicKey = readPubKey(username);
            privateKey = readPrivKey(username);
        } catch(FileNotFoundException e){
            logger.info("No keys were found. Proceeding to generate.");
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ClientConstants.KeyStandards.ASSYMETRIC_ALGORITHM);
            generator.initialize(ClientConstants.KeyStandards.ASSYMETRIC_SIZE);
            KeyPair keyPair = generator.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
            logger.debug("Writing asymmetric keys");
            writePubKey(username);
            writePrivKey(username);
        }

    }

    /**
     * Method for singleton implementation.
     * @return an instance of the KeyManager class
     */
    public static KeyManager getManager(){
        if( manager == null){
            manager =  new KeyManager();
        }
        return manager;
    }

    /**
     * Symmetric deciphering.
     * @param ciphered ciphered content.
     * @param key key to decipher content.
     * @return deciphered content.
     * @see a16.yarfs.client.ClientConstants.KeyStandards
     */
    public static byte[] decipher(byte[] ciphered, byte[] key){
        try {
            SecretKeySpec keyspec = new SecretKeySpec(key, ClientConstants.KeyStandards.SYMETRIC_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ClientConstants.KeyStandards.SYMETRIC_STANDARD);
            cipher.init(Cipher.DECRYPT_MODE, keyspec);
            return cipher.doFinal(ciphered);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
