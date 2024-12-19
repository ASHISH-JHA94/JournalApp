package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.JournalEntryService;
import net.engineeringdigest.journalApp.service.UserEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("journal")
public class JourneyControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserEntryService userEntryService;

    // Get the authenticated user's username
    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    // Fetch the authenticated user's details
    private User getAuthenticatedUser() {
        String username = getAuthenticatedUsername();
        return userEntryService.getUserByName(username);
    }

    // Fetch a journal entry by ID
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntry> findById(@PathVariable ObjectId id) {
        try {
            JournalEntry entry = journalEntryService.findById(id);
            if (entry == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Fetch all journal entries for the authenticated user
    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAll() {
        try {
            User authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            List<JournalEntry> entries = journalEntryService.getEntriesByUserId(authenticatedUser.getId());
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Delete a journal entry by ID for the authenticated user
    @DeleteMapping("/{journalId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable ObjectId journalId) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
            }
            boolean deleted = journalEntryService.deleteById(journalId, authenticatedUser.getId());
            if (deleted) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    // Update a journal entry by ID for the authenticated user
    @PutMapping("/{id}")
    public ResponseEntity<JournalEntry> updateById(@PathVariable ObjectId id, @RequestBody JournalEntry updatedEntry) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            JournalEntry entry = journalEntryService.findById(id);
            if (entry == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            if (!entry.getUserId().equals(authenticatedUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
            entry.setTitle(updatedEntry.getTitle());
            entry.setContent(updatedEntry.getContent());
            journalEntryService.save(entry, authenticatedUser.getId());
            return ResponseEntity.ok(entry);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Create a new journal entry for the authenticated user
    @PostMapping("/create")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            User authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            journalEntryService.save(myEntry, authenticatedUser.getId());
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
