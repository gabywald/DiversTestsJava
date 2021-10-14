package gabywald.crypto.blockchain;

import java.security.Security;
import java.util.List;
import java.util.ArrayList;

// TODO replace GsonBuilder !! (build json from objet !)
import com.google.gson.GsonBuilder;

/**
 * NoobChain Test and Main Class of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa">https://medium.com/programmers-blockchain/create-simple-blockchain-java-tutorial-from-scratch-6eeed3cb03fa</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-1">https://github.com/CryptoKass/NoobChain-Tutorial-Part-1</a>
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public class NoobChainIntegration {

	private static List<Block> blockchain = new ArrayList<Block>(); 
	private static List<Block> blockchain2 = new ArrayList<Block>();

	private static int difficulty = 3;
	
	private static Wallet walletA;
	private static Wallet walletB;
	
	public static void main(String[] args) {

		// ***** Part I
		Block genesisBlock = new Block("Hi im the first block", "0");
		System.out.println("Hash for block 1 : " + genesisBlock.getHash());
		Block secondBlock = new Block("Yo im the second block", genesisBlock.getHash());
		System.out.println("Hash for block 2 : " + secondBlock.getHash());
		Block thirdBlock = new Block("Hey im the third block", secondBlock.getHash());
		System.out.println("Hash for block 3 : " + thirdBlock.getHash());


		// ***** Part II
		// Add our blocks to the blockchain List:
		NoobChainIntegration.blockchain.add(new Block("Hi im the first block", "0"));		
		NoobChainIntegration.blockchain.add(new Block("Yo im the second block", NoobChainIntegration.blockchain.get(NoobChainIntegration.blockchain.size()-1).getHash() )); 
		NoobChainIntegration.blockchain.add(new Block("Hey im the third block",  NoobChainIntegration.blockchain.get(NoobChainIntegration.blockchain.size()-1).getHash() ));
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(NoobChainIntegration.blockchain);		
		System.out.println(blockchainJson);

		// ***** Part III
		// Add our blocks to the blockchain List:
		NoobChainIntegration.blockchain2.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		NoobChainIntegration.blockchain2.get(0).mineBlock(NoobChainIntegration.difficulty);

		NoobChainIntegration.blockchain2.add(new Block("Yo im the second block", NoobChainIntegration.blockchain2.get(NoobChainIntegration.blockchain2.size()-1).getHash() ));
		System.out.println("Trying to Mine block 2... ");
		NoobChainIntegration.blockchain2.get(1).mineBlock(NoobChainIntegration.difficulty);

		NoobChainIntegration.blockchain2.add(new Block("Hey im the third block", NoobChainIntegration.blockchain2.get(NoobChainIntegration.blockchain2.size()-1).getHash() ));
		System.out.println("Trying to Mine block 3... ");
		NoobChainIntegration.blockchain2.get(2).mineBlock(NoobChainIntegration.difficulty);

		System.out.println("\nBlockchain is Valid: " + BlockChain.isChainValidV1( NoobChainIntegration.blockchain2 ));

		String blockchain2Json = new GsonBuilder().setPrettyPrinting().create().toJson(NoobChainIntegration.blockchain2);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchain2Json);

		// ***** Part IV
		// Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		// Security.addProvider(new sun.security.provider.Sun());
		// XXX NOTE see https://docs.oracle.com/javase/8/docs/technotes/guides/security/SunProviders.html
		// Create the new wallets
		NoobChainIntegration.walletA = new Wallet();
		NoobChainIntegration.walletB = new Wallet();
		// Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtils.getStringFromKey(NoobChainIntegration.walletA.privateKey));
		System.out.println(StringUtils.getStringFromKey(NoobChainIntegration.walletA.publicKey));
		// Create a test transaction from WalletA to walletB 
		Transaction transaction = new Transaction(NoobChainIntegration.walletA.publicKey, NoobChainIntegration.walletB.publicKey, 5, null);
		transaction.generateSignature(NoobChainIntegration.walletA.privateKey);
		// Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(transaction.verifySignature());
		
		// ***** Part V
		// XXX NOTE see NoobChainFinale and NoobChainTests !!
	}

	/*
Hash for block 1 : 9f98b14417f65bf7f55053d6ee948de264beaf4f4effdf1be1b80adb939a56bb
Hash for block 2 : e7571024da930e1fee1dad719e0b8eeeefdf80c70a3a3739d83d9de1e23eced8
Hash for block 3 : 7f18457511c26228726b46cb99248113973cb695df6918d78b1402c77435d003
[
  {
    "hash": "b9e3632117413cb710d39c3a449747b6beeeb2b524f26c05882c3bd6af9d74e5",
    "previousHash": "0",
    "data": "Hi im the first block",
    "timeStamp": 1633963207796
  },
  {
    "hash": "7e27f719f013fec598427375fe7b880a5630ceaa901ee03348c17987cb4d392b",
    "previousHash": "b9e3632117413cb710d39c3a449747b6beeeb2b524f26c05882c3bd6af9d74e5",
    "data": "Yo im the second block",
    "timeStamp": 1633963207796
  },
  {
    "hash": "e12c29de9bbb838e5851d5d8ce59cac2e6225192c8e6b3afde054803e5483ee4",
    "previousHash": "7e27f719f013fec598427375fe7b880a5630ceaa901ee03348c17987cb4d392b",
    "data": "Hey im the third block",
    "timeStamp": 1633963207796
  }
]
	 */

	/*
Trying to Mine block 1... 
Block Mined!!! : 00000206cc43b6e9b1e4d81cc5e2959296c0e1b90db2b0e509ef7fc3ec04095d
Trying to Mine block 2... 
Block Mined!!! : 0000091a52ce1847ea50156a6cb4a5fd479fa0575f39a95011968c0b682cb5d9
Trying to Mine block 3... 
Block Mined!!! : 0000043ba1e8397d791066bd0b9122339e03135abb451ccbf8b0efc37dc3a109

Blockchain is Valid: true

The block chain: 
[
  {
    "hash": "00000206cc43b6e9b1e4d81cc5e2959296c0e1b90db2b0e509ef7fc3ec04095d",
    "previousHash": "0",
    "data": "Hi im the first block",
    "timeStamp": 1633963490480,
    "nonce": 285549
  },
  {
    "hash": "0000091a52ce1847ea50156a6cb4a5fd479fa0575f39a95011968c0b682cb5d9",
    "previousHash": "00000206cc43b6e9b1e4d81cc5e2959296c0e1b90db2b0e509ef7fc3ec04095d",
    "data": "Yo im the second block",
    "timeStamp": 1633963491272,
    "nonce": 3329949
  },
  {
    "hash": "0000043ba1e8397d791066bd0b9122339e03135abb451ccbf8b0efc37dc3a109",
    "previousHash": "0000091a52ce1847ea50156a6cb4a5fd479fa0575f39a95011968c0b682cb5d9",
    "data": "Hey im the third block",
    "timeStamp": 1633963497804,
    "nonce": 3752258
  }
]
	 */
}

