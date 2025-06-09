package es.grupo18.jobmatcher.controller.web;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.dto.ReviewDTO;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.ReviewService;
import es.grupo18.jobmatcher.service.UserService;

@Controller
public class BlogController {

    @Autowired
    private PostService postService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    @GetMapping("/blog/posts")
    public String getFilteredPosts(@RequestParam(required = false) String sort,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String title,
            Model model) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
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

        List<Map<String, Object>> postMaps = posts.stream()
                .map(post -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", post.id());
                    map.put("title", post.title());
                    map.put("content", post.content());
                    map.put("authorName", post.authorName());
                    map.put("timestamp", post.timestamp());
                    map.put("formattedTimestamp", post.timestamp() != null
                            ? post.timestamp().format(displayFormatter)
                            : "");
                    return map;
                }).toList();

        model.addAttribute("posts", postMaps);
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
    public String newPost(Model model,
            @RequestParam("image") MultipartFile image,
            @RequestParam String title,
            @RequestParam String content) throws IOException {

        if (title.isBlank() || content.isBlank()) {
            PostDTO post = new PostDTO(null, title, content, null, null, null, null, "", List.of());
            model.addAttribute("post", post);
            model.addAttribute("isEdit", false);
            model.addAttribute("error", "Title and content cannot be empty");
            return "blog/post_form";
        }

        if (!image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/jpg")
                    || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                postService.create(title, content, image);
            } else {
                PostDTO post = new PostDTO(null, title, content, null, null, null, null, "", List.of());
                model.addAttribute("post", post);
                model.addAttribute("isEdit", false);
                model.addAttribute("error", "Invalid image type");
                return "blog/post_form";
            }
        } else {
            postService.create(title, content, null);
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

            boolean canEdit = false;
            try {
                long loggedId = userService.getLoggedUser().id();
                canEdit = postService.canEditOrDeletePost(postId, loggedId);
            } catch (UsernameNotFoundException | NoSuchElementException ex) {
            }
            model.addAttribute("canEditPost", canEdit);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String formattedTimestamp = post.timestamp() != null
                    ? post.timestamp().format(formatter)
                    : "";
            model.addAttribute("formattedTimestamp", formattedTimestamp);

            return "blog/post_detail";
        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
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
            if (!postService.findById(postId).authorId().equals(userService.getLoggedUser().id()) &&
                    !userService.getLoggedUser().roles().contains("ADMIN")) {
                return "error/403";
            }
            model.addAttribute("post", postService.findById(postId));
            model.addAttribute("isEdit", true);
            model.addAttribute("currentTimeMillis", System.currentTimeMillis());
            return "blog/post_form";
        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/edit")
    public String updatePost(Model model,
            @PathVariable long postId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam String title,
            @RequestParam String content) throws IOException {
        try {
            if (!postService.findById(postId).authorId().equals(userService.getLoggedUser().id()) &&
                    !userService.getLoggedUser().roles().contains("ADMIN")) {
                return "error/403";
            }

            if (image != null && !image.isEmpty()) {
                String contentType = image.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    PostDTO post = postService.findById(postId);
                    model.addAttribute("post", post);
                    model.addAttribute("isEdit", true);
                    model.addAttribute("error", "Invalid image type");
                    return "blog/post_form";
                }
            }

            postService.update(postId, title, content, image);
            return "redirect:/blog/posts/" + postId + "/edit";

        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/delete")
    public String deletePost(@PathVariable long postId) {
        try {
            long userId = userService.getLoggedUser().id();
            if (!postService.canEditOrDeletePost(postId, userId)) {
                return "error/403";
            }
            postService.delete(postService.findById(postId));
            return "redirect:/blog/posts";
        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/reviews/new")
    public String newReview(Model model,
            @PathVariable long postId,
            @RequestParam String text,
            @RequestParam int rating) {
        try {
            if (text.isBlank()) {
                model.addAttribute("post", postService.findById(postId));
                model.addAttribute("review", new ReviewDTO(null, text, rating, null, postId, null));
                model.addAttribute("error", "Comment cannot be empty");
                return "blog/review_form";
            }

            reviewService.create(postId, text, rating);
            return "redirect:/blog/posts/" + postId;
        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/reviews/{reviewId}/edit")
    public String editReview(Model model, @PathVariable long reviewId, @PathVariable long postId) {
        try {
            if (!reviewService.findById(reviewId).authorId().equals(userService.getLoggedUser().id()) &&
                    !userService.getLoggedUser().roles().contains("ADMIN")) {
                return "error/403";
            }
            model.addAttribute("post", postService.findById(postId));
            model.addAttribute("review", reviewService.findById(reviewId));
            return "blog/review_form";
        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/reviews/{reviewId}/edit")
    public String updateReview(Model model,
            @PathVariable long postId,
            @PathVariable long reviewId,
            @RequestParam String text,
            @RequestParam int rating) {
        try {
            long userId = userService.getLoggedUser().id();
            if (!reviewService.canEditOrDeleteReview(reviewId, userId)) {
                return "error/403";
            }
            if (text.isBlank()) {
                model.addAttribute("post", postService.findById(postId));
                model.addAttribute("review", reviewService.findById(reviewId));
                model.addAttribute("error", "Comment cannot be empty");
                return "blog/review_form";
            }

            reviewService.update(reviewId, text, rating);
            return "redirect:/blog/posts/" + postId;
        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable long postId, @PathVariable long reviewId) {
        try {
            long userId = userService.getLoggedUser().id();
            if (!reviewService.canEditOrDeleteReview(reviewId, userId)) {
                return "error/403";
            }
            reviewService.delete(reviewId, postService.findById(postId));
            return "redirect:/blog/posts/" + postId;
        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/reviews/{reviewId}")
    public String getReview(Model model,
            @PathVariable long postId,
            @PathVariable long reviewId) {
        try {
            model.addAttribute("post", postService.findById(postId));
            ReviewDTO review = reviewService.findById(reviewId);
            model.addAttribute("review", review);

            boolean canEdit = false;
            if (SecurityContextHolder.getContext().getAuthentication() != null &&
                    SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                    !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

                long userId = userService.getLoggedUser().id();
                canEdit = reviewService.canEditOrDeleteReview(reviewId, userId);
            }
            model.addAttribute("canEditReview", canEdit);

            return "blog/review_detail";
        } catch (NoSuchElementException e) {
            return "blog/post_not_found";
        }
    }

}
