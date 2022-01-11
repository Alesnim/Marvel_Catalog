package com.stm.marvelcatalog.controller;

import com.stm.marvelcatalog.model.Character;
import com.stm.marvelcatalog.services.CharacterService;
import com.stm.marvelcatalog.util.AppConstants;
import com.stm.marvelcatalog.util.MappingUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/v1/public")
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
    public Page<Character> allCharacters(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        List<Character> all = characterServiceImp.getAllCharacter()
                .stream()
                .map(MappingUtil::mapToCharacter)
                .collect(Collectors.toList());
        return new PageImpl<>(all, pageable, all.size());
    }


}
