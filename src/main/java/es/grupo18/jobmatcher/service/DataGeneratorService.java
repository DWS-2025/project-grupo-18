package es.grupo18.jobmatcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.Post;
import es.grupo18.jobmatcher.model.Review;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.CompanyRepository;
import es.grupo18.jobmatcher.repository.PostRepository;
import es.grupo18.jobmatcher.repository.UserRepository;
import jakarta.annotation.PostConstruct;

@Service
public class DataGeneratorService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PostRepository postRepository;

    @PostConstruct
    public void init() {

        User testing = new User("Testing", "testing@gmail.com", "password123", "666666666", "Madrid",
                "I'm a testing user", 2, null, null, null, null);
        userRepository.save(testing);

        User martina = new User("Martina", "martinini@gmail.com", "martinini123", "659801423", "Vicálvaro",
                "Busco a alguien para que haga mi trabajo", 2,
                null, null, null, null);
        userRepository.save(martina);

        Post post = new Post("Test", "This is a test post", "12-03-2025", "https://www.google.com");

        Review review = new Review("This is a test review", 5);
        review.setAuthor(testing);
        post.getReviews().add(review);

        Review review2 = new Review("This is a test review 2", 3);
        review2.setAuthor(martina);
        post.getReviews().add(review2);

        postRepository.save(post);

        Company company = new Company("Test Company", "test@gmail.com", "password123", "Madrid",
                "We are a testing company", "https://www.google.com", null);
        companyRepository.save(company);

        Company company2 = new Company("Test Company 2", "test2@gmail.com", "password123", "Barcelona",
                "We are a testing company again", "https://www.google.com", null);
        companyRepository.save(company2);
    }

}
