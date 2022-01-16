package com.stm.marvelcatalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.repository.CharacterRepo;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CharacterRepo charaterRepo;


    @Test
    void crateCharacterTest() throws Exception {
        var s = Files.readAllBytes(new ClassPathResource("test.jpg").getFile().toPath());
        var comics = new String[]{""};
        mockMvc.perform(MockMvcRequestBuilders.multipart("/v2/public/character")
                        .file("img", s)
                        .param("description", "")
                        .param("name", "test")
                        .param("comics", Arrays.toString(comics)))
                .andExpect(status().isCreated());

        Optional<Character> c = StreamSupport.stream(charaterRepo.findAll()
                        .spliterator(), true)
                .filter(x -> x.getName().equals("test00000")).findFirst();

        assertThat(c.isPresent());
    }

    @Test
    void returnCharacter() throws Exception {
        Character c = new Character();
        c.setName("test");
        c.setDescription("aaaa");
        c.setThumbnail(null);
        c.setComics(Collections.emptyList());
        c = charaterRepo.save(c);

        mockMvc.perform(get("/v2/public/character/{id}", c.getId().toHexString()))
                .andExpect(status().isOk());

        assertThat(charaterRepo.existsById(c.getId()));

        charaterRepo.delete(c);
    }

    @Test
    void characterNotFound() throws Exception {
        ObjectId o = new ObjectId();

        mockMvc.perform(get("/v2/public/character/{id}", o.toHexString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void characterIdInvalid() throws Exception {
        String i = "0000";
        mockMvc.perform(get("/v2/public/character/{id}", i))
                .andExpect(status().isBadRequest());
    }
}
