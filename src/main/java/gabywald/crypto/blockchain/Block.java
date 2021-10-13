package gabywald.crypto.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Block of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa">https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-1">https://github.com/CryptoKass/NoobChain-Tutorial-Part-1</a>
 * @author Gabriel Chandesris (2021)
 */
public class Block {

	public String hash;
	public String previousHash; 
	/** our data will be a simple message. */
	private String data;
	/** as number of milliseconds since 1/1/1970. */
	private long timeStamp;
	private int nonce;

	public String merkleRoot;
	/** Our data will be a simple message. */
	public List<Transaction> transactions = new ArrayList<Transaction>(); 

	/**
	 * Block Constructor. 
	 * @param data
	 * @param previousHash
	 */
	public Block(String data, String previousHash ) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		// Making sure we do this after we set the other values.
		this.hash = this.calculateHash();
	}
	
	/**
	 * Block Constructor. 
	 * @param previousHash
	 */
	public Block(String previousHash ) {
		this(null, previousHash);
	}

	/**
	 * Calculate new hash based on blocks contents
	 * @return (null if exception apply internally). 
	 * @see StringUtils#applySha256(String)
	 */
	public String calculateHash() {
		String calculatedhash = null;
		try {
			calculatedhash = StringUtils.applySha256( 
					previousHash +
					Long.toString(timeStamp) +
					Integer.toString(nonce) + 
					merkleRoot
					);
		} catch (BlockchainException e) {
			// e.printStackTrace();
			System.out.println( e.getMessage() );
			calculatedhash = null;
		}
		return calculatedhash;
	}

	public void mineBlock(int difficulty) {
		this.merkleRoot = StringUtils.getMerkleRoot(this.transactions);
		// Create a string with difficulty * "0"
		String target = StringUtils.getDificultyString(difficulty);
		while ( (this.hash == null) || ( ! this.hash.substring( 0, difficulty ).equals(target)) ) {
			this.nonce++;
			this.hash = this.calculateHash();
			// XXX NOTE if excpetion inside calculateHash : hash will be null !
		}
		System.out.println("Block Mined!!! : " + this.hash);
	}

	/**
	 * Add transactions to this block. 
	 * @param transaction
	 * @return
	 */
	public boolean addTransaction(Transaction transaction) {
		// Process transaction and check if valid, unless block is genesis block then ignore.
		if (transaction == null) { return false; }		
		if (previousHash != "0") {
			if (transaction.processTransaction() != true) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}
}
