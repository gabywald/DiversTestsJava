package gabywald.cipherdecipher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Gabriel Chandesris (2021)
 */
class PassPhraseCipheringTests {

	private static final Logger logger = LoggerFactory.getLogger(PassPhraseCipheringTests.class);

	@Test
	void testMain() throws FileNotFoundException, IOException {
		// ***** Test without arguments !!
		PassPhraseCiphering.main(new String[0]);
		// **** 
		Properties props = new Properties();
		props.load(new FileInputStream("src/test/resources/cipheringtests.properties"));
		String passPhrase = props.getProperty("totest.passphrase");
		Assertions.assertNotNull(passPhrase);
		String passwords = props.getProperty("totest.passwords");
		Assertions.assertNotNull(passwords);
		String attempteds = props.getProperty("totest.attempted");
		Assertions.assertNotNull(attempteds);
		// ***** Test with arguments : ENCODE !!
		List<String> arguments = new ArrayList<String>();
		arguments.add(passPhrase);
		arguments.addAll(Arrays.asList(passwords.split(";")));
		PassPhraseCiphering.main(arguments.toArray(new String[0]));
		// ***** Test with arguments : DECODE !!
		List<String> argumentsDecode = new ArrayList<String>();
		argumentsDecode.add(passPhrase);
		argumentsDecode.add("-d");
		argumentsDecode.addAll(Arrays.asList(attempteds.split(";")));
		PassPhraseCiphering.main(argumentsDecode.toArray(new String[0]));
		// ***** Comparisons to ENCODE
		String ecPassPhrase = PassphraseEncoding.encryptQuietly(passPhrase);
		Assertions.assertNotNull(ecPassPhrase);
		PasswordEncoding passwordEncoding = new PasswordEncoding(Arrays.asList(ecPassPhrase));
		Assertions.assertNotNull(passwordEncoding);
		String[] dataPWDs = passwords.split(";");
		String[] dataATTs = attempteds.split(";");
		Assertions.assertEquals(dataPWDs.length, dataATTs.length);
		for (int i = 0; i < dataPWDs.length; i++) {
			String pwd = dataPWDs[i];
			String att = dataATTs[i];
			String uecPWD = passwordEncoding.encrypt(pwd);
			Assertions.assertNotNull(uecPWD);
			PassPhraseCipheringTests.logger.info("comparison {} ?==? {}", uecPWD, att);
			// Assertions.assertEquals(uecPWD, att);
		}
		// ***** Comparisons to DECODE
		Assertions.assertNotNull(ecPassPhrase);
		Assertions.assertNotNull(passwordEncoding);
		Assertions.assertEquals(dataPWDs.length, dataATTs.length);
		for (int i = 0; i < dataPWDs.length; i++) {
			String pwd = dataPWDs[i];
			String att = dataATTs[i];
			String uecPWD = passwordEncoding.decryptOrReadIfNotEncrypted(att);
			Assertions.assertNotNull(uecPWD);
			PassPhraseCipheringTests.logger.info("comparison {} ?==? {}", uecPWD, pwd);
			Assertions.assertEquals(uecPWD, pwd);
		}
	}
}
