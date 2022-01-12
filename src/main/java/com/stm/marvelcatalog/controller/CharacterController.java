package com.stm.marvelcatalog.controller;

import com.stm.marvelcatalog.DTO.CharacterDTO;
import com.stm.marvelcatalog.DTO.CharacterResponse;
import com.stm.marvelcatalog.controller.validators.ImageFileValidator;
import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.services.CharacterService;
import com.stm.marvelcatalog.util.AppConstants;
import com.stm.marvelcatalog.util.MappingUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/v2/public")
public class CharacterController {

    private final CharacterService characterServiceImp;


    public CharacterController(CharacterService characterServiceImp) {
        this.characterServiceImp = characterServiceImp;
    }

    @ApiOperation(value = "Endpoint returns all of characters in comics", notes = "Return Page of Characters", tags = "Marvel Characters")
    @ApiResponses(
            @ApiResponse(code = 200, message = "Successful requested all of characters", response = Page.class)
    )
    @GetMapping("/characters")
    public Page<CharacterResponse> allCharacters(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        List<CharacterResponse> all = characterServiceImp.getAllCharacter()
                .stream()
                .map(MappingUtil::mapToCharacterResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(all, pageable, all.size());
    }

    @ApiOperation(value = "Endpoint creates of character", notes = "Return of character status", tags = "Marvel Characters")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful created", response = Page.class),
            @ApiResponse(code = 400, message = "Incorrect data")
    }
    )
    @PostMapping(value = "/character", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CharacterDTO> createCharacter(
            @RequestPart String name,
            @RequestPart String description,
            @RequestPart MultipartFile img,
            @RequestPart String comics
    ) {
        try {
            CharacterDTO dto = new CharacterDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setThumbnail(ImageFileValidator.validate(img));
            dto.setComics(comics);

            Character c = characterServiceImp.insertCharacter(dto);
            if (c != null) {
                dto.setId(c.getId().toHexString());
            }
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    @ApiOperation(value = "Return character by Id", tags = "Marvel Characters")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success search, return character"),
            @ApiResponse(code = 404, message = "Character not found")
    })
    @GetMapping("/character/{id}")
    public ResponseEntity<CharacterResponse> searchCharacter(@PathVariable("id") String id) {
        CharacterDTO c = characterServiceImp.getCharacterById(id);
        CharacterResponse response = MappingUtil.mapToCharacterResponse(c);
        if (!c.isEmpty()) return new ResponseEntity<>(response, HttpStatus.OK);
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @ApiOperation(value = "Returns url of thumbnails", tags = "Marvel Characters")
    @GetMapping(value = "/character/{id}/thumbnails", produces = {MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> thumbnailsReturn(@PathVariable("id") String id) {
        CharacterDTO c = characterServiceImp.getCharacterById(id);
        if (!c.isEmpty())
            return ResponseEntity.ok()
                    .contentLength(c.getThumbnail().length())
                    .body(c.getThumbnail().getData());

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


}
