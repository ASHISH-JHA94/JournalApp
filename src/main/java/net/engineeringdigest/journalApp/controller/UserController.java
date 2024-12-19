package net.engineeringdigest.journalApp.controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserEntryService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j  // Lombok annotation to create a logger instance (log)
public class UserController {

    private final UserEntryService userEntryService;

    public UserController(UserEntryService userEntryService) {
        this.userEntryService = userEntryService;
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            log.info("Attempting to update user with username: {}", username);

            User userInDb = userEntryService.getUserByName(username);
            if (userInDb == null) {
                log.warn("User not found for username: {}", username);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            userEntryService.updateUser(userInDb.getId(), user);
            log.info("User with username: {} updated successfully", username);
            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error updating user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@PathVariable String id) {
        try {
            log.info("Attempting to delete user with id: {}", id);

            ObjectId objectId = new ObjectId(id);
            userEntryService.deleteUserById(objectId);

            log.info("User with id: {} deleted successfully", id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting user with id {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Error deleting user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            log.info("Fetching all users");

            return new ResponseEntity<>(userEntryService.getAllUser(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error fetching users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byName/{name}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        try {
            log.info("Fetching user by name: {}", name);

            User user = userEntryService.getUserByName(name);
            if (user != null) {
                log.info("User found by name: {}", name);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                log.warn("User not found by name: {}", name);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error fetching user by name {}: {}", name, e.getMessage(), e);
            return new ResponseEntity<>("Error fetching user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byId/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        try {
            log.info("Fetching user by ID: {}", id);

            ObjectId objectId = new ObjectId(id);
            User user = userEntryService.getUserById(objectId);
            if (user != null) {
                log.info("User found by ID: {}", id);
                return new ResponseEntity<>(user, HttpStatus.OK);
            } else {
                log.warn("User not found by ID: {}", id);
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error fetching user by ID {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Error fetching user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
