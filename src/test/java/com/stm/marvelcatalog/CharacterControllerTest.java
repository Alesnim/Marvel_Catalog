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
public class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CharacterRepo characterRepo;

    @Autowired
    private ComicsRepo comicsRepo;


    @Test
    void createCharacterTest() throws Exception {
        var s = Files.readAllBytes(new ClassPathResource("test.jpg").getFile().toPath());
        var comics = new String[]{""};
        MockMultipartFile img = new MockMultipartFile("img", "filename-1.img", MediaType.IMAGE_JPEG_VALUE, s);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/v2/public/character")
                .file(img)
                .param("name", "name")
                .param("description", "description")
                .param("comics", Arrays.toString(comics)))
                .andExpect(status().isCreated());

        Optional<Character> c = StreamSupport.stream(characterRepo.findAll().spliterator(), true)
                .filter(x -> x.getName().equals("name"))
                .findAny();

        assertThat(c.isPresent());

        c.ifPresent(characterRepo::delete);

    }

    @Test
    void returnCharacter() throws Exception {
        Character c = new Character();
        c.setName("test");
        c.setDescription("aaaa");
        c.setThumbnail(null);
        c.setComics(Collections.emptyList());
        c = characterRepo.save(c);

        mockMvc.perform(get("/v2/public/character/{id}", c.getId().toHexString())).andExpect(status().isOk());

        assertThat(characterRepo.existsById(c.getId()));

        characterRepo.delete(c);
    }

    @Test
    void characterNotFound() throws Exception {
        ObjectId o = new ObjectId();

        mockMvc.perform(get("/v2/public/character/{id}", o.toHexString())).andExpect(status().isNotFound());
    }

        @Test
        void characterIdInvalid() throws Exception {
            String i = "0000";
            mockMvc.perform(get("/v2/public/character/{id}", i)).andExpect(status().isBadRequest());
        }

    @Test
    void comicsInCharacter() throws Exception {
        var s = Files.readAllBytes(new ClassPathResource("test.jpg").getFile().toPath());
        Comic c = new Comic();
        c.setName("test00000");
        c.setDescription("");
        c.setThumbnail(new Binary(s));

        c = comicsRepo.save(c);

        MockMultipartFile img = new MockMultipartFile("img", "filename-1.img", MediaType.IMAGE_JPEG_VALUE, s);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/v2/public/character")
                        .file(img)
                        .param("name", "name")
                        .param("description", "description")
                        .param("comics", Arrays.toString(new String[]{c.getId().toHexString()})))
                .andExpect(status().isCreated());

        List<String> comicsIds = StreamSupport.stream(characterRepo.findAll().spliterator(), true)
                .filter(x -> x.getName().equals("name"))
                .map(Character::getComics)
                .flatMap(Collection::stream)
                .map(comic -> comic.getId().toHexString())
                .collect(Collectors.toList());

        assertThat(!comicsIds.isEmpty());
        assertThat(comicsIds.contains(c.getId().toHexString()));

        Optional<Character> character = StreamSupport.stream(characterRepo.findAll().spliterator(), true)
                .filter(x -> x.getName().equals("name"))
                .findAny();

        character.ifPresent(characterRepo::delete);
        comicsRepo.delete(c);

    }

}
