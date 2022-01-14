package com.stm.marvelcatalog.repository;

import com.stm.marvelcatalog.model.Comic;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ComicsRepo extends PagingAndSortingRepository<Comic, ObjectId> {

    /**
     * Check comics exist by id
     * @param objectId id of comic
     * @return True if comics with id exist
     */
    @Override
    boolean existsById(ObjectId objectId);



}
