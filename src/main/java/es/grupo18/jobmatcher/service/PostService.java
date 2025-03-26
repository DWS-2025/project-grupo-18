package es.grupo18.jobmatcher.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.repository.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    public List<Post> findAllWithAuthors() { // Returns the posts list with authors
        return postRepository.findAllWithAuthors();
    }

    public List<Post> findAll() { // Returns the posts list in reverse order
        return postRepository.findAll();
    }

    public Optional<Post> findById(long id) { // Returns a post by its id
        return postRepository.findById(id);
    }

    public void save(Post post) { // Saves a post
        post.setAuthor(userService.getLoggedUser());
        postRepository.save(post);
    }

    public void update(Post oldPost, Post updatedPost) {
        oldPost.setTitle(updatedPost.getTitle());
        oldPost.setContent(updatedPost.getTitle());
        oldPost.setTimestamp(updatedPost.getTimestamp());
        oldPost.setImagePath(updatedPost.getImagePath());
        postRepository.save(oldPost);
    }

    public void deleteById(long id) { // Deletes a post by its id

    }

    public void delete(Post post) { // Deletes a post
        postRepository.deleteById(post.getId());
    }

}
