package ru.relastic.asyncworker.domain;
import android.util.Base64;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class MyCrypto {

    public static String myDecrypt(String key, String encrypted) {


        return new String("");
    }

    public static String MyEncrypt (String key, String iv, String encrypted) {


        return null;
    }



    public static String myDecryptOrig(String key, String iv, String encrypted) throws GeneralSecurityException {

        //Преобразование входных данных в массивы байт

        final byte[] keyBytes = key.getBytes();
        final byte[] ivBytes = iv.getBytes();

        final byte[] encryptedBytes = Base64.decode(encrypted, Base64.DEFAULT);

        //Инициализация и задание параметров расшифровки

        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(ivBytes));

        //Расшифровка

        final byte[] resultBytes = cipher.doFinal(encryptedBytes);
        return new String(resultBytes);
    }
    public static String MyEncryptOrig (String key, String iv, String encrypted)
            throws GeneralSecurityException {



        //<...>




        return null;
    }
    public static String MD5_Hash(String s) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(s.getBytes(),0,s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
