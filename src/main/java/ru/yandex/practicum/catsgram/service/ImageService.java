package ru.yandex.practicum.catsgram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.ImageFileException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Image;
import ru.yandex.practicum.catsgram.model.ImageData;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final PostService postService;

    private final Map<Long, Image> images = new HashMap<>();

    public List<Image> saveImages(long postId, List<MultipartFile> files) {
        postService.findPostById(postId).orElseThrow(() ->
                new NotFoundException("Пост с id = " + postId + " не найден"));

        List<Image> savedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            long imageId = getNextImageId();

            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                throw new ConditionsNotMetException("Имя файла не может быть пустым");
            }

            String filename = imageId + "_" + originalFileName;
            String imageDirectory = "D:\\Catsgram\\Images";
            Path filePath = Paths.get(imageDirectory, filename);

            try {
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new ImageFileException("Ошибка сохранения файла " + originalFileName, e);
            }

            Image image = new Image();
            image.setId(imageId);
            image.setPostId(postId);
            image.setOriginalFileName(originalFileName);
            image.setFilePath(filePath.toString());

            images.put(imageId, image);
            savedImages.add(image);
        }
        return savedImages;
    }

    public ImageData getImageData(long imageId) {
        if (!images.containsKey(imageId)) {
            throw new NotFoundException("Изображение с id = " + imageId + " не найдено");
        }
        Image image = images.get(imageId);
        byte[] data = loadFile(image);
        return new ImageData(data, image.getOriginalFileName());
    }

    private byte[] loadFile(Image image) {
        Path path = Paths.get(image.getFilePath());
        if (!Files.exists(path)) {
            throw new ImageFileException("Файл не найден. Id: " + image.getId()
                    + ", name: " + image.getOriginalFileName());
        }
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new ImageFileException("Ошибка чтения файла. Id: " + image.getId()
                    + ", name: " + image.getOriginalFileName(), e);
        }
    }

    private long getNextImageId() {
        return images.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1;
    }
}
