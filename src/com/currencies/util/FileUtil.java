/*
 * This FileUtil Utility provide MD5 hash text for file name of JSON and 
 * generates file in ./export path.
 * 
 * Comments added by Ali Malik - 26.01.2020
 */
package com.currencies.util;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONException;
import org.json.JSONObject;

public class FileUtil {

    public static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5 
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest 
            //  of an input digest() return array of byte 
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value 
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exportJsonFile(String data, String fileName, String folderPath) throws IOException, JSONException {
        FileWriter fileWriter = new FileWriter(folderPath+fileName);
        JSONObject obj;
        obj = new JSONObject(data);
        fileWriter.write(obj.toString(5));
        System.out.println("\nMD5 Hash of JSON as FileName is generated under ./export folder.");
        fileWriter.close();
    }
}
