package gabywald.crypto.blockchain;

/**
 * TransactionInput of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public class TransactionInput {
	/** Reference to TransactionOutputs -> transactionId */
	public String transactionOutputId;
	/** Contains the Unspent transaction output */
	public TransactionOutput UTXO;
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
