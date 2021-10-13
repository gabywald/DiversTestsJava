package gabywald.crypto.blockchain;

import java.security.PublicKey;

/**
 * TransactionOutput of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public class TransactionOutput {
	public String id;
	/** Also known as the new owner of these coins. */
	public PublicKey reciepient;
	/** The amount of coins they own */
	public float value;
	/** The id of the transaction this output was created in */
	public String parentTransactionId;
	
	/**
	 * Constructor
	 * @param reciepient
	 * @param value
	 * @param parentTransactionId
	 */
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		try {
			this.id = StringUtils.applySha256(StringUtils.getStringFromKey(reciepient) 
					+ Float.toString(value) + parentTransactionId);
			// TODO check alternatives for here when exception occurs !! (builder ?) : see StringUtils.applySha256 comment !
		} catch (BlockchainException e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
			this.id = null;
		}
	}
	
	/**
	 * Check if coin belongs to you. 
	 * @param publicKey
	 * @return
	 */
	public boolean isMine(PublicKey publicKey) {
		return this.reciepient.equals(publicKey);
		// return (publicKey == this.reciepient);
	}
	
}
