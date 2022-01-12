package com.stm.marvelcatalog.controller;


import com.stm.marvelcatalog.DTO.CharacterDTO;
import com.stm.marvelcatalog.DTO.ComicDTO;
import com.stm.marvelcatalog.DTO.ComicResponse;
import com.stm.marvelcatalog.controller.validators.ImageFileValidator;
import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.model.Comic;
import com.stm.marvelcatalog.services.CharacterService;
import com.stm.marvelcatalog.services.ComicsService;
import com.stm.marvelcatalog.util.AppConstants;
import com.stm.marvelcatalog.util.MappingUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
            @RequestParam String[] comics
    ) {

        try {
            ComicDTO dto = new ComicDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setThumbnail(ImageFileValidator.validate(img));

            if (comics.length == 0) dto.setCharacters(null);
            else {
                dto.setCharacters(Stream.of(comics)
                        .filter(characterServiceImp::containCharacterById)
                        .map(characterServiceImp::getCharacterById)
                        .map(MappingUtil::mapToCharacter)
                        .collect(Collectors.toList()));
            }
            Comic c = comicsServiceImp.insertComics(dto);
            dto.setId(c.getId().toHexString());
            return new ResponseEntity<>(MappingUtil.mapToComicResponse(dto), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }


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
}
