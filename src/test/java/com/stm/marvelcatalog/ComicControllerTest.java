package com.stm.marvelcatalog;

import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.model.Comic;
import com.stm.marvelcatalog.repository.CharacterRepo;
import com.stm.marvelcatalog.repository.ComicsRepo;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ComicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CharacterRepo characterRepo;

    @Autowired
    private ComicsRepo comicsRepo;


    @Test
    void createComicTest() throws Exception {

        var s = Files.readAllBytes(new ClassPathResource("test.jpg").getFile().toPath());
        var characters = new String[]{""};
        MockMultipartFile img = new MockMultipartFile("img", "filename-1.img", MediaType.IMAGE_JPEG_VALUE, s);


        mockMvc.perform(MockMvcRequestBuilders.multipart("/v2/public/comics")
                        .file(img)
                        .param("name", "name")
                        .param("description", "description")
                        .param("characters", Arrays.toString(characters)))
                .andExpect(status().isCreated());

        Optional<Comic> c = StreamSupport.stream(comicsRepo.findAll().spliterator(), true)
                .filter(x -> x.getName().equals("name"))
                .findAny();

        assertThat(c.isPresent());

        c.ifPresent(comicsRepo::delete);
    }


    @Test
    void returnComic() throws Exception {
        Comic c = new Comic();
        c.setName("name");
        c.setDescription("aaaa");
        c.setThumbnail(null);
        c.setCharacters(Collections.emptyList());
        c = comicsRepo.save(c);

        mockMvc.perform(get("/v2/public/comics/{id}", c.getId().toHexString()))
                .andExpect(status().isOk());

        assertThat(comicsRepo.existsById(c.getId()));

        comicsRepo.delete(c);
    }

    @Test
    void comicNotFound() throws Exception {
        ObjectId o = new ObjectId();

        mockMvc.perform(get("/v2/public/comics/{id}", o.toHexString()))
                .andExpect(status().isNotFound());
    }


    @Test
    void comicIdInvalid() throws Exception {
        String i = "0000";
        mockMvc.perform(get("/v2/public/comics/{id}", i))
                .andExpect(status().isBadRequest());
    }

    @Test
    void characterInComic() throws Exception {
        var s = Files.readAllBytes(new ClassPathResource("test.jpg").getFile().toPath());
        Character c = new Character();
        c.setName("test00000");
        c.setDescription("");
        c.setThumbnail(new Binary(s));

        c = characterRepo.save(c);

        MockMultipartFile img = new MockMultipartFile("img", "filename-1.img", MediaType.IMAGE_JPEG_VALUE, s);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/v2/public/comics")
                        .file(img)
                        .param("name", "name")
                        .param("description", "description")
                        .param("characters", Arrays.toString(new String[]{c.getId().toHexString()})))
                .andExpect(status().isCreated());

        List<String> characterIds = StreamSupport.stream(comicsRepo.findAll().spliterator(), true)
                .filter(x -> x.getName().equals("name"))
                .map(Comic::getCharacters)
                .flatMap(Collection::stream)
                .map(character -> character.getId().toHexString())
                .collect(Collectors.toList());

        assertThat(!characterIds.isEmpty());
        assertThat(characterIds.contains(c.getId().toHexString()));

        Optional<Comic> character = StreamSupport.stream(comicsRepo.findAll().spliterator(), true)
                .filter(x -> x.getName().equals("name"))
                .findAny();

        character.ifPresent(comicsRepo::delete);
        characterRepo.delete(c);

    }
}
