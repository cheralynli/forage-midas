package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public Optional<UserRecord> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<UserRecord> findUserByName(String name) {
        return userRepository.findByName(name);
    }


    @Transactional
    public boolean processTransaction(Long senderId, Long recipientId, float amount) {
        // Find both users
        Optional<UserRecord> senderOpt = findUserById(senderId);
        Optional<UserRecord> recipientOpt = findUserById(recipientId);

        // Validate transaction
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return false; // Invalid users
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if (sender.getBalance() < amount) {
            return false; // Insufficient funds
        }

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        // Save users
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create and save transaction record
        TransactionRecord transaction = new TransactionRecord(amount, sender, recipient);
        transactionRecordRepository.save(transaction);

        return true;
    }

}