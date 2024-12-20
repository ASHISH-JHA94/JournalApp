package net.engineeringdigest.journalApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.ArrayList;

@Data
@Document
@NoArgsConstructor
public class User {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    @NonNull
    private String username; // Changed to lowercase

    @NonNull
    private String password;

    @DBRef
    private List<JournalEntry> journalEntryList = new ArrayList<>();

    private List<String>roles = new ArrayList<>();


    public User(String user, String password) {
        this.username = user;
        this.password = password;
    }
}




