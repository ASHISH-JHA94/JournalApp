package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.Repository.UserEntryRepository;
import net.engineeringdigest.journalApp.controller.UserController;
import net.engineeringdigest.journalApp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



class CustomUserDetailsServiceTest {

    @Mock
    private UserEntryRepository userEntryRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private UserEntryService userEntryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public CustomUserDetailsServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @ArgumentsSource(UserArgumentProvider.class)
    void testSaveUser(User user) {
        assertTrue(userEntryService.saveUser(user));
    }
    @Test
    void testLoadUserByUsername_UserExists() {
        // Arrange
        String username = "ram";
        User mockUser = new User();
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");
        mockUser.setRoles(Collections.singletonList("USER"));

        when(userEntryRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        verify(userEntryRepository, times(1)).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        when(userEntryRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));
        verify(userEntryRepository, times(1)).findByUsername(username);
    }
}
