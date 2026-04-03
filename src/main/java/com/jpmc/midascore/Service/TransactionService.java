package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.payload.TransactionResponse;

public interface TransactionService {
    TransactionResponse transfer(Long senderID, Long receiverID, float amount);  // ❌ FIX 1: float not Float
    float deposit(UserRecord user, float amount);  // ❌ FIX 2: float not Float
    float withdraw(UserRecord user, float amount); // ❌ FIX 2: float not Float
}
