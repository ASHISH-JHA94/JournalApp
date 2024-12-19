package net.engineeringdigest.journalApp.controller;

import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.service.UserEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {
    private final UserEntryService userEntryService;

    public PublicController(UserEntryService userEntryService) {
        this.userEntryService = userEntryService;
    }
    @PostMapping("/add")
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        try {
            userEntryService.saveUser(user);
            return new ResponseEntity<>("User saved successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}