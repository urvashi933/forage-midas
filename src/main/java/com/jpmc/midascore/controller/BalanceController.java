package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    @Autowired
    private UserService userService;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId){  // ❌ FIX 1: Add @RequestParam
        UserRecord user = userService.getUserByID(userId);
        if(user == null)
            return new Balance(0.0);  // ❌ FIX 2: Use 0.0 (double)
        else
            return new Balance(user.getBalance());
    }
}
