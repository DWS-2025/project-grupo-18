package es.grupo18.jobmatcher.service;

import java.util.ArrayList;

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

                User testing2 = new User("Testing2", "testing@gmail.com", "password123", "999999999", "Barcelona",
                                "I'm a testing user", 2, null, null, null, null);
                userRepository.save(testing2);

                Post post = new Post("Test", "This is a test post", "12-03-2025", "https://www.google.com");

                Review review = new Review("This is a test review", 5);
                review.setAuthor(testing);
                post.getReviews().add(review);

                Review review2 = new Review("This is a test review 2", 3);
                review2.setAuthor(testing2);
                post.getReviews().add(review2);

                postRepository.save(post);

                for (int i = 1; i <= 30; i++) {
                        Company company = new Company();
                        company.setName("Empresa " + i);
                        company.setEmail("empresa" + i + "@gmail.com");
                        company.setLocation("Ciudad " + i);
                        company.setBio("Descripción de la Empresa " + i);
                        company.setImagePath("");
                        company.setFavouriteUsersList(new ArrayList<>());
                        companyRepository.save(company);
                }
        }

}
