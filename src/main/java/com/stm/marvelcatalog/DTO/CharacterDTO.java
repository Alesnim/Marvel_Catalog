package com.stm.marvelcatalog.DTO;


import com.stm.marvelcatalog.model.Comic;
import org.bson.types.Binary;

import java.util.Objects;
import java.util.Set;

public class CharacterDTO {
    private String id;
    private String name;
    private String description;
    private Binary thumbnail;
    private Set<Comic> comics;
    private String thumbnailURL;

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

    public Binary getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Binary thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isEmpty() {
        return name != null
                || description != null
                || thumbnail != null
                || comics != null;
    }


    public Set<Comic> getComics() {
        return comics;
    }

    public void setComics(Set<Comic> comics) {
        this.comics = comics;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @Override
    public String toString() {
        return "CharacterDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail=" + thumbnail +
                ", comics='" + comics + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterDTO that = (CharacterDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getThumbnail(), that.getThumbnail()) && Objects.equals(getComics(), that.getComics()) && Objects.equals(getThumbnailURL(), that.getThumbnailURL());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getThumbnail(), getComics(), getThumbnailURL());
    }
}
