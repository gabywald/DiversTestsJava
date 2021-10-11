package gabywald.crypto.blockchain;

import java.util.Date;

/**
 * Block of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa">https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-1">https://github.com/CryptoKass/NoobChain-Tutorial-Part-1</a>
 * @author Gabriel Chandesris (2021)
 */
public class Block {

	public String hash;
	public String previousHash; 
	private String data; //our data will be a simple message.
	private long timeStamp; //as number of milliseconds since 1/1/1970.
	private int nonce;

	// Block Constructor.  
	public Block(String data,String previousHash ) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();

		// Making sure we do this after we set the other values.
		this.hash = calculateHash(); 
	}

	// Calculate new hash based on blocks contents
	public String calculateHash() {
		String calculatedhash = StringUtils.applySha256( 
				this.previousHash +
				Long.toString(this.timeStamp) +
				Integer.toString(this.nonce) + 
				this.data 
				);
		return calculatedhash;
	}

	public void mineBlock(int difficulty) {
		// Create a string with difficulty * "0"
		String target = new String(new char[difficulty]).replace('\0', '0'); 
		while ( ! this.hash.substring( 0, difficulty).equals(target)) {
			this.nonce++;
			this.hash = this.calculateHash();
		}
		System.out.println("Block Mined!!! : " + this.hash);
	}
}
