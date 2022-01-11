package com.stm.marvelcatalog.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class ComicList {
    @Id
    private ObjectId id;
    private String collectionUri;
    private List<Comic> items;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCollectionUri() {
        return collectionUri;
    }

    public void setCollectionUri(String collectionUri) {
        this.collectionUri = collectionUri;
    }

    public List<Comic> getItems() {
        return items;
    }

    public void setItems(List<Comic> items) {
        this.items = items;
    }


}
