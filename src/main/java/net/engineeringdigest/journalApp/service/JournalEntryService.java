package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.Repository.JournalEntryRepository;
import net.engineeringdigest.journalApp.Repository.UserEntryRepository;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserEntryRepository userEntryRepository;

    @Transactional
    public void save(JournalEntry journalEntry, ObjectId userId) {
        // Set the current date-time for the journal entry
        journalEntry.setDateTime(LocalDateTime.now());
        journalEntry.setUserId(userId);
        // Save the journal entry
        JournalEntry savedEntry = journalEntryRepository.save(journalEntry);

        // Update the user's journal entry list
        User user = userEntryRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User with the given ID does not exist.")
        );
        user.getJournalEntryList().add(savedEntry);
        userEntryRepository.save(user);
    }

    @Transactional
    public boolean deleteById(ObjectId journalEntryId, ObjectId userId) {
        // Find the user and update their journal entry list
        User user = userEntryRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User with the given ID does not exist.")
        );

        JournalEntry entryToRemove = journalEntryRepository.findById(journalEntryId).orElse(null);
        if (entryToRemove != null) {
            user.getJournalEntryList().remove(entryToRemove);
            userEntryRepository.save(user);
            journalEntryRepository.delete(entryToRemove);
            return true;
        } else {
            return false;
        }
    }



    public JournalEntry findById(ObjectId id) {
        return journalEntryRepository.findById(id).orElse(null);
    }

    public JournalEntry updateById(ObjectId id, JournalEntry updatedEntry) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        if (updatedEntry == null) {
            throw new IllegalArgumentException("Updated entry cannot be null.");
        }
        if (!id.equals(updatedEntry.getId())) {
            throw new IllegalArgumentException("Provided ID does not match the ID in the updated entry.");
        }
        if (!journalEntryRepository.existsById(id)) {
            throw new IllegalArgumentException("Entry with the given ID does not exist.");
        }

        return journalEntryRepository.save(updatedEntry);
    }

    public List<JournalEntry> getEntriesByUserId(ObjectId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return journalEntryRepository.findByUserId(userId);
    }

}
