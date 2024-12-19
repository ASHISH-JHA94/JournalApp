package net.engineeringdigest.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import net.engineeringdigest.journalApp.Repository.UserEntryRepository;




@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserEntryRepository userEntryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database
        net.engineeringdigest.journalApp.entity.User user = userEntryRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Here, you can dynamically assign roles based on the user entity's data
        // For example, assuming you have a `role` field in the User entity
        String[] roles = user.getRoles().toArray(new String[0]); // Assuming getRoles() returns a List<String>


        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roles) // Dynamically assigning roles from DB
                .build();
    }
}
