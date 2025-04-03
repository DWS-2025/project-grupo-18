package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
        UserDTO user = userService.findById(id);
        if (user != null && user.imageFile() != null) {
            Resource image = new ByteArrayResource(user.imageFile());
            return ResponseEntity.ok()
                    .contentLength(user.imageFile().length)
                    .contentType(MediaType.parseMediaType(user.imageContentType() != null
                            ? user.imageContentType()
                            : "image/jpeg"))
                    .body(image);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/users/{id}")
    public ResponseEntity<?> uploadUserImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
            throws IOException {
        UserDTO user = userService.findById(id);
        if (user == null)
            return ResponseEntity.notFound().build();
        if (file.isEmpty())
            return ResponseEntity.badRequest().body("Empty image file");

        userService.save(user, file);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUserImage(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        if (user == null)
            return ResponseEntity.notFound().build();

        userService.removeImage();
        return ResponseEntity.noContent().build();
    }

    // === POST IMAGE ===

    @GetMapping("/posts/{id}")
    public ResponseEntity<byte[]> getPostImage(@PathVariable Long id) {
        PostDTO post = postService.findById(id);
        if (post != null && post.image() != null && post.image().length > 0) {
            MediaType contentType = MediaType.parseMediaType(
                    post.imageContentType() != null ? post.imageContentType() : "image/jpeg");
            return ResponseEntity.ok()
                    .contentType(contentType)
                    .contentLength(post.image().length)
                    .body(post.image());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/posts/{id}")
    public ResponseEntity<?> uploadPostImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
            throws IOException {
        PostDTO post = postService.findById(id);
        if (post == null)
            return ResponseEntity.notFound().build();
        if (file.isEmpty())
            return ResponseEntity.badRequest().body("Empty image file");

        PostDTO updated = new PostDTO(
                post.id(), post.title(), post.content(), post.timestamp(), post.authorId(),
                file.getBytes(), file.getContentType(), "");

        postService.update(post, updated);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePostImage(@PathVariable Long id) {
        PostDTO post = postService.findById(id);
        if (post == null)
            return ResponseEntity.notFound().build();

        PostDTO updated = new PostDTO(
                post.id(), post.title(), post.content(), post.timestamp(), post.authorId(),
                null, null, "");

        postService.update(post, updated);
        return ResponseEntity.noContent().build();
    }

}
