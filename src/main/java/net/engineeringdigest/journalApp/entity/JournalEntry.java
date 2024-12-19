package net.engineeringdigest.journalApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@NoArgsConstructor
public class JournalEntry {

    @Id
    private ObjectId id;

    private String title;
    private String content;

    private LocalDateTime dateTime = LocalDateTime.now(); // Automatically set creation date

    private ObjectId userId; // Link to the user who owns this entry
}
