//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.controller;


import at.technikum.springrestbackend.entity.User;
import at.technikum.springrestbackend.service.FileStorageService;
import at.technikum.springrestbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

//#######################################################################
//#######################################################################
//#######################################################################
// class
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;
    private final UserService userService;

    //POST - upload profile picture
    @PostMapping("/profile-picture")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ){
        UUID userId = (UUID) authentication.getPrincipal();
        String fileName = fileStorageService.uploadFile(file, "profile-picture");
        //delete old PP, if existing
        User user = userService.findEntityById(userId);
        if(user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()){
            fileStorageService.deleteFile(user.getProfilePicture());
        }
        //save new PP
        user.setProfilePicture(fileName);
        userService.saveEntity(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("fileName", fileName));
    }

    //POST - upload survey picture [Admin]
    @PostMapping("/survey-cover/{surveyId}")
    public ResponseEntity<Map<String, String>> uploadSurveyPicture(
            @PathVariable UUID surveyId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ){
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_ADMIN")
        );
        if(!isAdmin){
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not allowed to access this resource"
            );
        }
        String fileName = fileStorageService.uploadFile(file, "survey-cover");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("fileName", fileName));
    }

    //GET - download file
    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<InputStreamResource> downloadFile(
            @PathVariable String folder,
            @PathVariable String filename
    ){
        String filePath = folder + "/" + filename;
        InputStream stream = fileStorageService.downloadFile(filePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(stream));
    }
}
