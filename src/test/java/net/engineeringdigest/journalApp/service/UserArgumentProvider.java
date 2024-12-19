package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.User; // Import your entity class
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

public class UserArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(org.junit.jupiter.api.extension.ExtensionContext context) {
        return Stream.of(
                Arguments.of(new User("user", "password"))
                // Use your entity's constructor
        );
    }
}
