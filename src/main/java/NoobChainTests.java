import static org.junit.jupiter.api.Assertions.*;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;

import gabywald.crypto.blockchain.Block;
import gabywald.crypto.blockchain.BlockChain;
import gabywald.crypto.blockchain.StringUtils;
import gabywald.crypto.blockchain.Transaction;
import gabywald.crypto.blockchain.Wallet;

/**
 * Tests about BlockChain / NoobChain. 
 * @author Gabriel Chandesris (2021)
 */
class NoobChainTests {

	/**
	 * Test : block. 
	 */
	@Test
	void testPart01() {
		Block genesisBlock = new Block("Hi im the first block", "0");
		Assertions.assertNotNull( genesisBlock );
		System.out.println("Hash for block 1 : " + genesisBlock.getHash() );
		Assertions.assertNotNull( genesisBlock.getHash() );
		
		Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash() );
		Assertions.assertNotNull( secondBlock );
		System.out.println("Hash for block 2 : " + secondBlock.getHash() );
		Assertions.assertNotNull( secondBlock.getHash() );
		
		Block thirdBlock = new Block("Hey im the third block", secondBlock.getHash() );
		Assertions.assertNotNull( thirdBlock );
		System.out.println("Hash for block 3 : " + thirdBlock.getHash() );
		Assertions.assertNotNull( thirdBlock.getHash() );
	}
	
	/**
	 * Test : blockchain
	 */
	@Test
	void testPart02() {
		List<Block> blockchain = new ArrayList<Block>(); 
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", "0"));		
		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() )); 
		blockchain.add(new Block("Hey im the third block",  blockchain.get(blockchain.size()-1).getHash() ));
		Assertions.assertEquals(3,  blockchain.size());
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		Assertions.assertNotNull( blockchainJson );
		System.out.println(blockchainJson);
	}
	
	@Test
	void testPart03() {
		List<Block> blockchain = new ArrayList<Block>();
		int difficulty = 3;
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		blockchain.get(0).mineBlock(difficulty);

		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash() ));
		System.out.println("Trying to Mine block 2... ");
		blockchain.get(1).mineBlock(difficulty);

		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).getHash() ));
		System.out.println("Trying to Mine block 3... ");
		blockchain.get(2).mineBlock(difficulty);
		
		Assertions.assertEquals(3,  blockchain.size());
		
		System.out.println("\nBlockchain is Valid: " + BlockChain.isChainValidV1( blockchain ));
		
		Assertions.assertTrue( BlockChain.isChainValidV1( blockchain ) );

		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		
		Assertions.assertNotNull( blockchainJson );
		
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
	}
	
	@Test
	void testPart04() {
		// Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		// Security.addProvider(new sun.security.provider.Sun());
		// XXX NOTE see https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html
		// Create the new wallets
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();
		Assertions.assertNotNull( walletA );
		Assertions.assertNotNull( walletB );
		// Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtils.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtils.getStringFromKey(walletA.publicKey));
		Assertions.assertNotNull( walletA.privateKey );
		Assertions.assertNotNull( walletA.publicKey );
		Assertions.assertNotNull( walletB.privateKey );
		Assertions.assertNotNull( walletB.publicKey );
		//Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		Assertions.assertNotNull( transaction );
		transaction.generateSignature(walletA.privateKey);
		Assertions.assertNotNull( walletA.privateKey );
		//Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(transaction.verifySignature());
		Assertions.assertTrue( transaction.verifySignature() );
	}
	
	@Test
	void testPart05() {
		fail("Not yet implemented");
	}

}
