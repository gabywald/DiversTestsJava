package gabywald.crypto.blockchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

/**
 * Wallet of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2/tree/master/src/noobchain">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2/tree/master/src/noobchain</a>
 * @author Gabriel Chandesris (2021)
 */
public class Wallet {

	public PrivateKey privateKey;
	public PublicKey publicKey;

	public Wallet() {
		generateKeyPair();	
	}

	public void generateKeyPair() {
		try {
			// TODO learning how security works here !
			KeyPairGenerator keyGen = // KeyPairGenerator.getInstance(StringUtil.ECDSA, StringUtil.BC);
					KeyPairGenerator.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair : 256 bytes provides an acceptable security level
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the keyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch(Exception e) { 
			throw new RuntimeException(e);
		}
	}

}

