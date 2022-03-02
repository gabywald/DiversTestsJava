package gabywald.cipherdecipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gabywald.utilities.others.Base64;

import java.io.IOException;
import java.util.Arrays;

public class EncryptDecryptPassword {
	private static final Logger logger = LoggerFactory.getLogger(EncryptDecryptPassword.class);

	public static void main(String[] args) {
		
		String encodedPassPhrase1 = "cT9Y6pn6VTuWiO5sAiPdeQ==";
		String encodedPassPhrase2 = "NXJCkLV4FjvIzFKCgpd0yQ==";
		String encodedPassPhrase3 = "lpHlmgiEExsrblbSkFluHg=="; // passPhrase
		try {
			EncryptDecryptPassword.logger.info( "{}", Base64.decode(encodedPassPhrase1) );
			EncryptDecryptPassword.logger.info( "{}", Base64.decode(encodedPassPhrase2) );
			EncryptDecryptPassword.logger.info( "{}", Base64.decode(encodedPassPhrase3) );
		} catch (IOException e) {
			e.printStackTrace();
		}

		EncryptDecryptPassword.testEncrypt( encodedPassPhrase3, "PassWord");
		EncryptDecryptPassword.testDecrypt( encodedPassPhrase3, "0st3VonqhpwP4xYQZeV2wKwkUNpW+1RU" );

		EncryptDecryptPassword.testEncrypt( encodedPassPhrase3, "PassWord2021_");
		EncryptDecryptPassword.testDecrypt( encodedPassPhrase3, "l6YOUa1YO38WMwpyisx4deEgIZO0X9eaug7YGnzsFqM=" );

		// attempted 'apiUser2021_'
		// EncryptDecryptPassword.testEncrypt( "passPhrase", "apiUser2021_");
		EncryptDecryptPassword.testDecrypt( "passPhrase", "d06n8H1StRmIaYtNzWlA/WHqnAhsWfw5qPHF1ABElMo=" );

	}

	private static String testEncrypt(String s1, String s2) {
		String passPhrase	= s1;
		EncryptDecryptPassword.logger.info("passPhrase: {}", passPhrase);
		PasswordEncoding passwordEncoding = new PasswordEncoding(Arrays.asList(passPhrase));

		String password		= s2;
		String s3			= passwordEncoding.encrypt(password);
		EncryptDecryptPassword.logger.info("password '{}' is crypted '{}'",password, s3 );
		return s3;
	}

	private static String testDecrypt(String s1, String s2) {
		String passPhrase			= s1;
		EncryptDecryptPassword.logger.info("passPhrase: {}", passPhrase);
		PasswordEncoding passwordEncoding = new PasswordEncoding(Arrays.asList(passPhrase));
		String encryptedPassword	= s2;
		String s3					= passwordEncoding.decryptOrReadIfNotEncrypted(encryptedPassword);
		EncryptDecryptPassword.logger.info("encryptedPassword '{}' is '{}'", encryptedPassword, s3);
		return s3;
	}


}
