package com.stm.marvelcatalog.services;

import com.stm.marvelcatalog.DTO.CharacterDTO;
import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.repository.CharacterRepo;
import com.stm.marvelcatalog.util.MappingUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CharacterServiceImp implements CharacterService {


    private final CharacterRepo characterRepo;


    public CharacterServiceImp(CharacterRepo characterRepo) {
        this.characterRepo = characterRepo;
    }

    @Override
    public List<CharacterDTO> getAllCharacter() {
        return StreamSupport.stream(characterRepo.findAll().spliterator(), true)
                .map(MappingUtil::mapToCharacterDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CharacterDTO getCharacterById(String id) {

        return characterRepo
                .findById(new ObjectId(id))
                .map(MappingUtil::mapToCharacterDTO)
                .orElseGet(CharacterDTO::new);
    }

    @Override
    public Character insertCharacter(CharacterDTO characterDTO) throws IllegalArgumentException {
        Character c = MappingUtil.mapToCharacter(characterDTO);
        characterRepo.save(c);
        return c;
    }
}
