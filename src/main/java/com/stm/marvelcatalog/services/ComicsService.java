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

    /**
     * Search comics by id
     *
     * @param id id of comic
     * @return True if comic in DB, else False
     */
    boolean containComicsById(String id);

    Comic updateComic(ComicDTO c);
}
