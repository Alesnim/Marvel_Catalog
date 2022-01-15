package com.stm.marvelcatalog.controller;

import com.stm.marvelcatalog.DTO.CharacterDTO;
import com.stm.marvelcatalog.DTO.CharacterResponse;
import com.stm.marvelcatalog.DTO.ComicResponse;
import com.stm.marvelcatalog.controller.validators.ImageFileValidator;
import com.stm.marvelcatalog.exceptions.CharacterNotFoundException;
import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.services.CharacterService;
import com.stm.marvelcatalog.services.ComicsService;
import com.stm.marvelcatalog.util.AppConstants;
import com.stm.marvelcatalog.util.MappingUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bson.types.ObjectId;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping("/v2/public")
public class CharacterController {

    private final CharacterService characterServiceImp;
    private final ComicsService comicsServiceImp;


    public CharacterController(CharacterService characterServiceImp, ComicsService comicsServiceImp) {
        this.characterServiceImp = characterServiceImp;
        this.comicsServiceImp = comicsServiceImp;
    }

    @ApiOperation(value = "Endpoint returns all of characters in comics", notes = "Return Page of Characters", tags = "Characters")
    @ApiResponses(@ApiResponse(code = 200, message = "Successful requested all of characters", response = Page.class))
    @GetMapping(value = "/characters", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CharacterResponse> allCharacters(@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo, @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize, @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy, @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        List<CharacterResponse> all = characterServiceImp.getAllCharacter().stream().map(MappingUtil::mapToCharacterResponse).collect(Collectors.toList());
        return new PageImpl<>(all, pageable, all.size());
    }

    @ApiOperation(value = "Endpoint creates of character", notes = "Return of character status", tags = "Characters")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Successful created", response = Page.class), @ApiResponse(code = 400, message = "Incorrect data")})
    @PostMapping(value = "/character", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CharacterResponse> createCharacter(@RequestPart String name, @RequestPart String description, @RequestPart MultipartFile img, @RequestParam String[] comics) {
        try {
            CharacterDTO dto = new CharacterDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setThumbnail(ImageFileValidator.validate(img));
            if (comics.length != 0) {
                dto.setComics(Stream.of(comics).filter(ObjectId::isValid).filter(comicsServiceImp::containComicsById).map(comicsServiceImp::getComicsById).map(MappingUtil::mapToComic).collect(Collectors.toSet()));

            } else {
                dto.setComics(Collections.emptySet());
            }

            Character c = characterServiceImp.insertCharacter(dto);

            // update comics when save character
            c.getComics().forEach(x -> {
                x.getCharacters().add(c);
                comicsServiceImp.updateComic(MappingUtil.mapToComicDTO(x));
            });

            dto.setId(c.getId().toHexString());
            return new ResponseEntity<>(MappingUtil.mapToCharacterResponse(dto), HttpStatus.CREATED);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }


    @ApiOperation(value = "Return character by Id", tags = "Characters")
    @ApiResponses({@ApiResponse(code = 200, message = "Success search, return character"), @ApiResponse(code = 404, message = "Character not found"), @ApiResponse(code = 400, message = "Id invalid")})
    @GetMapping(value = "/character/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CharacterResponse> searchCharacter(@PathVariable("id") String id) {
        try {
            if (!ObjectId.isValid(id)) throw new IllegalArgumentException();
            CharacterDTO c = characterServiceImp.getCharacterById(id);
            CharacterResponse response = MappingUtil.mapToCharacterResponse(c);
            if (c.isEmpty()) return new ResponseEntity<>(response, HttpStatus.OK);
            else throw new CharacterNotFoundException(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Invalid id: %s", id), e);
        } catch (CharacterNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }


    }


    @ApiOperation(value = "Returns url of thumbnails", tags = "Characters")
    @ApiResponses({@ApiResponse(code = 200, message = "Return image"), @ApiResponse(code = 404, message = "Thumbnail not found"), @ApiResponse(code = 400, message = "Id invalid")})
    @GetMapping(value = "/character/{id}/thumbnails", produces = {MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> thumbnailsReturn(@PathVariable("id") String id) throws CharacterNotFoundException {
        CharacterDTO c;
        if (ObjectId.isValid(id)) c = characterServiceImp.getCharacterById(id);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not valid");
        if (c.isEmpty()) {
            return ResponseEntity.ok().contentLength(c.getThumbnail().length()).body(c.getThumbnail().getData());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Thumbnail not found");
    }

    @ApiOperation(value = "Return all comics with character", tags = "Characters")
    @ApiResponses({@ApiResponse(code = 200, message = "Return list of comics"), @ApiResponse(code = 404, message = "Character not found"), @ApiResponse(code = 400, message = "Id invalid")})
    @GetMapping(value = "/character/{id}/comics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ComicResponse> getComicsCharacter(@PathVariable("id") String id, @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo, @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize, @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy, @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        CharacterDTO c;
        if (ObjectId.isValid(id)) c = characterServiceImp.getCharacterById(id);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not valid");
        if (c.isEmpty()) {
            List<ComicResponse> comics = characterServiceImp.getCharacterById(id).getComics().stream().map(MappingUtil::mapToComicDTO).map(MappingUtil::mapToComicResponse).collect(Collectors.toList());

            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
            return new PageImpl<>(comics, pageable, comics.size());

        } else throw new CharacterNotFoundException(id);
    }

    @ApiOperation(value = "Update character data", tags = "Characters")
    @ApiResponses({@ApiResponse(code = 200, message = "Character updated"),
            @ApiResponse(code = 404, message = "Character not found"),
            @ApiResponse(code = 400, message = "Invalid id")})
    @PutMapping(value = "/character/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CharacterResponse> putCharacter(@PathVariable(value = "id") String id,
                                                          @RequestPart(value = "name", required = false) Optional<String> name,
                                                          @RequestPart(value = "description", required = false) Optional<String> description,
                                                          @RequestPart(value = "thumbnails", required = false) Optional<MultipartFile> thumbmail,
                                                          @RequestParam(value = "comics", required = false) Optional<String[]> comics) {
        CharacterDTO c;
        if (ObjectId.isValid(id)) c = characterServiceImp.getCharacterById(id);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        if (c.isEmpty()) {
            name.ifPresent(c::setName);
            description.ifPresent(c::setDescription);
            thumbmail.ifPresent(img -> {
                try {
                    c.setThumbnail(ImageFileValidator.validate(img));
                } catch (IOException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image", e);
                }
            });
            comics.ifPresent(comicsList -> Stream.of(comicsList)
                    .filter(ObjectId::isValid)
                    .filter(comicsServiceImp::containComicsById)
                    .map(comicsServiceImp::getComicsById)
                    .map(MappingUtil::mapToComic)
                    .forEach(comic -> c.getComics().add(comic)));
            characterServiceImp.updateCharacter(c);

            // update characters in comic
            c.getComics().forEach(x -> {
                        x.getCharacters().add(MappingUtil.mapToCharacter(c));
                        comicsServiceImp.updateComic(MappingUtil.mapToComicDTO(x));
                    }
            );

            return new ResponseEntity<>(MappingUtil.mapToCharacterResponse(c), HttpStatus.OK);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Character with id not found");

    }
}
