package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator {
    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private DatabaseConduit databaseConduit;

    public void populate() {
        System.out.println("=== USER POPULATOR STARTING ===");

        String[] userLines = fileLoader.loadStrings("/test_data/lkjhgfdsa.hjkl");
        System.out.println("Loaded " + userLines.length + " user lines from file");

        for (String userLine : userLines) {
            System.out.println("Processing user line: '" + userLine + "'");
            String[] userData = userLine.split(", ");
            UserRecord user = new UserRecord(userData[0], Float.parseFloat(userData[1]));
            System.out.println("Created user: " + user.getName() + " with balance: " + user.getBalance());
            databaseConduit.save(user);
            System.out.println("Saved user to database");
        }

        System.out.println("=== USER POPULATOR FINISHED ===");
    }
}