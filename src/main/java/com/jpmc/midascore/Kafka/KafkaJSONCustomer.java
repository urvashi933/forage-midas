package com.jpmc.midascore.kafka;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.payload.TransactionResponse;
import com.jpmc.midascore.service.RestTemplateService;
import com.jpmc.midascore.service.TransactionService;
import com.jpmc.midascore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;  // ❌ FIX 1: Add @Transactional

@Component
public class KafkaJSONConsumer {  // ✅ Good name

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJSONConsumer.class);  // ❌ FIX 2: Wrong class name

    @Autowired private TransactionService transactionService;
    @Autowired private UserService userService;
    @Autowired private RestTemplateService restTemplateService;
    @Autowired private DatabaseConduit databaseConduit;

    @KafkaListener(topics = "${general.kafka-topic}")  // ❌ FIX 3: "trader-updates" not "transactions"
    @Transactional  // ❌ FIX 4: Add for atomic DB operations
    public void consumeTransaction(Transaction transaction) {  // ❌ FIX 5: void, not TransactionResponse
        LOGGER.info("Received transaction: {}", transaction);

        // Validate sender balance BEFORE transfer
        UserRecord sender = userService.getUserByID(transaction.getSenderId());
        UserRecord recipient = userService.getUserByID(transaction.getRecipientId());
        
        if (sender != null && recipient != null && sender.getBalance() >= transaction.getAmount()) {
            // Transfer funds
            TransactionResponse response = transactionService.transfer(
                transaction.getSenderId(),
                transaction.getRecipientId(),
                transaction.getAmount());

            if (!response.getError()) {
                // Get incentive
                Incentive incentive = restTemplateService.postTransaction(transaction);
                
                // Persist with incentive
                float incentiveAmount = incentive != null ? incentive.getAmount() : 0.0f;
                transaction.setIncentive(incentive);
                
                // Update balances (incentive to recipient only)
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);
                
                databaseConduit.save(sender);
                databaseConduit.save(recipient);
            }
        }
    }
}
