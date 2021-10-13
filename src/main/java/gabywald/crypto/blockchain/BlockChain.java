package gabywald.crypto.blockchain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Gabriel Chandesris (2021)
 */
public abstract class BlockChain {
	
	public static List<Block> build() {
		return new ArrayList<Block>();
	}

	public static Boolean isChainValidV1(List<Block> blockchain) {
		Block currentBlock; 
		Block previousBlock;

		// Loop through blockchain to check hashes:
		for (int i = 1 ; i < blockchain.size() ; i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			// Compare registered hash and calculated hash:
			if ( ! currentBlock.hash.equals(currentBlock.calculateHash()) ) {
				System.out.println("Current Hashes not equal");			
				return false;
			}
			// Compare previous hash and registered previous hash
			if ( ! previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
		}
		return true;
	}
	
	public static boolean isChainValidV2(List<Block> blockchain, Transaction genesisTransaction, int difficulty) {
		Block currentBlock = null; 
		Block previousBlock = null;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		// A temporary working list of unspent transactions at a given block state.
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>();
		tempUTXOs.put(genesisTransaction.getOutputs().get(0).id, genesisTransaction.getOutputs().get(0));

		// Loop through blockchain to check hashes:
		for (int i = 1 ; i < blockchain.size() ; i++) {

			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
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
			if ( ! currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}

			// Loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for (int t = 0 ; t < currentBlock.transactions.size() ; t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);

				if ( ! currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}

				for (TransactionInput input: currentTransaction.getInputs()) {	
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

				for (TransactionOutput output: currentTransaction.getOutputs()) {
					tempUTXOs.put(output.id, output);
				}

				if (currentTransaction.getOutputs().get(0).reciepient != currentTransaction.getRecipient()) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if (currentTransaction.getOutputs().get(1).reciepient != currentTransaction.getSender()) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid");
		return true;
	}

	public static boolean addBlock(final List<Block> blockchain, final Block newBlock, final int difficulty) {
		newBlock.mineBlock(difficulty);
		return blockchain.add(newBlock);
	}
}
