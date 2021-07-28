package com.bhuvancom.ecom;

import com.bhuvancom.ecom.util.PasswordManager;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/28/2021
 * Time    8:29 PM
 * Project ecomNew
 */
public class PasswordUnitTest {

    @Test
    public void givenString_whenEncrypt_thenSuccess() throws Exception {
            String input = "baeldung";
            String cipherText = PasswordManager.encrypt(input);
            String plainText = PasswordManager.decrypt(cipherText);
            Assert.assertEquals(input, plainText);

    }
}
