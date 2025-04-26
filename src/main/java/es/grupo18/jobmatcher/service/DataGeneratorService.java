package es.grupo18.jobmatcher.service;

import java.util.ArrayList;
import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
@Profile("h2")
public class DataGeneratorService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CompanyRepository companyRepository;

        @Autowired
        private PostRepository postRepository;

        @PostConstruct
        public void init() throws IOException {
                LocalDateTime now = LocalDateTime.now();

                // Users
                User ana = new User("Ana García", "ana.garcia@gmail.com", "password123", "666555444", "Madrid",
                                "Soy una profesional en busca de oportunidades en marketing digital.", 2, null, null,
                                null, null);
                userRepository.save(ana);

                User carlos = new User("Carlos López", "carlos.lopez@gmail.com", "password123", "999888777",
                                "Barcelona",
                                "Ingeniero de software con experiencia en Java y Spring Boot.", 2, null, null, null,
                                null);
                userRepository.save(carlos);

                // Post 1 with interview image
                Post post1 = new Post(
                                "5 Consejos Clave para Triunfar en tu Entrevista de Trabajo",
                                "Prepararte para una entrevista puede ser intimidante, pero con estos consejos podrás destacar: investiga la empresa, practica tus respuestas, muestra confianza, haz preguntas inteligentes y sigue el contacto tras la entrevista. ¡Tu próximo empleo está más cerca de lo que crees!",
                                now.minusMonths(2).minusDays(5),
                                "",
                                ana);
                post1.setImageContentType("image/jpeg");
                post1.getReviews().add(new Review(
                                "¡Gran artículo! Me ayudó a prepararme para mi entrevista en una startup.", 5, ana));
                post1.getReviews().add(new Review("Muy útil, lo apliqué y conseguí el puesto. ¡Gracias!", 4, carlos));
                postRepository.save(post1);

                // Post 2 with resume image
                Post post2 = new Post(
                                "Cómo Diseñar un Currículum que Destaque ante los Reclutadores",
                                "Un CV bien estructurado es tu carta de presentación. Usa un diseño limpio, destaca tus logros con números, adapta tu experiencia al puesto y no olvides incluir palabras clave del sector.",
                                now.minusMonths(1).minusDays(3),
                                "",
                                carlos);
                post2.setImageContentType("image/jpeg");
                post2.getReviews().add(new Review("Rediseñé mi CV y recibí más respuestas. ¡Recomendado!", 5, carlos));
                postRepository.save(post2);

                // Post 3 with networking image
                Post post3 = new Post(
                                "Errores que Debes Evitar al Buscar Trabajo en 2025",
                                "Desde enviar el mismo CV genérico a todas las ofertas hasta no seguir las instrucciones, estos errores pueden costarte oportunidades.",
                                now.minusYears(1).minusMonths(7),
                                "",
                                ana);
                post3.setImageContentType("image/jpeg");
                postRepository.save(post3);

                // Post 4
                Post post4 = new Post(
                                "El Poder del Networking: Construye tu Red para Encontrar Empleo",
                                "Conectar con profesionales de tu sector puede abrirte puertas inesperadas. Participa en eventos y usa LinkedIn estratégicamente.",
                                now.minusYears(1).minusMonths(3),
                                "",
                                carlos);
                postRepository.save(post4);

                // Enterprise generation
                for (int i = 1; i <= 30; i++) {
                        Company company = new Company();
                        company.setName("Tech Solutions " + i);
                        company.setEmail("contacto" + i + "@techsolutions.com");
                        company.setLocation(i % 2 == 0 ? "Madrid" : "Barcelona");
                        company.setBio("Somos una empresa innovadora dedicada a "
                                        + (i % 2 == 0 ? "desarrollo de software" : "consultoría tecnológica")
                                        + ". ¡Buscamos talento como tú!");
                        company.setFavouriteUsersList(new ArrayList<>());
                        companyRepository.save(company);
                }
        }

}
