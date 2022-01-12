package com.stm.marvelcatalog.model;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * Character Entity class
 */
@Document(collection = "characters")
public class Character {

    /**
     * Id of Character in DB (Mongo "_id")
     */
    @Id
    private ObjectId id;
    /**
     * Name of Character
     */
    private String name;
    /**
     * Description of Character
     */
    private String description;
    /**
     * Image of Character (Binary for Mongo)
     */
    private Binary thumbnail;
    /**
     * Uri of comics collection
     */
    private String comics;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Binary getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Binary thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getComics() {
        return comics;
    }

    public void setComics(String comics) {
        this.comics = comics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return Objects.equals(getId(), character.getId()) && Objects.equals(getName(), character.getName()) && Objects.equals(getDescription(), character.getDescription()) && Objects.equals(getThumbnail(), character.getThumbnail()) && Objects.equals(getComics(), character.getComics());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getThumbnail(), getComics());
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail=" + thumbnail +
                ", comics='" + comics + '\'' +
                '}';
    }
}
