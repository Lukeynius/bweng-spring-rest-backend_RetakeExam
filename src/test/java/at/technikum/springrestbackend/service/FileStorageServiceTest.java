//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.service;


import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void uploadFile_emptyFile_throwsBadRequest(){
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertThrows(ResponseStatusException.class, ()->fileStorageService.uploadFile(file, "testfolder"));
    }

    @Test
    void uploadFile_invalidContentType_throwsBadRequest(){
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        assertThrows(ResponseStatusException.class, ()->fileStorageService.uploadFile(file, "testfolder"));
    }
}
