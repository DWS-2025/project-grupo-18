package es.grupo18.jobmatcher.controller.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.dto.ReviewDTO;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.ReviewService;

@Controller
public class BlogController {

    @Autowired
    private PostService postService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/blog/posts")
    public String getFilteredPosts(@RequestParam(required = false) String sort,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String title,
            Model model) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;

        try {
            if (from != null && !from.isBlank()) {
                fromDate = LocalDateTime.parse(from, formatter);
            }
            if (to != null && !to.isBlank()) {
                toDate = LocalDateTime.parse(to, formatter);
            }
        } catch (DateTimeParseException ignored) {
        }

        List<PostDTO> posts = new ArrayList<>(postService.findFilteredPosts(sort, fromDate, toDate, title));

        List<PostView> postViews = posts.stream()
                .map(PostView::new)
                .toList();

        model.addAttribute("posts", postViews);
        model.addAttribute("sort", sort != null ? sort : "");
        model.addAttribute("from", from != null ? from : "");
        model.addAttribute("to", to != null ? to : "");
        model.addAttribute("sortIsAsc", "asc".equals(sort));
        model.addAttribute("sortIsDesc", "desc".equals(sort));
        model.addAttribute("title", title != null ? title : "");
        model.addAttribute("currentTimeMillis", System.currentTimeMillis());

        return "blog/posts";
    }

    @GetMapping("/blog/posts/new")
    public String showNewPostForm(Model model) {
        model.addAttribute("post",
                new PostDTO(null, "", "", LocalDateTime.now(), null, null, null, "", List.of()));
        model.addAttribute("isEdit", false);
        return "blog/post_form";
    }

    @PostMapping("/blog/posts/new")
    public String newPost(@RequestParam("image") MultipartFile image,
            @RequestParam String title,
            @RequestParam String content) throws IOException {

        byte[] imageBytes = null;
        String contentType = null;

        if (!image.isEmpty()) {
            contentType = image.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg")
                    || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                imageBytes = image.getBytes();
            } else {
                return "redirect:/blog/posts/new?error=invalidImageType";
            }
        }

        PostDTO postDTO = new PostDTO(null, title, content, LocalDateTime.now(), null, imageBytes,
                contentType, "", List.of());
        postService.save(postDTO);
        return "redirect:/blog/posts";
    }

    @GetMapping("/blog/posts/{postId}")
    public String getPost(Model model, @PathVariable long postId) {
        PostDTO post = postService.findById(postId);
        if (post != null) {
            model.addAttribute("post", post);
            model.addAttribute("postId", postId);
            model.addAttribute("currentTimeMillis", System.currentTimeMillis());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedTimestamp = post.timestamp() != null ? post.timestamp().format(formatter) : "";
            model.addAttribute("formattedTimestamp", formattedTimestamp);

            return "blog/post_detail";
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{id}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable long id) {
        PostDTO post = postService.findById(id);
        if (post != null && post.image() != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, post.imageContentType())
                    .body(post.image());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/blog/posts/{postId}/edit")
    public String editPost(Model model, @PathVariable long postId) {
        PostDTO post = postService.findById(postId);
        if (post != null) {
            model.addAttribute("post", post);
            model.addAttribute("isEdit", true);
            model.addAttribute("currentTimeMillis", System.currentTimeMillis());
            return "blog/post_form";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/edit")
    public String updatePost(@PathVariable long postId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam String title,
            @RequestParam String content) throws IOException {

        PostDTO existing = postService.findById(postId);
        if (existing == null)
            return "post_not_found";

        byte[] imageBytes = existing.image();
        String contentType = existing.imageContentType();

        if (image != null && !image.isEmpty()) {
            imageBytes = image.getBytes();
            contentType = image.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "image/jpeg";
            }
        }

        PostDTO updated = new PostDTO(postId, title, content, LocalDateTime.now(),
                existing.authorId(), imageBytes, contentType, existing.authorName(), List.of());
        postService.update(existing, updated);

        return "redirect:/blog/posts/" + postId;

    }

    @PostMapping("/blog/posts/{postId}/delete")
    public String deletePost(@PathVariable long postId) {
        PostDTO post = postService.findById(postId);
        if (post != null) {
            postService.delete(post);
            return "redirect:/blog/posts";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/reviews/new")
    public String newReview(@PathVariable long postId,
            @RequestParam String text,
            @RequestParam int rating) {
        PostDTO post = postService.findById(postId);
        if (post != null) {
            ReviewDTO review = new ReviewDTO(null, text, rating, null, postId, null);
            reviewService.save(post, review);
            return "redirect:/blog/posts/" + postId;
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/reviews/{reviewId}/edit")
    public String editReview(Model model, @PathVariable long postId, @PathVariable long reviewId) {
        PostDTO post = postService.findById(postId);
        ReviewDTO review = reviewService.findById(reviewId);
        if (post != null && review != null) {
            model.addAttribute("post", post);
            model.addAttribute("review", review);
            return "blog/review_form";
        }
        return "post_not_found";
    }

    @PostMapping("/blog/posts/{postId}/reviews/{reviewId}/edit")
    public String updateReview(@PathVariable long postId,
            @PathVariable long reviewId,
            @RequestParam String text,
            @RequestParam int rating) {
        ReviewDTO oldReview = reviewService.findById(reviewId);
        if (oldReview != null) {
            ReviewDTO updated = new ReviewDTO(reviewId, text, rating, oldReview.authorId(), oldReview.postId(),
                    oldReview.authorName());
            reviewService.update(oldReview, updated);
            return "redirect:/blog/posts/" + postId;
        }
        return "post_not_found";
    }

    @PostMapping("/blog/posts/{postId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable long postId, @PathVariable long reviewId) {
        PostDTO post = postService.findById(postId);
        if (post != null) {
            reviewService.delete(reviewId, post);
            return "redirect:/blog/posts/" + postId;
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/reviews/{reviewId}")
    public String getReview(Model model, @PathVariable long postId, @PathVariable long reviewId) {
        PostDTO post = postService.findById(postId);
        ReviewDTO review = reviewService.findById(reviewId);
        if (post != null && review != null) {
            model.addAttribute("post", post);
            model.addAttribute("review", review);
            return "blog/review_detail";
        }
        return "post_not_found";
    }

    public static class PostView {
        private final PostDTO post;
        private final String formattedTimestamp;

        public PostView(PostDTO post) {
            this.post = post;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            this.formattedTimestamp = post.timestamp() != null ? post.timestamp().format(formatter) : "";
        }

        public PostDTO getPost() {
            return post;
        }

        public String getFormattedTimestamp() {
            return formattedTimestamp;
        }
    }
}
