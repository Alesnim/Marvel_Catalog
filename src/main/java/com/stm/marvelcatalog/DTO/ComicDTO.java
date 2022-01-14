package com.stm.marvelcatalog.DTO;

import com.stm.marvelcatalog.model.Character;
import org.bson.types.Binary;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ComicDTO {

    private String id;
    private String name;
    private String description;
    private Set<Character> characters;
    private Binary thumbnail;

    public ComicDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Set<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<Character> characters) {
        this.characters = characters;
    }

    public Binary getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Binary thumbnail) {
        this.thumbnail = thumbnail;

    }

    @Override
    public String toString() {
        return "ComicDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", characters=" + characters +
                ", thumbnail=" + thumbnail +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComicDTO comicDTO = (ComicDTO) o;
        return Objects.equals(getId(), comicDTO.getId()) && Objects.equals(getName(), comicDTO.getName()) && Objects.equals(getDescription(), comicDTO.getDescription()) && Objects.equals(getCharacters(), comicDTO.getCharacters()) && Objects.equals(getThumbnail(), comicDTO.getThumbnail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getCharacters(), getThumbnail());
    }

    public boolean isEmpty() {
        return name == null
                && description == null
                && thumbnail == null
                && characters == null;
    }
}
