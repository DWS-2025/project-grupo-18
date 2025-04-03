package es.grupo18.jobmatcher.controller.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
        model.addAttribute("post", postService.createEmpty());
        model.addAttribute("isEdit", false);
        return "blog/post_form";
    }

    @PostMapping("/blog/posts/new")
    public String newPost(@RequestParam("image") MultipartFile image,
            @RequestParam String title,
            @RequestParam String content) throws IOException {

        String contentType = null;

        if (!image.isEmpty()) {
            contentType = image.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg")
                    || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                postService.create(title, content, image);
            } else {
                return "redirect:/blog/posts/new?error=invalidImageType";
            }
        }
        return "redirect:/blog/posts";
    }

    @GetMapping("/blog/posts/{postId}")
    public String getPost(Model model, @PathVariable long postId) {
        try {
            PostDTO post = postService.findById(postId);
            model.addAttribute("post", post);
            model.addAttribute("postId", postId);
            model.addAttribute("currentTimeMillis", System.currentTimeMillis());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedTimestamp = post.timestamp() != null ? post.timestamp().format(formatter) : "";
            model.addAttribute("formattedTimestamp", formattedTimestamp);
            return "blog/post_detail";
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{id}/image")
    public ResponseEntity<byte[]> getPostImage(@PathVariable long id) {
        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, postService.findById(id).imageContentType())
                    .body(postService.findById(id).image());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/blog/posts/{postId}/edit")
    public String editPost(Model model, @PathVariable long postId) {
        try {
            model.addAttribute("post", postService.findById(postId));
            model.addAttribute("isEdit", true);
            model.addAttribute("currentTimeMillis", System.currentTimeMillis());
            return "blog/post_form";
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/edit")
    public String updatePost(@PathVariable long postId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam String title,
            @RequestParam String content) throws IOException {
        try {
            postService.update(postId, title, content, image);
            return "redirect:/blog/posts/" + postId;
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/delete")
    public String deletePost(@PathVariable long postId) {
        try {
            postService.delete(postService.findById(postId));
            return "redirect:/blog/posts";
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/reviews/new")
    public String newReview(@PathVariable long postId,
            @RequestParam String text,
            @RequestParam int rating) {
        try {
            reviewService.create(postId, text, rating);
            return "redirect:/blog/posts/" + postId;
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/reviews/{reviewId}/edit")
    public String editReview(Model model, @PathVariable long reviewId, @PathVariable long postId) {
        try {
            model.addAttribute("post", postService.findById(postId));
            model.addAttribute("review", reviewService.findById(reviewId));
            return "blog/review_form";
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/reviews/{reviewId}/edit")
    public String updateReview(@PathVariable long postId,
            @PathVariable long reviewId,
            @RequestParam String text,
            @RequestParam int rating) {
        try {
            reviewService.update(reviewId, text, rating);
            return "redirect:/blog/posts/" + postId;
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable long postId, @PathVariable long reviewId) {
        try {
            reviewService.delete(reviewId, postService.findById(postId));
            return "redirect:/blog/posts/" + postId;
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/reviews/{reviewId}")
    public String getReview(Model model, @PathVariable long postId, @PathVariable long reviewId) {
        try {
            model.addAttribute("post", postService.findById(postId));
            model.addAttribute("review", reviewService.findById(reviewId));
            return "blog/review_detail";
        } catch (NoSuchElementException e) {
            return "post_not_found";
        }
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
