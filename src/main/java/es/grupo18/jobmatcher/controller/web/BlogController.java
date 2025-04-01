package es.grupo18.jobmatcher.controller.web;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.model.Review;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.ReviewService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BlogController {

    @Autowired
    private PostService postService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/blog/posts")
    public String getFilteredPosts(
            @RequestParam(required = false) String sort,
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
        } catch (DateTimeParseException e) {
        }

        List<Post> posts = postService.findFilteredPosts(sort, fromDate, toDate, title);

        model.addAttribute("posts", posts);
        model.addAttribute("sort", sort != null ? sort : "");
        model.addAttribute("from", from != null ? from : "");
        model.addAttribute("to", to != null ? to : "");
        model.addAttribute("sortIsAsc", "asc".equals(sort));
        model.addAttribute("sortIsDesc", "desc".equals(sort));
        model.addAttribute("title", title != null ? title : "");

        return "blog/posts";
    }

    @GetMapping("/blog/posts/new")
    public String showNewPostForm(Model model) {
        if (!model.containsAttribute("post")) {
            model.addAttribute("post", new Post());
        }
        model.addAttribute("isEdit", false);
        return "blog/post_form";
    }

    @PostMapping("/blog/posts/new")
    public String newPost(@RequestParam("imageFile") MultipartFile imageFile, Post post) {
        try {
            if (!imageFile.isEmpty()) {
                post.setImage(imageFile.getBytes());
                String contentType = imageFile.getContentType();
                if(contentType == null || contentType.isBlank()){
                    contentType = "image/jpeg";
                }
                post.setImageContentType(contentType);
            }
            post.setTimestamp(LocalDateTime.now());
            postService.save(post);
            return "redirect:/blog/posts";
        } catch (IOException e) {
            return "error";
        }
    }

    @GetMapping("/blog/posts/{postId}")
    public String getPost(Model model, @PathVariable long postId) {
        Optional<Post> postOpt = postService.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            if (post.getReviews() == null) {
                post.setReviews(new ArrayList<>());
            }
            model.addAttribute("post", post);
            model.addAttribute("postId", postId); // Añade postId explícitamente
            return "blog/post_detail";
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/edit")
    public String editPost(Model model, @PathVariable long postId) {
        Optional<Post> post = postService.findById(postId);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            model.addAttribute("isEdit", true);
            return "blog/post_form";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/edit")
    public String updatePost(@PathVariable long postId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Post updatedPost) {
        Optional<Post> op = postService.findById(postId);
        if (op.isPresent()) {
            Post oldPost = op.get();
            try {
                if (imageFile != null && !imageFile.isEmpty()) {
                    updatedPost.setImage(imageFile.getBytes());
                    String contentType = imageFile.getContentType();
                    if(contentType == null || contentType.isBlank()){
                        contentType = "image/jpeg";
                    }
                    updatedPost.setImageContentType(contentType);
                } else {
                    updatedPost.setImage(oldPost.getImage());
                    updatedPost.setImageContentType(oldPost.getImageContentType());
                }
                updatedPost.setTimestamp(LocalDateTime.now());
                postService.update(oldPost, updatedPost);
                return "redirect:/blog/posts/" + postId;
            } catch (IOException e) {
                return "error";
            }
        }
        return "post_not_found";
    }

    @PostMapping("/blog/posts/{postId}/delete")
    public String deletePost(@PathVariable long postId) {
        Optional<Post> op = postService.findById(postId);
        if (op.isPresent()) {
            postService.delete(op.get());
            return "redirect:/blog/posts";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/blog/posts/{postId}/reviews/new")
    public String newReview(@PathVariable long postId, Review review) {
        Optional<Post> op = postService.findById(postId);
        if (op.isPresent()) {
            Post post = op.get();
            reviewService.save(post, review);
            return "redirect:/blog/posts/" + postId;
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/reviews/{reviewId}/edit")
    public String editReview(Model model, @PathVariable long postId, @PathVariable long reviewId) {
        Optional<Post> post = postService.findById(postId);
        if (post.isPresent()) {
            Optional<Review> review = reviewService.findById(reviewId);
            if (review.isPresent()) {
                model.addAttribute("post", post.get());
                model.addAttribute("review", review.get());
                return "blog/review_form";
            }
        }
        return "post_not_found";
    }

    @PostMapping("/blog/posts/{postId}/reviews/{reviewId}/edit")
    public String updateReview(@PathVariable long postId, @PathVariable long reviewId, Review updatedReview) {
        Optional<Review> reviewOpt = reviewService.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            review.setText(updatedReview.getText());
            review.setRating(updatedReview.getRating());
            reviewService.update(review, updatedReview);
            return "redirect:/blog/posts/" + postId;
        }
        return "post_not_found";
    }

    @PostMapping("/blog/posts/{postId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable long postId, @PathVariable long reviewId) {
        Optional<Post> op = postService.findById(postId);
        if (op.isPresent()) {
            Post post = op.get();
            reviewService.delete(reviewId, post);
            return "redirect:/blog/posts/" + postId;
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/blog/posts/{postId}/reviews/{reviewId}")
    public String getReview(Model model, @PathVariable long postId, @PathVariable long reviewId) {
        Optional<Post> postOpt = postService.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            Optional<Review> reviewOpt = post.getReviews().stream()
                .filter(review -> review.getId() == reviewId)
                .findFirst();
            if (reviewOpt.isPresent()) {
                model.addAttribute("post", post);
                model.addAttribute("review", reviewOpt.get());
                return "blog/review_detail";
            }
        }
    return "post_not_found"; 
}

}
