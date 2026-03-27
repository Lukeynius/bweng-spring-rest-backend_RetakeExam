//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.service;


import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private static final List<String> ALLOWED_IMAGES_TYPES = List.of("image/jpeg", "image/png", "image/gif", "Image/webp");

    public String uploadFile(MultipartFile file, String folder){
        validateFile(file);
        ensureBucketExists();
        String fileName = folder + "/" + UUID.randomUUID() +getExtension(file.getOriginalFilename());
        try{
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage());
        }
        return fileName;
    }

    public InputStream downloadFile(String fileName){
        try{
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + fileName);
        }
    }

    public void deleteFile(String fileName){
        try{
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file){
        if(file.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        if(!ALLOWED_IMAGES_TYPES.contains(file.getContentType())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed (JPEG, PNG, GIF, WebP)");
        }
    }

    private void ensureBucketExists(){
        try{
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if(!exists){
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e){
            if(!e.getMessage().contains("Bucket already exists")){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create bucket: " + e.getMessage());
            }
        }
    }

    private String getExtension(String fileName){
        if(fileName == null || !fileName.contains(".")){
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

}
