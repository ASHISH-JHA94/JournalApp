package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.Repository.JournalEntryRepository;
import net.engineeringdigest.journalApp.Repository.UserEntryRepository;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class UserEntryService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserEntryRepository userEntryRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    // Save a user
    public boolean saveUser(User user) {
        try{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userEntryRepository.save(user);
            return true;
        }catch(Exception e){
            return false;
        }

    }

    // Get all users
    public List<User> getAllUser() {
        return userEntryRepository.findAll();
    }

    // Update a user by ID
    public void updateUser(ObjectId id, User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setId(id); // Ensure the ID is set to the updated user
        userEntryRepository.save(user);
    }
    @Transactional
    // Delete a user by ID along with associated journal entries
    public void deleteUserById(ObjectId userId) {
        try {
            // Fetch the user and their journal entry list
            User user = userEntryRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Safeguard against null entries in the journalEntryList
            List<JournalEntry> journalEntries = user.getJournalEntryList();
            if (journalEntries != null) {
                for (JournalEntry journalEntry : journalEntries) {
                    if (journalEntry != null) {
                        journalEntryRepository.deleteById(journalEntry.getId());
                    }
                }
            }

            // Delete the user
            userEntryRepository.deleteById(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user and associated journal entries: " + e.getMessage());
        }
    }


    // Get a user by username
    public User getUserByName(String username) {
        return userEntryRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User with username '" + username + "' not found"));
    }

    // Get a user by ID
    public User getUserById(ObjectId id) {
        return userEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with ID '" + id + "' not found"));
    }
}
