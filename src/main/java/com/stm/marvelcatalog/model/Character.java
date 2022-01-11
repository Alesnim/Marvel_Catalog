package com.stm.marvelcatalog.model;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;

@Document(collection = "characters")
public class Character {

    @Id
    private ObjectId id;
    private String name;
    private String description;
    private Date modified;
    private Binary thumbnail;
    private ComicList comics;


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

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Binary getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Binary thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ComicList getComics() {
        return comics;
    }

    public void setComics(ComicList comics) {
        this.comics = comics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return Objects.equals(getId(), character.getId()) && Objects.equals(getName(), character.getName()) && Objects.equals(getDescription(), character.getDescription()) && Objects.equals(getModified(), character.getModified()) && Objects.equals(getThumbnail(), character.getThumbnail()) && Objects.equals(getComics(), character.getComics());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getModified(), getThumbnail(), getComics());
    }
}
