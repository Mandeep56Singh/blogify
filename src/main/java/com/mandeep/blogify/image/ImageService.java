package com.mandeep.blogify.image;

import com.mandeep.blogify.common.exceptions.ApiException;
import com.mandeep.blogify.constants.ApiError;
import com.mandeep.blogify.constants.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Validated
public class ImageService {

    private final ImageRepository imageRepository;
    private final Path rootLocation;
    private final Tika tika = new Tika();
    private final ImageMapper mapper;

    public ImageService(ImageRepository imageRepository, ImageMapper mapper) throws IOException {
        this.imageRepository = imageRepository;
        this.mapper = mapper;
        Path path = Paths.get(AppConstants.FILE_PATH).toAbsolutePath().normalize();
        Files.createDirectories(path);
        this.rootLocation = path.toRealPath();
    }

    @Transactional
    public ImageDto uploadImage(@NotNull MultipartFile file) throws IOException {

        // checking file type
        String detectedType = tika.detect(file.getInputStream());
        if (!detectedType.startsWith("image/")) {
            throw new ApiException(ApiError.IMAGE_INVALID_TYPE);
        }

        // generating unique name for file stored
        String extension = detectedType.substring(detectedType.indexOf("/") + 1);
        String fileId = UUID.randomUUID() + "." + extension;

        // Security check: Path traversal attack
        Path destinationPath = rootLocation.resolve(fileId).normalize();
        if (!destinationPath.startsWith(rootLocation)) {
            throw new ApiException(ApiError.IMAGE_UPLOAD_FAILED);
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
        return mapper.toDto(storedImage);
    }

    @Transactional
    public Resource loadImage(@NotBlank String id) {
        Image image = getById(id);

        Path filePath = Paths.get(image.getPath());

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ApiException(ApiError.IMAGE_NOT_FOUND);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new ApiException(ApiError.IMAGE_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public Image getById(@NotNull String id) {
        return imageRepository.findById(id).orElseThrow(
                () -> new ApiException(ApiError.IMAGE_NOT_FOUND)
        );
    }
}