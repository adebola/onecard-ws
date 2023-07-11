package io.factorialsystems.msscprovider.recharge.onecard;

import io.factorialsystems.msscprovider.utils.Utility;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class MCrypt {

    private final IvParameterSpec ivSpec;
    private final SecretKeySpec keySpec;
    private final Cipher cipher;

    public MCrypt(String SecretKey, String iVSecret)  {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            ivSpec = new IvParameterSpec(iVSecret.getBytes(StandardCharsets.UTF_8));
            keySpec = new SecretKeySpec(SecretKey.getBytes(StandardCharsets.UTF_8), "AES");
            log.info("INFO5:["+ SecretKey +"] : " + iVSecret);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException Exception Message : {}", e.getMessage());
            throw new RuntimeException("Encryption Algorithm Not Found");
        } catch (NoSuchPaddingException e) {
            log.error("NoSuchPaddingException Exception Message : {}", e.getMessage());
            throw new RuntimeException("Encryption Padding Exception");
        }
    }

    public byte[] encryptByte(String text) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }

        byte[] encrypted = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            encrypted = cipher.doFinal((text.getBytes()));
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }
    public byte[] decryptByte(String code) throws Exception {
        if(code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            decrypted = cipher.doFinal(Base64.getMimeDecoder().decode(code));
            //Remove trailing zeroes
            if( decrypted.length > 0)
            {
                int trim = 0;
                for( int i = decrypted.length - 1; i >= 0; i-- ) if( decrypted[i] == 0 ) trim++;

                if( trim > 0 )
                {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage() );
        }

        return decrypted;
    }

    public byte[] decrypt(String code) throws Exception
    {
        if(code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            decrypted = cipher.doFinal(Utility.hexToBytes(code));
            //Remove trailing zeroes
            if( decrypted.length > 0)
            {
                int trim = 0;
                for( int i = decrypted.length - 1; i >= 0; i-- ) if( decrypted[i] == 0 ) trim++;

                if( trim > 0 )
                {
                    byte[] newArray = new byte[decrypted.length - trim];
                    System.arraycopy(decrypted, 0, newArray, 0, decrypted.length - trim);
                    decrypted = newArray;
                }
            }
        } catch (Exception e)
        {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }
}
