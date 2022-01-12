package com.stm.marvelcatalog.repository;

import com.stm.marvelcatalog.model.Character;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepo extends PagingAndSortingRepository<Character, ObjectId> {

    /**
     * Check Character in DB
     *
     * @param id Id of Character (Hex String)
     * @return True if Character in DB, False if not
     */
    boolean existsById(ObjectId id);

}
