package com.eth.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eth.model.BlockchainTransaction;
import com.eth.service.BlockchainService;

@RestController
public class BlockchainController {

	private final BlockchainService service;

	public BlockchainController(BlockchainService service) {
		this.service = service;
	}
	
	@GetMapping("/accounts")
	public List<String> getAccounts() throws IOException {
		
		return service.getAccounts();
	}
	
	@PostMapping("/transaction")
	public BlockchainTransaction execute(@RequestBody BlockchainTransaction transaction) throws IOException {

		return service.process(transaction);
	}
}
