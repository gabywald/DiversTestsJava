package gabywald.cipherdecipher;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Laurent Mercier (2021)
 * @author Gabriel Chandesris (2021, review)
 */
public class PassphraseEncoding {

	private static final Logger logger = LoggerFactory.getLogger(PassphraseEncoding.class);
	
	public static final String ALG = "DESede";
	
	// TODO change this / dynamic load ?!
	public static byte[] _k = new byte[] { 52, 102, 102, 99, 56, 100, 98, 51, 98, 97, 56, 98, 51, 52, 48, 51, 56, 100, 56, 56, 102, 101, 102, 99 };

	public PassphraseEncoding()
		{ ; }

	public static String encrypt(String str)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		PassphraseEncoding.logger.debug( new String(PassphraseEncoding._k) );

		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(1, new SecretKeySpec(PassphraseEncoding._k, "DESede"));
		return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes()));
	}

	public static String decrypt(String str)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		PassphraseEncoding.logger.debug( new String(PassphraseEncoding._k) );
		
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(2, new SecretKeySpec(PassphraseEncoding._k, "DESede"));
		return new String(cipher.doFinal(Base64.getDecoder().decode(str)));
	}

	public static String decryptQuietly(String passphrase) {
		try {
			return PassphraseEncoding.decrypt(passphrase);
		} catch (Exception e) {
			PassphraseEncoding.logger.error(e.getMessage());
			return null; // throw new RuntimeException(e);
		}
	}

	public static String encryptQuietly(String passphrase) {
		try {
			return PassphraseEncoding.encrypt(passphrase);
		} catch (Exception e) {
			PassphraseEncoding.logger.error(e.getMessage());
			return null; // throw new RuntimeException(e);
		}
	}

	public static PasswordEncoding buildPasswordEncodingFromPassphrase(String encodedPassphrase) {
		return new PasswordEncoding((List<String>) Stream.of(encodedPassphrase.split(",")). // 
				filter(Objects::nonNull).map(PassphraseEncoding::decryptQuietly).collect(Collectors.toList()));
	}
}
