package gabywald.cipherdecipher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Laurent Mercier (2021)
 * @author Gabriel Chandesris (2021, review)
 */
public class PasswordEncoding {

	public static final String DEFAULT_ALGORITHM	= "PBEWithMD5AndDES";
	public static final String PREFIX				= "crypt:";
	private static final Logger logger				= LoggerFactory.getLogger(PasswordEncoding.class);
	private static final List<StandardPBEStringEncryptor> encryptors = new ArrayList<StandardPBEStringEncryptor>();

	public PasswordEncoding() 
		{ ; }

	public PasswordEncoding(List<String> passphrases) {
		PasswordEncoding.encryptors.clear();
		this.addPassphrases(passphrases);
	}

	public static String doDecryption(String encryptedPassword) {
		String decryptedPassword = null;
		Iterator<StandardPBEStringEncryptor> var2 = PasswordEncoding.encryptors.iterator();
		while (var2.hasNext()) {
			StandardPBEStringEncryptor encryptor = (StandardPBEStringEncryptor) var2.next();
			try {
				decryptedPassword = encryptor.decrypt(encryptedPassword);
				if (decryptedPassword.startsWith(PasswordEncoding.PREFIX)) {
					return decryptedPassword.substring(PasswordEncoding.PREFIX.length());
				}
			} catch (Exception e) {
				String msg = "Can't decrypt password, the password is either not crypted or the passphrase is wrong : " + e.getClass().getSimpleName();
				if (e.getMessage() != null) {
					msg = msg + " (msg = " + e.getMessage() + ")";
				}
				PasswordEncoding.logger.debug(msg);
			}
		}
		PasswordEncoding.logger.warn("Can't decrypt password with any of the passphrases configured. Considering password is not crypted");
		return decryptedPassword != null ? decryptedPassword : encryptedPassword;
	}

	private StandardPBEStringEncryptor getPBEStringEncryptor(String passphrase) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(passphrase);
		encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.initialize();
		return encryptor;
	}

	public void addPassphrases(List<String> passphrases) {
		Iterator<String> var2 = passphrases.iterator();
		while (var2.hasNext()) {
			String passphrase = (String) var2.next();
			if (passphrase != null && !passphrase.trim().isEmpty()) {
				PasswordEncoding.encryptors.add(this.getPBEStringEncryptor(passphrase.trim()));
			}
		}
	}

	public String encrypt(String password) {
		return (!PasswordEncoding.encryptors.isEmpty()) ? ((StandardPBEStringEncryptor) PasswordEncoding.encryptors.get(0)).encrypt("crypt:" + password) : password;
	}

	public String decryptOrReadIfNotEncrypted(String encryptedPassword) {
		return PasswordEncoding.doDecryption(encryptedPassword);
	}
}