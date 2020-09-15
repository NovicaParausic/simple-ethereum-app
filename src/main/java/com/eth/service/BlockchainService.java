package com.eth.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import com.eth.model.BlockchainTransaction;

@Service
public class BlockchainService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainService.class);
	
	private final Web3j web3j;

	public BlockchainService(Web3j web3j) {
		this.web3j = web3j;
	}
	
	//sending ethereum transaction to account specified in trx 
	public BlockchainTransaction process(BlockchainTransaction trx) throws IOException {
		
		EthAccounts accounts = web3j.ethAccounts().send();
		EthGetTransactionCount transactionCount = 
				web3j.ethGetTransactionCount(accounts.getAccounts().get(trx.getFromId()), DefaultBlockParameterName.LATEST).send();
		
		Transaction transaction = Transaction.createEtherTransaction(
				accounts.getAccounts().get(trx.getFromId()), transactionCount.getTransactionCount(), BigInteger.valueOf(trx.getValue()), 
				BigInteger.valueOf(21_000), accounts.getAccounts().get(trx.getToId()), BigInteger.valueOf(trx.getValue()));
	
		EthSendTransaction response = web3j.ethSendTransaction(transaction).send();
		
		if (response.getError() != null) {
			trx.setAccepted(false);
			LOGGER.warn("Tx rejected: {}", response.getError().getMessage());
			return trx;
		}
		
		trx.setAccepted(true);
		String txHash = response.getTransactionHash();
		LOGGER.info("Tx hash: {}", txHash);
		
		trx.setId(txHash);
		EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash).send();
		
		receipt.getTransactionReceipt().ifPresent(txr -> LOGGER.info("Tx receipt: {}", txr.getCumulativeGasUsed().intValue()));
				
		return trx;
	}
	
	//geting all acounts
	public List<String> getAccounts() throws IOException {
		
		EthAccounts accounts = web3j.ethAccounts().send();
		return accounts.getAccounts();
	}
}
