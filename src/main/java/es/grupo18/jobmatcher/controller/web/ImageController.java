package es.grupo18.jobmatcher.controller.web;

import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.service.PostService;

@Controller
public class ImageController {

    @Autowired
    private PostService postService;

    @GetMapping("/blog/posts/{postId}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable long postId) {
        PostDTO post = postService.findById(postId);
        if (post != null && post.image() != null && post.image().length > 0) {
            String contentType = post.imageContentType();
            MediaType mediaType;
            try {
                mediaType = MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg");
            } catch (Exception e) {
                mediaType = MediaType.IMAGE_JPEG;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(post.image().length)
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                    .body(post.image());
        }
        return ResponseEntity.notFound().build();
    }

}
