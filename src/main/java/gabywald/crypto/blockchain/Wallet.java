package gabywald.crypto.blockchain;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wallet of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public class Wallet {

	public PrivateKey privateKey = null;
	public PublicKey publicKey = null;

	public Wallet() {
		this.generateKeyPair();	
	}

	/**
	 * Generate some KeyPair (Public and Private). 
	 */
	public void generateKeyPair() {
		try {
			// TODO learning how security works here !
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(StringUtils.ECDSA, StringUtils.BC);
			// KeyPairGenerator keyGen = KeyPairGenerator.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair : 256 bytes provides an acceptable security level
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the keyPair
			this.privateKey = keyPair.getPrivate();
			this.publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException nsae) {
			// throw new BlockchainException("StringUtils: NoSuchAlgorithmException: " + nsae.getMessage());
			System.out.println( "StringUtils: NoSuchAlgorithmException: " + nsae.getMessage() );
		} catch (NoSuchProviderException nspe) {
			// throw new BlockchainException("StringUtils: NoSuchProviderException: " + nspe.getMessage());
			System.out.println( "StringUtils: NoSuchProviderException: " + nspe.getMessage() );
		} catch (InvalidAlgorithmParameterException iape) {
			// throw new BlockchainException("StringUtils: InvalidAlgorithmParameterException: " + iape.getMessage());
			System.out.println( "StringUtils: InvalidAlgorithmParameterException: " + iape.getMessage() );
		} 
	}

	/**
	 * Returns balance and stores the UTXO's owned by this wallet in this.UTXOs
	 * @return
	 */
	public float getBalance() {
		float total = 0;	
		for (Map.Entry<String, TransactionOutput> item: NoobChainFinale.UTXOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			if (UTXO.isMine(publicKey)) {
				// If output belongs to me ( if coins belong to me )
				// Add it to our list of unspent transactions.
				NoobChainFinale.UTXOs.put(UTXO.id,UTXO);
				total += UTXO.value ; 
			}
		}  
		return total;
	}

	/** 
	 * Generates and returns a new transaction from this wallet.
	 * @param recipient
	 * @param value
	 * @return
	 */
	public Transaction sendFunds(PublicKey recipient, float value) {
		// Gather balance and check funds.
		if (this.getBalance() < value) {
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}
		// Create array list of inputs
		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		float total = 0;
		for (Map.Entry<String, TransactionOutput> item: NoobChainFinale.UTXOs.entrySet()){
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if (total > value)  { break; }
		}

		Transaction newTransaction = new Transaction(this.publicKey, recipient , value, inputs);
		newTransaction.generateSignature(this.privateKey);

		for (TransactionInput input: inputs) {
			NoobChainFinale.UTXOs.remove(input.transactionOutputId);
		}
		return newTransaction;
	}

}

