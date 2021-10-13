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

	public static List<Block> blockchain = new ArrayList<Block>();
	public static Map<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();

	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;

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
		NoobChainFinale.genesisTransaction.transactionId = "0";
		// Manually add the Transactions Output
		NoobChainFinale.genesisTransaction.outputs.add(new TransactionOutput(	NoobChainFinale.genesisTransaction.reciepient, 
																				NoobChainFinale.genesisTransaction.value, 
																				NoobChainFinale.genesisTransaction.transactionId));
		
		// Its important to store our first transaction in the UTXOs list.
		UTXOs.put(NoobChainFinale.genesisTransaction.outputs.get(0).id, NoobChainFinale.genesisTransaction.outputs.get(0));

		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(NoobChainFinale.genesisTransaction);
		NoobChainFinale.addBlock(genesis);

		// Testing
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		NoobChainFinale.addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		NoobChainFinale.addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		NoobChainFinale.isChainValid();
	}

	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		// A temporary working list of unspent transactions at a given block state.
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>();
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		// Loop through blockchain to check hashes:
		for (int i = 1 ; i < NoobChainFinale.blockchain.size() ; i++) {

			currentBlock = NoobChainFinale.blockchain.get(i);
			previousBlock = NoobChainFinale.blockchain.get(i-1);
			
			// Compare registered hash and calculated hash:
			if ( ! currentBlock.hash.equals(currentBlock.calculateHash()) ) {
				System.out.println("#Current Hashes not equal");
				return false;
			}
			
			// Compare previous hash and registered previous hash
			if ( ! previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}
			
			// Check if hash is solved
			if ( ! currentBlock.hash.substring( 0, NoobChainFinale.difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			// Loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for (int t = 0 ; t < currentBlock.transactions.size() ; t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);

				if ( ! currentTransaction.verifiySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}

				for (TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}

					if (input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);
				}

				for (TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}

				if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid");
		return true;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		NoobChainFinale.blockchain.add(newBlock);
	}
}
