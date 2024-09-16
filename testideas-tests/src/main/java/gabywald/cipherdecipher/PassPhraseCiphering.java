package gabywald.cipherdecipher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Gabriel Chandesris (2021)
 */
public class PassPhraseCiphering {

	private static final Logger logger = LoggerFactory.getLogger(PassPhraseCiphering.class);

	public static void showHelp() {
		System.out.println("To encode : \"PassPhraseCiphering <passphrase> <password>[ <password>?]\"");
		System.out.println("To decode : \"PassPhraseCiphering <passphrase> -d <password>[ <password>?]\"");
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			PassPhraseCiphering.showHelp();
			return;
		}
		for (String arg : args) {
			PassPhraseCiphering.logger.info("\t arg {" + arg + "}");
		}
		String passPhrase = args[0];
		boolean isDecode = false;
		isDecode = ((args.length > 3) && (args[1].equals("-d")));
		List<String> passwords = new ArrayList<String>();
		for (int i = ((isDecode) ? 2 : 1); i < args.length; i++) {
			passwords.add(args[i]);
		}
		String ecPassPhrase = PassphraseEncoding.encryptQuietly(passPhrase);
		PassPhraseCiphering.logger.info("passphrase: '{}'", ecPassPhrase);
		PasswordEncoding passwordEncoding = new PasswordEncoding(Arrays.asList(ecPassPhrase));
		if (isDecode) {
			for (String pwd : passwords) {
				String uecPWD = passwordEncoding.decryptOrReadIfNotEncrypted(pwd);
				PassPhraseCiphering.logger.info("password '{}'", uecPWD);
			}
		} else {
			for (String pwd : passwords) {
				String ecPWD = passwordEncoding.encrypt(pwd);
				PassPhraseCiphering.logger.info("password '{}' is crypted '{}'", pwd, ecPWD);
			}
		}
	}
}
