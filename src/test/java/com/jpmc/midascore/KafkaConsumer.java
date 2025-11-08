package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class KafkaConsumer {

    private final DatabaseConduit databaseConduit;
    private final UserRepository userRepository;

    public KafkaConsumer(DatabaseConduit databaseConduit, UserRepository userRepository) {
        this.databaseConduit = databaseConduit;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "test-group")
    public void receiveTransaction(Transaction transaction) {
        // Process transaction
        databaseConduit.processTransaction(
                transaction.getSenderId(),
                transaction.getRecipientId(),
                transaction.getAmount()
        );

        // Check waldorf's balance
        Optional<UserRecord> waldorf = userRepository.findByName("waldorf");
        if (waldorf.isPresent()) {
            float balance = waldorf.get().getBalance();
            System.out.println("waldorf balance: " + balance);
        }

        // Debug point
        int debugPoint = 1;
    }
}