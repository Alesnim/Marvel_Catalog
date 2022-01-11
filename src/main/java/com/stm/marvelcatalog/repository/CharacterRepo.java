package com.stm.marvelcatalog.repository;

import com.stm.marvelcatalog.model.Character;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepo extends PagingAndSortingRepository<Character, ObjectId> {

}
