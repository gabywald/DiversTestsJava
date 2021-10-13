package gabywald.crypto.blockchain;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Transaction of BlockChain.
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2/tree/master/src/noobchain">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2/tree/master/src/noobchain</a>
 * @author Gabriel Chandesris (2021)
 */
public class Transaction {
	/** This is also the hash of the transaction. */
	public String transactionId;
	/** senders address/public key. */
	public PublicKey sender;
	/** Recipients address/public key. */
	public PublicKey reciepient;
	public float value;
	/** This is to prevent anybody else from spending funds in our wallet. */
	public byte[] signature;

	public List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0; // a rough count of how many transactions have been generated. 

	// Constructor: 
	public Transaction(PublicKey from, PublicKey to, float value,  List<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	/** 
	 * This Calculates the transaction hash (which will be used as its Id)
	 * @return
	 */
	private String calculateHash() {
		// Increase the sequence to avoid 2 identical transactions having the same hash
		Transaction.sequence++;
		try {
			return StringUtils.applySha256(
					StringUtils.getStringFromKey(this.sender) +
					StringUtils.getStringFromKey(this.reciepient) +
					Float.toString(value) + Transaction.sequence
					);
		} catch (BlockchainException e) {
			// e.printStackTrace();
			System.out.println( e.getMessage() );
			return null;
		}
	}

	/** 
	 * Applies ECDSA Signature and returns the result ( as bytes ). 
	 * @param privateKey
	 * @param input
	 * @return
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance(StringUtils.ECDSA, StringUtils.BC);
			// Signature.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	/** 
	 * Verifies a String signature. 
	 * @param publicKey
	 * @param data
	 * @param signature
	 * @return
	 */
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance(StringUtils.ECDSA, StringUtils.BC);
			// Signature.getInstance(StringUtils.CipherAlgorithm, StringUtils.CipherProvider);
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/** 
	 * Signs all the data we don't wish to be tampered with.
	 * @param privateKey
	 */
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtils.getStringFromKey(this.sender) 
				+ StringUtils.getStringFromKey(this.reciepient) 
				+ Float.toString(this.value);
		try {
			this.signature = StringUtils.applyECDSASig(privateKey,data);
		} catch (BlockchainException e) {
			// e.printStackTrace();
			System.out.println( e.getMessage() );
			this.signature = null;
		}		
	}

	/**
	 * Verifies the data we signed hasn't been tampered with. 
	 * @return
	 */
	public boolean verifiySignature() {
		String data = StringUtils.getStringFromKey(this.sender) 
				+ StringUtils.getStringFromKey(this.reciepient) 
				+ Float.toString(this.value);
		return StringUtils.verifyECDSASig(this.sender, data, this.signature);
	}

	/** 
	 * Returns true if new transaction could be created.	
	 * @return
	 */
	public boolean processTransaction() {

		if (verifiySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		// Gather transaction inputs (Make sure they are unspent):
		inputs.stream().forEach(ti -> ti.UTXO = NoobChain.UTXOs.get(ti.transactionOutputId) );

		// Check if transaction is valid:
		if (this.getInputsValue() < NoobChain.minimumTransaction) {
			System.out.println("#Transaction Inputs to small: " + getInputsValue());
			return false;
		}

		// Generate transaction outputs:
		float leftOver = this.getInputsValue() - value; // 
		// - Get value of inputs then the left over change:
		this.transactionId = this.calculateHash();
		// - Send value to recipient
		this.outputs.add(new TransactionOutput( this.reciepient, this.value, this.transactionId));
		// - Send the left over 'change' back to sender
		this.outputs.add(new TransactionOutput( this.sender, leftOver, this.transactionId));	

		// Add outputs to Unspent list
		this.outputs.stream().forEach(to -> NoobChain.UTXOs.put(to.id , to) );

		// Remove transaction inputs from UTXO lists as spent:
		this.inputs.stream().forEach(ti -> {
			// If Transaction can't be found skip it
			if ( ti.UTXO != null) 
				{ NoobChain.UTXOs.remove(ti.UTXO.id); }
		} );

		return true;
	}

	/**
	 * Returns sum of inputs(UTXOs) values. 
	 * @return
	 */
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput ti : inputs) {
			// If Transaction can't be found skip it
			if ( ti.UTXO == null)  { continue; } 
			total += ti.UTXO.value;
		}
		return total;
	}

	/**
	 * Returns sum of outputs:
	 * @return
	 */
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput to : outputs) {
			total += to.value;
		}
		return total;
	}

}
