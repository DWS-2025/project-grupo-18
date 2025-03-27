package es.grupo18.jobmatcher.controller;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.service.PostService;

@Controller
public class ImageController {

    @Autowired
    private PostService postService;

    @GetMapping("/blog/posts/{postId}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable long postId) {
        Optional<Post> postOpt = postService.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            byte[] imageData = post.getImage();
            String contentType = post.getImageContentType();
            
            if (imageData != null && imageData.length > 0) {
                MediaType mediaType;
                try {
                    mediaType = MediaType.parseMediaType(contentType != null ? contentType : "image/jpeg");
                } catch (Exception e) {
                    mediaType = MediaType.IMAGE_JPEG;
                }
                
                return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(imageData.length)
                    .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
                    .body(imageData);
            }
        }
        return ResponseEntity.notFound().build();
}
}