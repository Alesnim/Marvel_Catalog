package com.stm.marvelcatalog.services;

import com.stm.marvelcatalog.DTO.ComicDTO;
import com.stm.marvelcatalog.model.Comic;

import java.util.List;

public interface ComicsService {

    /**
     * Method searches all character
     *
     * @return list of all character
     */
    List<ComicDTO> getAllComics();


    /**
     * @param id of character
     * @return Character DAO from database
     */
    ComicDTO getComicsById(String id);


    /**
     * Adding character in base
     *
     * @param comics Character params
     * @return instance of created character
     */
    Comic insertComics(ComicDTO comics);
}
