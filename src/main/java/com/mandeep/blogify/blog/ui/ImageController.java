package com.mandeep.blogify.blog.ui;


import com.mandeep.blogify.blog.application.constants.ImageConstants;
import com.mandeep.blogify.blog.application.dto.response.ImageDto;
import com.mandeep.blogify.blog.application.dto.response.ImageResourceDto;
import com.mandeep.blogify.blog.application.service.ImageService;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.dto.ResponsePayload;
import com.mandeep.blogify.shared.exceptions.validation.RequestValidator;
import com.mandeep.blogify.shared.exceptions.validation.StringIdWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/images")
class ImageController {

    private final ImageService imageService;
    private final RequestValidator validator;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<ImageDto>> saveImage(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        ResponseDto<ImageDto> imageDto = imageService.uploadImage(file);
        if (!imageDto.success()) {
            return new ResponseEntity<>(imageDto, imageDto.error().status());
        }
        return new ResponseEntity<>(imageDto, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getImage(
            @PathVariable String id
    ) {
        Optional<ResponseDto<ResponsePayload>> violationError = validator.validate(new StringIdWrapper(id));
        if (violationError.isPresent()) {
            return new ResponseEntity<>(violationError, violationError.get().error().status());
        }

        ResponseDto<ImageResourceDto> responseDto = imageService.loadImage(id);

        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        String contentType = Optional.ofNullable(responseDto.metaData())
                .map(m -> m.get(ImageConstants.CONTENT_TYPE))
                .map(Object::toString)
                .orElse("image/jpeg");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(responseDto.data().resource());
    }

}
