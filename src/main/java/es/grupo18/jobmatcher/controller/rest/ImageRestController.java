package es.grupo18.jobmatcher.controller.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.UserService;

@RestController
@RequestMapping("/api/images")
public class ImageRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    // === USER PROFILE IMAGE ===

    @GetMapping("/users/{id}")
    public ResponseEntity<Resource> getUserImage(@PathVariable Long id) {
        try {
            if (userService.findById(id) != null && userService.findById(id).image() != null) {
                Resource image = new ByteArrayResource(userService.findById(id).image());
                return ResponseEntity.ok()
                        .contentLength(userService.findById(id).image().length)
                        .contentType(MediaType.parseMediaType(userService.findById(id).imageContentType() != null
                                ? userService.findById(id).imageContentType()
                                : "image/jpeg"))
                        .body(image);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users/{id}")
    public ResponseEntity<?> uploadUserImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
            throws IOException {
        try {
            userService.save(userService.findById(id), file);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUserImage(@PathVariable Long id) {
        try {
            userService.removeImage();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // === POST IMAGE ===

    @GetMapping("/posts/{id}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) {
        try {
            if (postService.findById(id) != null && postService.findById(id).image() != null
                    && postService.findById(id).image().length > 0) {
                MediaType contentType = MediaType.parseMediaType(
                        postService.findById(id).imageContentType() != null
                                ? postService.findById(id).imageContentType()
                                : "image/jpeg");
                return ResponseEntity.ok()
                        .contentType(contentType)
                        .contentLength(postService.findById(id).image().length)
                        .body(postService.findById(id).image());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/posts/{id}")
    public ResponseEntity<?> uploadPostImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
            throws IOException {
        try {
            postService.update(id, postService.findById(id).title(),
                    postService.findById(id).content(), file);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePostImage(@PathVariable Long id) {
        try {
            postService.update(id, postService.findById(id).title(),
                    postService.findById(id).content(), null);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
}
