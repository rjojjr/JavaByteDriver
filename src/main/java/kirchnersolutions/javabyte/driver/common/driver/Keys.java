package kirchnersolutions.javabyte.driver.common.driver;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

public class Keys {

    private Map<String, Object> RSAKeys;
    private SecretKey secretKey = null;

    void generateRSAKeys() throws Exception{
        RSAKeys = CryptTools.getRSAKeys();
    }

    byte[] getPublicKey() throws Exception{
        return CryptTools.serializePubKey((PublicKey)RSAKeys.get("public"));
    }

    boolean hasKey(){
        if(secretKey == null){
            return false;
        }
        return true;
    }

    boolean decryptAESKey(byte[] key) {
        try{
            secretKey = CryptTools.deserializeAESKey(CryptTools.decryptRSAMsg(key, (PrivateKey)RSAKeys.get("private")).getBytes("UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    byte[] decryptAESResponse(byte[] response) throws Exception{
        if(secretKey == null){
            return null;
        }
        return CryptTools.aesDecrypt(secretKey, Base64.getDecoder().decode(response));
    }

    byte[] encryptAESRequest(byte[] request) throws Exception{
        if(secretKey == null){
            return null;
        }
        return CryptTools.aesEncrypt(secretKey, Base64.getEncoder().encode(request));
    }

}
