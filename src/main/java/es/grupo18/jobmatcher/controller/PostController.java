package es.grupo18.jobmatcher.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.model.Review;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/posts")
    public String getPosts(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "posts";
    }

    @PostMapping("/posts/new")
    public String newPost(Model model, Post post) {
        postService.save(post);
        return "saved_post";
    }

    @GetMapping("/posts/{id}")
    public String getPost(Model model, @PathVariable long id) {
        Optional<Post> post = postService.findById(id);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            return "show_post";
        } else {
            return "post_not_found";
        }
    }

    @GetMapping("/posts/{id}/edit")
    public String editPost(Model model, @PathVariable long id) {
        Optional<Post> post = postService.findById(id);
        if (post.isPresent()) {
            model.addAttribute("post", post.get());
            return "edit_post";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/posts/{id}/edit")
    public String updatePost(Model model, @PathVariable long id, Post updatedPost) {
        Optional<Post> op = postService.findById(id);
        if (op.isPresent()) {
            Post oldPost = op.get();
            postService.update(oldPost, updatedPost);
            return "redirect:/posts/" + id;
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable long id) {
        Optional<Post> op = postService.findById(id);
        if (op.isPresent()) {
            postService.delete(op.get());
            return "redirect:/";
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/posts/{postId}/reviews/new")
    public String newReview(@PathVariable long postId, Review review) {
        Optional<Post> op = postService.findById(postId);
        if (op.isPresent()) {
            Post post = op.get();
            reviewService.save(post, review);
            return "redirect:/posts/" + postId;
        } else {
            return "post_not_found";
        }
    }

    @PostMapping("/posts/{postId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long postId, @PathVariable Long reviewId) {
        Optional<Post> op = postService.findById(postId);
        if (op.isPresent()) {
            Post post = op.get();
            reviewService.delete(reviewId, post);
            return "redirect:/posts/" + postId;
        } else {
            return "post_not_found";
        }
    }

}
