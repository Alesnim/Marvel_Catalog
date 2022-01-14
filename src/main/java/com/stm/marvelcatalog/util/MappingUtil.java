package com.stm.marvelcatalog.util;

import com.stm.marvelcatalog.DTO.CharacterDTO;
import com.stm.marvelcatalog.DTO.CharacterResponse;
import com.stm.marvelcatalog.DTO.ComicDTO;
import com.stm.marvelcatalog.DTO.ComicResponse;
import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.model.Comic;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Util class for mapping data
 */
@Service
public class MappingUtil {

    public static CharacterDTO mapToCharacterDTO(Character character) {
        CharacterDTO dto = new CharacterDTO();
        dto.setId(character.getId().toHexString());
        dto.setName(character.getName());
        dto.setDescription(character.getDescription());
        dto.setThumbnail(character.getThumbnail());
        // Drop duplicate id comics
        dto.setComics(new HashSet<>(character.getComics()));

        return dto;
    }


    public static Character mapToCharacter(CharacterDTO characterDTO) {
        Character character = new Character();
        if (characterDTO.getId() != null) character.setId(new ObjectId(characterDTO.getId()));
        character.setName(characterDTO.getName());
        character.setThumbnail(characterDTO.getThumbnail());
        character.setDescription(characterDTO.getDescription());
        character.setComics(new ArrayList<>(characterDTO.getComics()));

        return character;
    }


    public static Comic mapToComic(ComicDTO dto) {
        Comic c = new Comic();
        if (dto.getId() != null) c.setId(new ObjectId(dto.getId()));
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setThumbnail(dto.getThumbnail());
        c.setCharacters(new ArrayList<>(dto.getCharacters()));

        return c;
    }

    public static ComicDTO mapToComicDTO(Comic comic) {
        ComicDTO dto = new ComicDTO();
        dto.setId(comic.getId().toHexString());
        dto.setName(comic.getName());
        dto.setDescription(comic.getDescription());
        dto.setThumbnail(comic.getThumbnail());
        // Drop duplicates id
        dto.setCharacters(new HashSet<>(comic.getCharacters()));

        return dto;
    }


    /**
     * Mapper for Character formatted response
     *
     * @param dto params of character
     * @return Formatted response about character
     */
    public static CharacterResponse mapToCharacterResponse(CharacterDTO dto) {
        CharacterResponse response = new CharacterResponse();
        response.setId(dto.getId());
        response.setName(dto.getName());
        response.setDescription(dto.getDescription());
        response.setThumbnails(uriResolver(dto.getId(), "character", "thumbnails"));
        // TODO: implement comicList url resolve
        var comix = Optional.ofNullable(dto.getComics());
        comix.ifPresent(comicDTOS -> response.setComics(comicDTOS
                .stream()
                .parallel()
                .map(x -> uriResolver(x.getId().toHexString(), "comics", ""))
                .collect(Collectors.toList())
        ));

        return response;
    }


    /**
     * Mapper ComicDTO to Response in API
     *
     * @param dto Comic for response
     * @return object for api serialization
     */
    public static ComicResponse mapToComicResponse(ComicDTO dto) {
        ComicResponse response = new ComicResponse();
        if (dto.getId() != null) response.setId(dto.getId());
        response.setName(dto.getName());
        response.setDescription(dto.getDescription());

        // resolve url to image
        response.setThumbnail(uriResolver(dto.getId(), "comics", "thumbnails"));
        // resolve list of Characters to lis of links
        response.setCharacters(dto.getCharacters()
                .stream()
                .map(x -> uriResolver(x.getId().toHexString(), "character", ""))
                .collect(Collectors.toList()));

        return response;
    }


    /**
     * @param id id of object in DB
     * @param point url extend tag (endpoint of entity)
     * @param option optional end of url
     * @return link to resource baseurl+point+id+option
     */
    public static String uriResolver(String id, String point, String option) {
        StringBuilder b = new StringBuilder(ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .build()
                .toUri().getAuthority());
        b.append("/");
        b.append("v2/public/");
        b.append(point);
        b.append("/");
        b.append(id);
        if (!option.isEmpty()) {
            b.append("/");
            b.append(option);
        }
        return b.toString();
    }
}
