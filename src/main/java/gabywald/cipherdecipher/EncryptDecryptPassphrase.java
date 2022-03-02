package gabywald.cipherdecipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptDecryptPassphrase {
    private static final Logger logger = LoggerFactory.getLogger(EncryptDecryptPassphrase.class);

    public static void main(String[] args) throws Exception {
        String passphrase			= "passPhrase";
        logger.info( "Encrypt {} ==> {}", passphrase, PassphraseEncoding.encrypt(passphrase));

        String encryptedPassphrase	= "lpHlmgiEExsrblbSkFluHg==";
        logger.info("Decrypt {} ==> {}", encryptedPassphrase, PassphraseEncoding.decrypt(encryptedPassphrase));
    }
}
