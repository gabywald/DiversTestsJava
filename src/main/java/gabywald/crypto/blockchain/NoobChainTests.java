package gabywald.crypto.blockchain;

import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;

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
		System.out.println("Hash for block 1 : " + genesisBlock.hash);
		Assertions.assertNotNull( genesisBlock.hash );
		
		Block secondBlock = new Block("Yo im the second block", genesisBlock.hash);
		Assertions.assertNotNull( secondBlock );
		System.out.println("Hash for block 2 : " + secondBlock.hash);
		Assertions.assertNotNull( secondBlock.hash );
		
		Block thirdBlock = new Block("Hey im the third block", secondBlock.hash);
		Assertions.assertNotNull( thirdBlock );
		System.out.println("Hash for block 3 : " + thirdBlock.hash);
		Assertions.assertNotNull( thirdBlock.hash );
	}
	
	/**
	 * Test : blockchain
	 */
	@Test
	void testPart02() {
		List<Block> blockchain = BlockChain.build(); 
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", "0"));		
		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).hash)); 
		blockchain.add(new Block("Hey im the third block",  blockchain.get(blockchain.size()-1).hash));
		Assertions.assertEquals(3,  blockchain.size());
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		Assertions.assertNotNull( blockchainJson );
		System.out.println(blockchainJson);
	}
	
	@Test
	void testPart03() {
		List<Block> blockchain = BlockChain.build(); 
		int difficulty = 3;
		// Add our blocks to the blockchain List:
		blockchain.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		blockchain.get(0).mineBlock(difficulty);

		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine block 2... ");
		blockchain.get(1).mineBlock(difficulty);

		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).hash));
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
		
		List<Block> blockchain = BlockChain.build();
		
		Map<String, TransactionOutput> mapUTXOs = new HashMap<String, TransactionOutput>();
		
		int difficulty = 3;
		float minimumTransaction = 0.1f;
		
		// Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create wallets:
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();		
		Wallet coinbase = new Wallet();

		// Create genesis transaction, which sends 100 NoobCoin to walletA: 
		Transaction genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		Assertions.assertNotNull( genesisTransaction );
		// Manually sign the genesis transaction
		genesisTransaction.generateSignature(coinbase.privateKey);
		// Manually set the transaction id
		genesisTransaction.setTransactionId( "0" );
		// Manually add the Transactions Output
		genesisTransaction.getOutputs().add(new TransactionOutput(	genesisTransaction.getRecipient(), 
																	genesisTransaction.getValue(), 
																	genesisTransaction.getTransactionId() ));
		
		// Its important to store our first transaction in the UTXOs list.
		mapUTXOs.put(genesisTransaction.getOutputs().get(0).id, genesisTransaction.getOutputs().get(0));
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction, mapUTXOs, minimumTransaction);
		BlockChain.addBlock(blockchain, genesis, difficulty);

		// Testing
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance( mapUTXOs ));
		
		Assertions.assertEquals(100, walletA.getBalance( mapUTXOs ));
		
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		boolean bBlock1AddTransactionResult = block1.addTransaction(	walletA.sendFunds(walletB.publicKey, 40f, mapUTXOs), 
																		mapUTXOs, minimumTransaction) ;
		Assertions.assertTrue( bBlock1AddTransactionResult );
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block1, difficulty) );
		System.out.println("\nWalletA's balance is: " + walletA.getBalance( mapUTXOs ));
		System.out.println("WalletB's balance is: " + walletB.getBalance( mapUTXOs ));
		
		Assertions.assertEquals(60, walletA.getBalance( mapUTXOs ));
		Assertions.assertEquals(40, walletB.getBalance( mapUTXOs ));
		
		boolean isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		boolean bBlock2AddTransactionResult = block2.addTransaction(	walletA.sendFunds(walletB.publicKey, 1000f, mapUTXOs), 
																		mapUTXOs, minimumTransaction);
		Assertions.assertFalse( bBlock2AddTransactionResult);
		Assertions.assertTrue( BlockChain.addBlock(blockchain, block2, difficulty) );
		System.out.println("\nWalletA's balance is: " + walletA.getBalance( mapUTXOs ));
		System.out.println("WalletB's balance is: " + walletB.getBalance( mapUTXOs ));
		
		Assertions.assertEquals(60, walletA.getBalance( mapUTXOs ));
		Assertions.assertEquals(40, walletB.getBalance( mapUTXOs ));
		
		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );

		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		Assertions.assertTrue( 
				block3.addTransaction(	walletB.sendFunds( walletA.publicKey, 20, mapUTXOs), 
										mapUTXOs, minimumTransaction) );
		System.out.println("\nWalletA's balance is: " + walletA.getBalance( mapUTXOs ));
		System.out.println("WalletB's balance is: " + walletB.getBalance( mapUTXOs ));
		
		Assertions.assertEquals(80, walletA.getBalance( mapUTXOs ));
		Assertions.assertEquals(20, walletB.getBalance( mapUTXOs ));

		isChainValid = BlockChain.isChainValidV2( blockchain, genesisTransaction, difficulty );
		Assertions.assertTrue( isChainValid );
	}

}
