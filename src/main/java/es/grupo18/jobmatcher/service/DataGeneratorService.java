package es.grupo18.jobmatcher.service;

import java.util.ArrayList;
import java.time.LocalDateTime;

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

                LocalDateTime now = LocalDateTime.now();


                User martina = new User("Martina", "martinini@gmail.com", "martinini123", "659801423", "Vicálvaro",
                                "Busco a alguien para que haga mi trabajo", 2,
                                null, null, null, null);
                userRepository.save(martina);

                Post post1 = new Post("Test 1", "This is a test post",
                                now.minusYears(0).minusMonths(2).minusDays(5).minusHours(2).minusMinutes(25),
                                "https://wilku.top/wp-content/uploads/2023/06/System-Testing-Explained-in-Five-Minutes-With-Examples.png",
                                testing);

                Post post2 = new Post(
                                "Test 2",
                                "This is a test post 2",
                                now.minusYears(0).minusMonths(1).minusDays(3).minusHours(1).minusMinutes(13),
                                "https://wilku.top/wp-content/uploads/2023/06/System-Testing-Explained-in-Five-Minutes-With-Examples.png",
                                testing);

                Post post3 = new Post(
                                "Test 3",
                                "This is a test post 3",
                                now.minusYears(1).minusMonths(7).minusDays(5).minusHours(5).minusMinutes(15),
                                "https://wilku.top/wp-content/uploads/2023/06/System-Testing-Explained-in-Five-Minutes-With-Examples.png",
                                testing);

                Post post4 = new Post(
                                "Test 4",
                                "This is a test post 4",
                                now.minusYears(1).minusMonths(3).minusDays(52).minusHours(0).minusMinutes(1),
                                "https://wilku.top/wp-content/uploads/2023/06/System-Testing-Explained-in-Five-Minutes-With-Examples.png",
                                testing);

                postRepository.save(post1);
                postRepository.save(post2);
                postRepository.save(post3);
                postRepository.save(post4);

                Company company = new Company("Test Company", "test@gmail.com", "password123", "Madrid",
                                "We are a testing company", "https://www.google.com", null);
                companyRepository.save(company);

                Company company2 = new Company("Test Company 2", "test2@gmail.com", "password123", "Barcelona",
                                "We are a testing company again", "https://www.google.com", null);
                companyRepository.save(company2);
        }

}
