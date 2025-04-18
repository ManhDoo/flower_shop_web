package com.example.FlowerShop.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    @Autowired
    private Cloudinary cloudinary;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            // Đọc ảnh từ file
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            // Nén ảnh (resize)
            BufferedImage resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, 800);

            // Chuyển ảnh về byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            // Upload lên Cloudinary
            var uploadResult = cloudinary.uploader().upload(imageBytes, ObjectUtils.asMap(
                    "folder", "products",
                    "format", "jpg"
            ));

            // Lấy URL ảnh
            String imageUrl = (String) uploadResult.get("secure_url");

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "imageUrl", imageUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Lỗi khi xử lý ảnh: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "Lỗi server: " + e.getMessage()
            ));
        }
    }
}