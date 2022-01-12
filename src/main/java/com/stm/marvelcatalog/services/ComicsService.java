package com.stm.marvelcatalog.services;

import com.stm.marvelcatalog.DTO.ComicDTO;
import com.stm.marvelcatalog.model.Comic;

import java.util.List;

public interface ComicsService {

    /**
     * Method searches all comics
     *
     * @return list of all comics
     */
    List<ComicDTO> getAllComics();


    /**
     * @param id of comics
     * @return Comics DAO from database
     */
    ComicDTO getComicsById(String id);


    /**
     * Adding character in base
     *
     * @param comics comics params
     * @return instance of created comics
     */
    Comic insertComics(ComicDTO comics);
}
