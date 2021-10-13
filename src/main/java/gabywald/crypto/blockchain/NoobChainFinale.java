package gabywald.crypto.blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NoobChain Test and Main Class of BlockChain. 
 * <br/><a href="https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce">https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce</a>
 * <br/><a href="https://github.com/CryptoKass/NoobChain-Tutorial-Part-2">https://github.com/CryptoKass/NoobChain-Tutorial-Part-2</a>
 * @author Gabriel Chandesris (2021)
 */
public class NoobChainFinale {

	private static List<Block> blockchain = new ArrayList<Block>();
	private static Map<String, TransactionOutput> mapUTXOs = new HashMap<String, TransactionOutput>();

	private static int difficulty = 3;
	private static float minimumTransaction = 0.1f;
	private static Wallet walletA;
	private static Wallet walletB;
	private static Transaction genesisTransaction;

	public static void main(String[] args) {	
		// Add our blocks to the blockchain List:
		
		// Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		// Create wallets:
		NoobChainFinale.walletA = new Wallet();
		NoobChainFinale.walletB = new Wallet();		
		Wallet coinbase = new Wallet();

		// Create genesis transaction, which sends 100 NoobCoin to walletA: 
		NoobChainFinale.genesisTransaction = new Transaction(coinbase.publicKey, NoobChainFinale.walletA.publicKey, 100f, null);
		// Manually sign the genesis transaction
		NoobChainFinale.genesisTransaction.generateSignature(coinbase.privateKey);
		// Manually set the transaction id
		NoobChainFinale.genesisTransaction.setTransactionId( "0" );
		// Manually add the Transactions Output
		NoobChainFinale.genesisTransaction.getOutputs().add(new TransactionOutput(	NoobChainFinale.genesisTransaction.getRecipient(), 
																					NoobChainFinale.genesisTransaction.getValue(), 
																					NoobChainFinale.genesisTransaction.getTransactionId()));
		
		// Its important to store our first transaction in the UTXOs list.
		NoobChainFinale.mapUTXOs.put(NoobChainFinale.genesisTransaction.getOutputs().get(0).id, NoobChainFinale.genesisTransaction.getOutputs().get(0));

		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(NoobChainFinale.genesisTransaction, NoobChainFinale.mapUTXOs, NoobChainFinale.minimumTransaction);
		BlockChain.addBlock(NoobChainFinale.blockchain, genesis, NoobChainFinale.difficulty);

		// Testing
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + NoobChainFinale.walletA.getBalance( NoobChainFinale.mapUTXOs ));
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(	NoobChainFinale.walletA.sendFunds(NoobChainFinale.walletB.publicKey, 40f, NoobChainFinale.mapUTXOs), 
								NoobChainFinale.mapUTXOs, NoobChainFinale.minimumTransaction);
		BlockChain.addBlock(NoobChainFinale.blockchain, block1, NoobChainFinale.difficulty);
		System.out.println("\nWalletA's balance is: " + NoobChainFinale.walletA.getBalance( NoobChainFinale.mapUTXOs ));
		System.out.println("WalletB's balance is: " + NoobChainFinale.walletB.getBalance( NoobChainFinale.mapUTXOs ));

		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(	NoobChainFinale.walletA.sendFunds(NoobChainFinale.walletB.publicKey, 1000f, NoobChainFinale.mapUTXOs), 
								NoobChainFinale.mapUTXOs, NoobChainFinale.minimumTransaction);
		BlockChain.addBlock(NoobChainFinale.blockchain, block2, NoobChainFinale.difficulty);
		System.out.println("\nWalletA's balance is: " + NoobChainFinale.walletA.getBalance( NoobChainFinale.mapUTXOs ));
		System.out.println("WalletB's balance is: " + NoobChainFinale.walletB.getBalance( NoobChainFinale.mapUTXOs ));

		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(	NoobChainFinale.walletB.sendFunds( NoobChainFinale.walletA.publicKey, 20, NoobChainFinale.mapUTXOs), 
								NoobChainFinale.mapUTXOs, NoobChainFinale.minimumTransaction);
		System.out.println("\nWalletA's balance is: " + NoobChainFinale.walletA.getBalance( NoobChainFinale.mapUTXOs ));
		System.out.println("WalletB's balance is: " + NoobChainFinale.walletB.getBalance( NoobChainFinale.mapUTXOs ));

		BlockChain.isChainValidV2( NoobChainFinale.blockchain, NoobChainFinale.genesisTransaction, NoobChainFinale.difficulty );
	}

}
