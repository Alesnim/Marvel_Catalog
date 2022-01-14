package com.stm.marvelcatalog.exceptions;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


public class CharacterNotFoundException extends RuntimeException {

    public CharacterNotFoundException(String id) {
        super("Character not found" + " " + id);
    }
}
