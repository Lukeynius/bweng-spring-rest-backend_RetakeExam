//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.service;


import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() throws Exception {
        Field bucketField = FileStorageService.class.getDeclaredField("bucketName");
        bucketField.setAccessible(true);
        bucketField.set(fileStorageService, "test-bucket");
    }

    @Test
    void uploadFile_emptyFile_throwsBadRequest(){
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[0]
        );
        assertThrows(
                ResponseStatusException.class,
                ()->fileStorageService.uploadFile(file, "testfolder")
        );
    }

    @Test
    void uploadFile_invalidContentType_throwsBadRequest(){
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "content".getBytes()
        );
        assertThrows(
                ResponseStatusException.class,
                ()->fileStorageService.uploadFile(file, "testfolder")
        );
    }

    @Test
    void uploadFile_validData_returnsFileName() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "image-content".getBytes()
        );
        when(minioClient.bucketExists(
                any(BucketExistsArgs.class)
        )).thenReturn(true);
        String result = fileStorageService.uploadFile(file, "test-folder");
        assertNotNull(result);
        assert(result.startsWith("test-folder/"));
        assert(result.endsWith(".jpg"));
    }

    @Test
    void deleteFile_callsMinioRemove() throws Exception {
        fileStorageService.deleteFile("test/file.jpg");
        verify(minioClient).removeObject(
                any(RemoveObjectArgs.class));
    }

    @Test
    void downloadFile_minioError_throwsNotFound() throws Exception {
        when(minioClient.getObject(
                any(GetObjectArgs.class)
        )).thenThrow(new RuntimeException("error"));

        assertThrows(
                ResponseStatusException.class,
                () -> fileStorageService.downloadFile("missing.jpg")
        );
    }

}
