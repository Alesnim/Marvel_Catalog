package com.stm.marvelcatalog.model;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "Comics")
public class Comic {

    @Id
    private ObjectId id;
    private String name;
    private String description;
    private List<Character> characters;
    private Binary thumbnail;

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

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comic comic = (Comic) o;
        return Objects.equals(getId(), comic.getId()) && Objects.equals(getName(), comic.getName()) && Objects.equals(getDescription(), comic.getDescription()) && Objects.equals(getCharacters(), comic.getCharacters()) && Objects.equals(getThumbnail(), comic.getThumbnail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getCharacters(), getThumbnail());
    }

    @Override
    public String toString() {
        return "Comic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", characters=" + characters +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
