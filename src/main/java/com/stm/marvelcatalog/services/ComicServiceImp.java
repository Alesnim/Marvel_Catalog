package com.stm.marvelcatalog.services;

import com.stm.marvelcatalog.DTO.ComicDTO;
import com.stm.marvelcatalog.model.Comic;
import com.stm.marvelcatalog.repository.ComicsRepo;
import com.stm.marvelcatalog.util.MappingUtil;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ComicServiceImp implements ComicsService {
    private final ComicsRepo comicsRepo;


    public ComicServiceImp(ComicsRepo comicsRepo) {
        this.comicsRepo = comicsRepo;
    }

    @Override
    public List<ComicDTO> getAllComics() {
        return StreamSupport.stream(comicsRepo.findAll().spliterator(), true)
                .map(MappingUtil::mapToComicDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ComicDTO getComicsById(String id) {
        return comicsRepo.findById(new ObjectId(id))
                .map(MappingUtil::mapToComicDTO)
                .orElseGet(ComicDTO::new);
    }

    @Override
    public Comic insertComics(ComicDTO comics) {
        Comic c = MappingUtil.mapToComic(comics);
        comicsRepo.save(c);
        return c;
    }

    @Override
    public boolean containComicsById(String id) {
        if (id != null) return comicsRepo.existsById(new ObjectId(id));
        else return false;
    }

    @Override
    public Comic updateComic(ComicDTO c) {
        return comicsRepo.save(MappingUtil.mapToComic(c));
    }
}
