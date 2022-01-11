package com.stm.marvelcatalog.repository;

import com.stm.marvelcatalog.model.Comic;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ComicsRepo extends PagingAndSortingRepository<Comic, ObjectId> {
}
