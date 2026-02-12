package com.mandeep.blogify.blog.application.service;

import com.mandeep.blogify.blog.application.constants.ImageConstants;
import com.mandeep.blogify.blog.application.dto.response.ImageDto;
import com.mandeep.blogify.blog.application.dto.response.ImageResourceDto;
import com.mandeep.blogify.blog.application.mapping.ImageMapper;
import com.mandeep.blogify.blog.domain.entity.Image;
import com.mandeep.blogify.blog.domain.exceptions.ImageUploadError;
import com.mandeep.blogify.blog.domain.repository.ImageRepository;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.AppProblem;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final Path rootLocation;
    private final Tika tika = new Tika();
    private final ImageMapper mapper;

    public ImageService(ImageRepository imageRepository, ImageMapper mapper) throws IOException {
        this.imageRepository = imageRepository;
        this.mapper = mapper;
        Path path = Paths.get(ImageConstants.FILE_PATH).toAbsolutePath().normalize();
        Files.createDirectories(path);
        this.rootLocation = path.toRealPath();
    }

    @Transactional
    public ResponseDto<ImageDto> uploadImage(MultipartFile file) throws IOException {

        // checking file type
        String detectedType = tika.detect(file.getInputStream());
        if (!detectedType.startsWith("image/")) {
            AppProblem appProblem = AppProblem.getDetail(ImageUploadError.IMAGE_INVALID_TYPE);
            return ResponseDto.failure(appProblem);
        }

        // generating unique name for file stored
        String extension = detectedType.substring(detectedType.indexOf("/") + 1);
        String fileId = UUID.randomUUID() + "." + extension;

        // Security check: Path traversal attack
        Path destinationPath = rootLocation.resolve(fileId).normalize();
        if (!destinationPath.startsWith(rootLocation)) {
            AppProblem appProblem = AppProblem.getDetail(ImageUploadError.IMAGE_UPLOAD_FAILED);
            return ResponseDto.failure(appProblem);
        }

        // efficiently store into local system
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }

        Image image = new Image(
                fileId,
                file.getOriginalFilename(),
                destinationPath.toString(),
                file.getSize(),
                detectedType
        );

        Image storedImage = imageRepository.save(image);
        return ResponseDto.success(mapper.toDto(storedImage));
    }

    @Transactional
    public ResponseDto<ImageResourceDto> loadImage(String id) {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isEmpty()) {
            return ResponseDto.failure(ImageUploadError.IMAGE_NOT_FOUND);
        }

        Path filePath = Paths.get(image.get().getPath());

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseDto.failure(ImageUploadError.IMAGE_NOT_FOUND);
            }
            return ResponseDto.success(new ImageResourceDto(resource), Map.of(ImageConstants.CONTENT_TYPE, image.get().getContentType()));
        } catch (MalformedURLException ex) {
            return ResponseDto.failure(ImageUploadError.IMAGE_NOT_FOUND);
        }
    }


}