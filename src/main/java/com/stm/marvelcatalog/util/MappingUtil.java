package com.stm.marvelcatalog.util;

import com.stm.marvelcatalog.DTO.CharacterDTO;
import com.stm.marvelcatalog.DTO.ComicDTO;
import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.model.Comic;
import org.springframework.stereotype.Service;

@Service
public class MappingUtil {
    public static CharacterDTO mapToCharacterDTO(Character character) {
        CharacterDTO dto = new CharacterDTO();
        dto.setName(character.getName());
        dto.setDescription(character.getDescription());
        dto.setModified(character.getModified());
        dto.setThumbnail(character.getThumbnail());
        dto.setComics(character.getComics());

        return dto;
    }


    public static Character mapToCharacter(CharacterDTO characterDTO) {
        Character character = new Character();
        character.setName(characterDTO.getName());
        character.setThumbnail(characterDTO.getThumbnail());
        character.setDescription(characterDTO.getDescription());
        character.setModified(characterDTO.getModified());
        character.setComics(characterDTO.getComics());

        return character;
    }


    public static Comic mapToComic(ComicDTO dto) {
        Comic c = new Comic();
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setThumbnail(dto.getThumbnail());
        c.setCharacters(dto.getCharacters());

        return c;
    }

    public static ComicDTO mapToComicDTO(Comic comic) {
        ComicDTO dto = new ComicDTO();
        dto.setName(comic.getName());
        dto.setDescription(comic.getDescription());
        dto.setThumbnail(comic.getThumbnail());
        dto.setCharacters(comic.getCharacters());

        return dto;
    }
}
