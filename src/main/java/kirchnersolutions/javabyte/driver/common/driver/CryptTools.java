package kirchnersolutions.javabyte.driver.common.driver;

import kirchnersolutions.javabyte.driver.common.utilities.ByteTools;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 *
 * CryptTools Version
 *
 * @author rjojj
 */
public class CryptTools {

    public static final String VERSION = "1.0.02";

    static Map<String, Object> getRSAKeys() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put("private", privateKey);
        keys.put("public", publicKey);
        return keys;
    }

    static byte[] getPublicKeyBytes(PublicKey publicKey) {
        byte[] keyBytes = publicKey.getEncoded();
        return keyBytes;
    }

    /*
    static String serializePubKey(PublicKey pubKey) {
        RSAPublicKey publicKey = (RSAPublicKey) (pubKey);
        return publicKey.getModulus().toString() + "|"
                + publicKey.getPublicExponent().toString();
    }


    static PublicKey deserializePubKey(String serial) {
        String[] Parts = serial.split("|");
        RSAPublicKeySpec Spec = new RSAPublicKeySpec(
                new BigInteger(Parts[0]),
                new BigInteger(Parts[1]));
        try {
            return KeyFactory.getInstance("RSA").generatePublic(Spec);
        } catch (Exception e) {
            return null;
        }

    }

     */

    public static String decryptRSAMsg(byte[] encryptedText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(encryptedText));
    }

    /*
    public static String  encrypt(String text, PublicKey publicKey) throws Exception {
        // Set the plain text
        com.nimbusds.jose.Payload payload = new Payload(text);
        // Create the header
        JWEHeader header = new JWEHeader(RSA_OAEP_256, EncryptionMethod.A128CBC_HS256);
        // Create the JWE object and encrypt it
        JWEObject jweObject = new JWEObject(header, payload);
        jweObject.encrypt(new RSAEncrypter(publicKey);
        // Serialise to compact JOSE form...
        String jweString = jweObject.serialize();
        LOG.info("Generated Encrypted Key : {}", jweString);
        return jweString;
    }

     */

    public static String encryptRSAMsg(String plainText, PublicKey publicKey) throws Exception {
        /*
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return new String(Base64.getEncoder().encode(cipher.doFinal(java.util.Base64.getDecoder().decode(plainText))), "UTF-8");

         */
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return new java.lang.String(cipher.doFinal(plainText.getBytes("UTF-8")), "UTF-8");
    }

    static byte[] processRSAList(List<byte[]> input, boolean encrypt) {
        byte[] out;
        if (true) {
            out = new byte[(input.size() * 16)];
        } else {
            out = new byte[(input.size() * 16)];
        }

        int count = 0;
        for (byte[] bytes : input) {
            //System.out.println(bytes.length);
            for (byte byt : bytes) {
                out[count] = byt;
                count++;
            }
        }
        return out;
    }


    static byte[][] generateBlocks(byte[] data, boolean encrypt) {
        int size = data.length / 16;
        if (data.length % 16 != 0) {
            size++;
        }
        byte[][] blocks = new byte[size][16];
        int count1 = 0, count2 = 0, count3 = 0;
        byte[] temp = new byte[16];
        for (byte byt : data) {
            if (count2 == 15) {
                temp[count2] = data[count3];
                blocks[count1] = temp;
                count1++;
                count3++;
                count2 = 0;
                temp = new byte[16];
            } else {
                temp[count2] = data[count3];
                count2++;
                count3++;
            }
        }
        if (count2 != 0) {
            byte[] bfinal = new byte[count2];
            for (int i = 0; i < count2; i++) {
                bfinal[i] = temp[i];
            }
            blocks[count1] = bfinal;
        }
        return blocks;
    }

    static byte[] aesDecrypt(SecretKey key, byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(generateIV(key)));
        if (input.length < 16) {
            return cipher.doFinal(input);
        }
        List<byte[]> output = new ArrayList<>();
        byte[][] blocks = generateBlocks(input, false);
        int count = 0;
        for (byte[] block : blocks) {
            if (count + 1 == blocks.length) {
                output.add(cipher.doFinal(block));
                break;
            } else {
                output.add(cipher.update(block));
                count++;
            }
        }
        return processList(output, false);
    }

    static byte[] aesEncrypt(SecretKey key, byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //byte[] iv = generateRandomBytes(16);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(generateIV(key)));
        if (input.length < 16) {
            return cipher.doFinal(input);
        }
        List<byte[]> output = new ArrayList<>();
        byte[][] blocks = generateBlocks(input, true);
        int count = 0;
        //cipher.update(new byte[16]);
        for (byte[] block : blocks) {
            if (count + 1 == blocks.length) {
                output.add(cipher.doFinal(block));
                break;
            } else {
                output.add(cipher.update(block));
                count++;
            }
        }
        return processList(output, true);
    }

    static byte[] processList(List<byte[]> input, boolean encrypt) {
        byte[] out;
        if (true) {
            out = new byte[(input.size() * 16) + 16];
        } else {
            out = new byte[(input.size() * 16)];
        }

        int count = 0;
        for (byte[] bytes : input) {
            //System.out.println(bytes.length);
            for (byte byt : bytes) {
                out[count] = byt;
                count++;
            }
        }
        return out;
    }

    private static byte[] generateIV(SecretKey secretKey) throws Exception {
        byte[] sha = getSHA256(secretKey.getEncoded());
        for (int i = 0; i < 15; i++) {
            sha = getSHA256(secretKey.getEncoded(), sha);
        }
        return Arrays.copyOfRange(sha, 3, 19);
    }

    static SecretKey generateSecretKey(byte[] password) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");

            final byte[] digestOfPassword = md.digest(password);
            return new SecretKeySpec(digestOfPassword, "AES");
        } catch (Exception e) {
            return null;
        }
    }

    static SecretKey generateRandomSecretKey() {
        try {
            byte[] password = generateRandomBytes(1024 * 1024);
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final byte[] digestOfPassword = md.digest(password);
            return new SecretKeySpec(digestOfPassword, "AES");
        } catch (Exception e) {
            return null;
        }
    }

    static byte[] serializeAESKey(SecretKey key) {
        return key.getEncoded();
    }

    static File serializeAESKey(SecretKey key, File out) throws Exception {
        byte[] temp = key.getEncoded();
        temp = Base64.getEncoder().encode(temp);
        if (!out.exists()) {
            out.createNewFile();
        }
        ByteTools.writeBytesToFile(out, temp);
        return out;
    }

    static SecretKey deserializeAESKey(byte[] bytes) {
        SecretKey key = new SecretKeySpec(bytes, "AES");
        return key;
    }

    static SecretKey deserializeAESKey(File keyf) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(ByteTools.readBytesFromFile(keyf));
        return deserializeAESKey(bytes);
    }

    static byte[] getSHA256(String msg) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(msg.getBytes());
        byte[] byteData = md.digest();
        return byteData;
    }

    static byte[] getSHA256(byte[] msg) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(msg);
        byte[] byteData = md.digest();
        return byteData;
    }

    static byte[] getSHA256(byte[] msg, byte[] prev) throws Exception {
        BigInteger one = new BigInteger(msg);
        BigInteger two = new BigInteger(prev);
        one = one.add(two);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(one.toByteArray());
        byte[] byteData = md.digest();
        return byteData;
    }

    private static byte[] generateRandomBytes(int size) {
        byte[] bytes = new byte[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) random.nextInt(125);
        }
        return bytes;
    }

    public static byte[] serializePubKey(PublicKey object) throws Exception{
        PublicKey key = (PublicKey)object;
        return key.getEncoded();
        /*
        if(object != null){
            byte[] bytes = null;
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = null;
                out = new ObjectOutputStream(bos);
                out.writeObject(object);
                out.close();
                bos.close();
                bytes = bos.toByteArray();
            } catch (Exception e) {
            }
            return bytes;
        }else{
            return null;
        }

         */
    }

    public static PublicKey deserializePubKey(byte[] bytes) throws Exception{
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(new X509EncodedKeySpec(bytes));
        /*
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        PublicKey o = null;
        try {
            in = new ObjectInputStream(bis);
            o = (PublicKey)in.readObject();
            in.close();
            bis.close();;
        } catch (Exception e) {
        }
        return o;

         */
    }

    public static byte[] serializePivKey(PrivateKey object) throws Exception{
        PrivateKey key = (PrivateKey)object;
        return key.getEncoded();
        /*
        if(object != null){
            byte[] bytes = null;
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = null;
                out = new ObjectOutputStream(bos);
                out.writeObject(object);
                out.close();
                bos.close();
                bytes = bos.toByteArray();
            } catch (Exception e) {
            }
            return bytes;
        }else{
            return null;
        }

         */
    }

    static PrivateKey deserializePrivKey(byte[] bytes) throws Exception{
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
        /*
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        PrivateKey o = null;
        try {
            in = new ObjectInputStream(bis);
            o = (PrivateKey)in.readObject();
            in.close();
            bis.close();;
        } catch (Exception e) {
        }
        return o;

         */
    }

    static byte[] serializeKeystore(Object object) throws Exception{
        if(object != null){
            byte[] bytes = null;
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = null;
                out = new ObjectOutputStream(bos);
                out.writeObject(object);
                out.close();
                bos.close();
                bytes = bos.toByteArray();
            } catch (Exception e) {
            }
            return bytes;
        }else{
            return null;
        }
    }

    static Map<String, Object> deserializeKeystore(byte[] bytes) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream in = null;
        Map<String, Object> o = null;
        try {
            in = new ObjectInputStream(bis);
            o = (Map<String, Object>)in.readObject();
            in.close();
            bis.close();;
        } catch (Exception e) {
        }
        return o;
    }
}