package com.stm.marvelcatalog.exceptions;

public class ComicNotFoundException extends RuntimeException {

    public ComicNotFoundException(String id) {
        super("Comic not found" + " " + id);
    }
}
