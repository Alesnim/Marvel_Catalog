package com.stm.marvelcatalog.controller.validators;

import org.bson.types.Binary;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ImageFileValidator {
    private static final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");

    public static Binary validate(MultipartFile file) throws IOException {
        if (contentTypes.contains(file.getContentType())) {
            return new Binary(file.getBytes());
        }
        return null;
    }
}
