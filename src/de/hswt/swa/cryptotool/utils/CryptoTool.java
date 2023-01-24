package de.hswt.swa.cryptotool.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

public class CryptoTool {

	static String cryptTransformation = "AES";

	/**
	 * Nothing changed
	 */
	public boolean encrypt(OutputStream writer, byte[] input, String passPhrase) {
		
		Cipher c;
		try {
			if (passPhrase.length() > 16) return false;
			while (passPhrase.length() < 16) {
				passPhrase = passPhrase + 'x';
			}
			c = Cipher.getInstance(cryptTransformation);
			Key k = new SecretKeySpec( passPhrase.getBytes(),cryptTransformation);
			c.init( Cipher.ENCRYPT_MODE, k );
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		} 
	    
		try {
			CipherOutputStream cypherOut = new CipherOutputStream(writer, c);
			for (byte nextByte : input) {
				cypherOut.write(nextByte);
			}
			cypherOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Nothing changed
	 */
	public byte[] decrypt(InputStream input, String passPhrase) {
		
		Cipher c;
		try {
			if (passPhrase.length() > 16) return null;
			while (passPhrase.length() < 16) {
				passPhrase = passPhrase + 'x';
			}
			c = Cipher.getInstance(cryptTransformation);
			Key k = new SecretKeySpec( passPhrase.getBytes(),cryptTransformation);
			c.init( Cipher.DECRYPT_MODE, k );
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} 
	    
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			CipherInputStream cypherIn = new CipherInputStream(input, c);
			int nextByte;
			while ((nextByte = cypherIn.read()) != -1) {
				bos.write(nextByte);
			}
			cypherIn.close();
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method gets compiled to the external program used in the application.
	 */
	public static void main(String[] arg) {
	
		CryptoTool crypto = new CryptoTool();
		// mode == 0 is encrypt; mode == 1 is decrypt
		int mode = Integer.parseInt(arg[0]);
		String text = arg[1];
		String password = arg[2];

		if (mode == 0) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			crypto.encrypt( out, text.getBytes(), password);
			String s = Base64.getEncoder().encodeToString(out.toByteArray());
			out.reset();
			System.out.println(s);
		}
		if (mode == 1) {
			byte[] bytes  = Base64.getDecoder().decode(text);
			InputStream is = new ByteArrayInputStream( bytes);
			byte[] plain = crypto.decrypt(is, password);
			System.out.println(new String(plain));
		}
	}

}
