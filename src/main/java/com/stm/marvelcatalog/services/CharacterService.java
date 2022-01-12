package com.stm.marvelcatalog.services;

import com.stm.marvelcatalog.DTO.CharacterDTO;
import com.stm.marvelcatalog.model.Character;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CharacterService {


    /**
     * Method searches all character
     *
     * @return list of all character
     */
    List<CharacterDTO> getAllCharacter();


    /**
     * @param id of character
     * @return Character DAO from database
     */
    CharacterDTO getCharacterById(String id);


    /**
     * Adding character to base
     *
     * @param characterDTO Character params
     * @return instance of created character
     */
    Character insertCharacter(CharacterDTO characterDTO);

    /**
     *
     * @param characterDTO Character params
     * @return
     */
    Character updateCharacter(CharacterDTO characterDTO);


    /**
     * Check contain in DB
     * @param id
     * @return True if Character contains, False
     */
    boolean containCharacterById(String id);

}
