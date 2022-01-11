package com.stm.marvelcatalog.DTO;

import com.stm.marvelcatalog.model.Character;
import org.bson.types.Binary;

import java.util.List;

public class ComicDTO {

    private String name;
    private String description;
    private List<Character> characters;
    private Binary thumbnail;

    public ComicDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
    }

    public Binary getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Binary thumbnail) {
        this.thumbnail = thumbnail;

    }

    public boolean isEmpty() {
        return name == null
                && description == null
                && thumbnail == null
                && characters == null;
    }
}
