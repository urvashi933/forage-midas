package com.jpmc.midascore.service.impl;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.payload.TransactionResponse;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public TransactionResponse transfer(Long senderID, Long receiverID, float amount) {  // ❌ FIX 1: float not Float

        UserRecord sender = userRepository.findById(senderID).orElse(null);  // ❌ FIX 2: Simplify
        UserRecord recipient = userRepository.findById(receiverID).orElse(null);

        // ❌ FIX 3: Wrong logic - check BOTH users exist + sufficient balance
        if (sender == null || recipient == null || sender.getBalance() < amount || amount <= 0) {
            return new TransactionResponse(null, true);
        }

        float newSenderBalance = sender.getBalance() - amount;  // ❌ FIX 4: Direct calculation
        float newRecipientBalance = recipient.getBalance() + amount;

        sender.setBalance(newSenderBalance);
        recipient.setBalance(newRecipientBalance);
        
        userRepository.save(sender);
        userRepository.save(recipient);

        return new TransactionResponse(new Transaction(senderID, receiverID, amount), false);
    }

    // ✅ Keep deposit/withdraw if used elsewhere, but transfer doesn't need them
    public float deposit(UserRecord receiver, float amount) {
        return receiver.getBalance() + amount;
    }

    public float withdraw(UserRecord sender, float amount) {
        return sender.getBalance() - amount;
    }
}
