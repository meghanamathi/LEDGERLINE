
package com.ledgerline.controller;

import com.ledgerline.dto.TransferRequest;
import com.ledgerline.entity.Transfer;
import com.ledgerline.service.TransferService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createTransfer(
            @Valid @RequestBody TransferRequest request) {

        Transfer transfer = transferService.transferMoney(request);

        return Map.of(
            "transferId", transfer.getId(),
            "senderAccount", transfer.getSender().getAccountNumber(),
            "receiverAccount", transfer.getReceiver().getAccountNumber(),
            "amount", transfer.getAmount(),
            "currency", transfer.getCurrency(),
            "status", transfer.getStatus(),
            "message", "Money transferred successfully"
        );
    }
}
