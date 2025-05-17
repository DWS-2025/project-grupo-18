package es.grupo18.jobmatcher.controller.rest;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.grupo18.jobmatcher.dto.PostDTO;
import es.grupo18.jobmatcher.service.PostService;
import es.grupo18.jobmatcher.service.UserService;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {
    private static final PolicyFactory TEXT_SANITIZER = Sanitizers.FORMATTING;

    @Autowired 
    private PostService postService;
    
    @Autowired 
    private UserService userService;

    private String sanitizeText(String text) {
        return text != null 
            ? TEXT_SANITIZER.sanitize(text) 
            : null;
    }

    private Long getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext()
                                                .getAuthentication()
                                                .getPrincipal();
        if (principal instanceof UserDetails ud) {
            return userService.findByEmail(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
                .getId();
        }
        throw new RuntimeException("Usuario no autenticado");
    }

    @GetMapping({ "", "/" })
    public ResponseEntity<Collection<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping({ "/{id}", "/{id}/" })
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping({ "", "/" })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {
        Long userId = getAuthenticatedUserId();
        PostDTO dto = new PostDTO(
            null,
            sanitizeText(postDTO.title()),
            sanitizeText(postDTO.content()),
            LocalDateTime.now(),
            userId,
            postDTO.image(),
            postDTO.imageContentType(),
            sanitizeText(postDTO.authorName()),
            postDTO.reviews() != null ? postDTO.reviews() : List.of()
        );
        PostDTO saved = postService.save(dto);
        URI loc = ServletUriComponentsBuilder.fromCurrentRequest()
                                             .path("/{id}")
                                             .buildAndExpand(saved.id())
                                             .toUri();
        return ResponseEntity.created(loc).body(saved);
    }

    @PutMapping({ "/{id}", "/{id}/" })
    @PreAuthorize("hasRole('ADMIN') or @postService.canEditOrDeletePost(#id, @userService.getLoggedUser().id())")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestBody PostDTO postDTO) {
        try {
            PostDTO existing = postService.findById(id);
            PostDTO updated  = postService.update(existing, postDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping({ "/{id}", "/{id}/" })
    @PreAuthorize("hasRole('ADMIN') or @postService.canEditOrDeletePost(#id, @userService.getLoggedUser().id())")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        System.out.println(">>> INTENTANDO BORRAR POST ID: " + id);
        try {
            PostDTO post = postService.findById(id);
            System.out.println(">>> POST ENCONTRADO: " + post.title());
            postService.deleteById(id);
            System.out.println(">>> POST BORRADO");
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            System.out.println(">>> ERROR AL BORRAR: " + ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/filter")
    public ResponseEntity<Collection<PostDTO>> filterPosts(
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String title) {

        if (sort != null && !sort.matches("^[a-zA-Z0-9]+$")) {
            return ResponseEntity.badRequest().build();
        }

        LocalDateTime fromDate = null, toDate = null;
        try {
            if (from != null) 
                fromDate = LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME);
            if (to != null) 
                toDate   = LocalDateTime.parse(to,   DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }

        String safeTitle = sanitizeText(title);
        return ResponseEntity.ok(
            postService.findFilteredPosts(sort, fromDate, toDate, safeTitle)
        );
    }
}
