package com.stm.marvelcatalog.DTO;


import com.stm.marvelcatalog.model.ComicList;
import org.bson.types.Binary;

import java.util.Date;

public class CharacterDTO {
    private String name;
    private String description;
    private Date modified;
    private Binary thumbnail;
    private ComicList comics;


    public CharacterDTO() {
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

    public boolean isEmpty() {
        return name == null
                && description == null
                && modified == null
                && thumbnail == null
                && comics == null;
    }


    public ComicList getComics() {
        return comics;
    }

    public void setComics(ComicList comics) {
        this.comics = comics;
    }
}
