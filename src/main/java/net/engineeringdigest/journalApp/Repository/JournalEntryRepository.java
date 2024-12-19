package net.engineeringdigest.journalApp.Repository;


import net.engineeringdigest.journalApp.entity.JournalEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JournalEntryRepository extends MongoRepository<JournalEntry, ObjectId> {


    List<JournalEntry> findByUserId(ObjectId userId);
}
