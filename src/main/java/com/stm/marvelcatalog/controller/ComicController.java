package com.stm.marvelcatalog.controller;


import com.stm.marvelcatalog.DTO.CharacterDTO;
import com.stm.marvelcatalog.DTO.CharacterResponse;
import com.stm.marvelcatalog.DTO.ComicDTO;
import com.stm.marvelcatalog.DTO.ComicResponse;
import com.stm.marvelcatalog.controller.validators.ImageFileValidator;
import com.stm.marvelcatalog.exceptions.CharacterNotFoundException;
import com.stm.marvelcatalog.model.Comic;
import com.stm.marvelcatalog.services.CharacterService;
import com.stm.marvelcatalog.services.ComicsService;
import com.stm.marvelcatalog.util.AppConstants;
import com.stm.marvelcatalog.util.MappingUtil;
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

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Controller of Comics endpoint
 */
@RequestMapping("/v2/public")
@RestController
public class ComicController {

    private final ComicsService comicsServiceImp;
    private final CharacterService characterServiceImp;

    public ComicController(ComicsService comicsServiceImp, CharacterService characterServiceImp) {
        this.comicsServiceImp = comicsServiceImp;
        this.characterServiceImp = characterServiceImp;
    }


    /**
     * Method for search all comics in DB
     *
     * @param pageNo   Number page
     * @param pageSize Count elements on page
     * @param sortBy   Sort by pole name
     * @param sortDir  Sort direction
     * @return Page of result
     */
    @ApiOperation(value = "Endpoint return all list of comics", tags = "Comics")
    @ApiResponses(
            @ApiResponse(code = 200, message = "Success return list of comics")
    )
    @GetMapping("/comics")
    public Page<ComicResponse> allComics(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        List<ComicResponse> all = comicsServiceImp.getAllComics()
                .stream()
                .map(MappingUtil::mapToComicResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(all, pageable, all.size());
    }


    /**
     * Method for comics create
     * @param img Image for preview comic (png, jpg, gif)
     * @param name Name of comic
     * @param description Text of comic description
     * @param characters List if id character
     * @return Comic formatting response
     */
    @ApiOperation(value = "Create new comics", tags = "Comics")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Success create"),
                    @ApiResponse(code = 400, message = "Invalid data")
            }
    )
    @PostMapping(value = "/comics", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_JPEG_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ComicResponse> createComic(
            @RequestPart MultipartFile img,
            @RequestPart String name,
            @RequestPart String description,
            @RequestParam(required = false) String[] characters) {
        try {
            ComicDTO dto = new ComicDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setThumbnail(ImageFileValidator.validate(img));
            if (characters.length == 0) dto.setCharacters(Collections.emptySet());
            else {
                dto.setCharacters(Stream.of(characters)
                        .filter(ObjectId::isValid)
                        .filter(characterServiceImp::containCharacterById)
                        .map(characterServiceImp::getCharacterById)
                        .map(MappingUtil::mapToCharacter)
                        .collect(Collectors.toSet()));
            }
            Comic c = comicsServiceImp.insertComics(dto);
            // update character when save comics
            if (characters.length > 0) {
                c.getCharacters().forEach(x -> {
                    x.getComics().add(c);
                    characterServiceImp.insertCharacter(MappingUtil.mapToCharacterDTO(x));
                });
            }
            dto.setId(c.getId().toHexString());
            return new ResponseEntity<>(MappingUtil.mapToComicResponse(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Return Page of character in comic
     * @param id valid object id comic
     * @param pageNo Page number
     * @param pageSize Count of page element
     * @param sortBy Sort parameter
     * @param sortDir Sort direction
     * @return Page contain formatted character
     */
    @ApiOperation(value = "Return all character in comics", tags = "Comics")
    @ApiResponses({@ApiResponse(code = 200, message = "Return list of characters"),
            @ApiResponse(code = 404, message = "Comic not found"),
            @ApiResponse(code = 400, message = "Id invalid")})
    @GetMapping(value = "/comics/{id}/charactes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CharacterResponse> getComicsCharacter(@PathVariable("id") String id,
                                                      @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                      @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
                                                      @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
                                                      @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        ComicDTO c;
        if (ObjectId.isValid(id)) c = comicsServiceImp.getComicsById(id);
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not valid");
        if (c.isEmpty()) {
            List<CharacterResponse> comics = comicsServiceImp.getComicsById(id).getCharacters()
                    .stream()
                    .map(MappingUtil::mapToCharacterDTO)
                    .map(MappingUtil::mapToCharacterResponse)
                    .collect(Collectors.toList());

            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
            return new PageImpl<>(comics, pageable, comics.size());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comics not found");
    }

    /**
     * Return image of comic
     * @param id Hex-string id of comic
     * @return Byte[] contain image
     */
    @ApiOperation(value = "Returns url of thumbnails", tags = "Comics")
    @GetMapping(value = "/comics/{id}/thumbnails", produces = {MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> thumbnailsReturn(@PathVariable("id") String id) {
        ComicDTO c = comicsServiceImp.getComicsById(id);
        if (!c.isEmpty())
            return ResponseEntity.ok()
                    .contentLength(c.getThumbnail().length())
                    .body(c.getThumbnail().getData());

        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Update comic data
     * @param id Hex-string valid id of comic
     * @param name New name of comic
     * @param description New description of comic
     * @param thumbmail New image of comic
     * @param characterList List of valid character id
     * @return Formatted comic with changed data
     */
    @ApiOperation(value = "Update comic data", tags = "Comics")
    @ApiResponses({@ApiResponse(code = 200, message = "Comic updated"),
            @ApiResponse(code = 404, message = "Comic not found"),
            @ApiResponse(code = 400, message = "Invalid id")})
    @PutMapping(value = "/comics/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComicResponse> putComics(@PathVariable(value = "id") String id,
                                                   @RequestPart(value = "name", required = false) Optional<String> name,
                                                   @RequestPart(value = "description", required = false) Optional<String> description,
                                                   @RequestPart(value = "thumbnails", required = false) Optional<MultipartFile> thumbmail,
                                                   @RequestParam(value = "characters", required = false) Optional<String[]> characterList) {
        ComicDTO c;
        if (ObjectId.isValid(id)) c = comicsServiceImp.getComicsById(id);
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
            characterList.ifPresent(comicsList -> Stream.of(comicsList)
                    .filter(ObjectId::isValid)
                    .filter(characterServiceImp::containCharacterById)
                    .map(characterServiceImp::getCharacterById)
                    .map(MappingUtil::mapToCharacter)
                    .forEach(character -> c.getCharacters().add(character)));
            comicsServiceImp.updateComic(c);
            // update characters in comics
            c.getCharacters().forEach(x -> {
                        x.getComics().add(MappingUtil.mapToComic(c));
                        characterServiceImp.updateCharacter(MappingUtil.mapToCharacterDTO(x));
                    }
            );
            return new ResponseEntity<>(MappingUtil.mapToComicResponse(c), HttpStatus.OK);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Character with id not found");
    }
}
