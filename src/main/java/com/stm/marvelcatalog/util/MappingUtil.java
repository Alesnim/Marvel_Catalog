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

import java.util.stream.Collectors;

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
        dto.setComics(character.getComics());

        return dto;
    }


    public static Character mapToCharacter(CharacterDTO characterDTO) {
        Character character = new Character();
        character.setId(new ObjectId(characterDTO.getId()));
        character.setName(characterDTO.getName());
        character.setThumbnail(characterDTO.getThumbnail());
        character.setDescription(characterDTO.getDescription());
        character.setComics(characterDTO.getComics());

        return character;
    }


    public static Comic mapToComic(ComicDTO dto) {
        Comic c = new Comic();
        if (dto.getId() != null) c.setId(new ObjectId(dto.getId()));
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setThumbnail(dto.getThumbnail());
        c.setCharacters(dto.getCharacters());

        return c;
    }

    public static ComicDTO mapToComicDTO(Comic comic) {
        ComicDTO dto = new ComicDTO();
        dto.setId(comic.getId().toHexString());
        dto.setName(comic.getName());
        dto.setDescription(comic.getDescription());
        dto.setThumbnail(comic.getThumbnail());
        dto.setCharacters(comic.getCharacters());

        return dto;
    }


    /**
     * Mapper for Character formatted response
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


        response.setComics(null);

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
                .peek(System.out::println)
                .map(x -> uriResolver(x.getId().toHexString(), "character", ""))
                .collect(Collectors.toList()));

        return response;
    }


    /**
     * @param id
     * @param point
     * @param option
     * @return
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
