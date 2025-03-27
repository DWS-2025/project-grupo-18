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
        LocalDateTime now = LocalDateTime.now();

        // Usuarios de prueba con perfiles más realistas
        User ana = new User("Ana García", "ana.garcia@gmail.com", "password123", "666555444", "Madrid",
                "Soy una profesional en busca de oportunidades en marketing digital.", 2, null, null, null, null);
        userRepository.save(ana);

        User carlos = new User("Carlos López", "carlos.lopez@gmail.com", "password123", "999888777", "Barcelona",
                "Ingeniero de software con experiencia en Java y Spring Boot.", 2, null, null, null, null);
        userRepository.save(carlos);

        // Post 1: Consejos para entrevistas
        Post post1 = new Post(
                "5 Consejos Clave para Triunfar en tu Entrevista de Trabajo",
                "Prepararte para una entrevista puede ser intimidante, pero con estos consejos podrás destacar: investiga la empresa, practica tus respuestas, muestra confianza, haz preguntas inteligentes y sigue el contacto tras la entrevista. ¡Tu próximo empleo está más cerca de lo que crees!",
                now.minusMonths(2).minusDays(5),
                "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?ixlib=rb-4.0.3&auto=format&fit=crop&w=1350&q=80",
                ana
        );
        post1.getReviews().add(new Review(
                "¡Gran artículo! Me ayudó a prepararme para mi entrevista en una startup. La parte sobre hacer preguntas al entrevistador fue clave.", 5, ana));
        post1.getReviews().add(new Review(
                "Muy útil, especialmente el consejo de investigar la empresa. Lo apliqué y conseguí el puesto. ¡Gracias!", 4, carlos));
        post1.getReviews().add(new Review(
                "Buen contenido, pero añadiría ejemplos de preguntas para practicar.", 3, ana));
        postRepository.save(post1);

        // Post 2: Curriculum atractivo
        Post post2 = new Post(
                "Cómo Diseñar un Currículum que Destaque ante los Reclutadores",
                "Un CV bien estructurado es tu carta de presentación. Usa un diseño limpio, destaca tus logros con números, adapta tu experiencia al puesto y no olvides incluir palabras clave del sector. Aquí te contamos cómo hacerlo paso a paso.",
                now.minusMonths(1).minusDays(3),
                "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?ixlib=rb-4.0.3&auto=format&fit=crop&w=1350&q=80",
                carlos
        );
        post2.getReviews().add(new Review(
                "Rediseñé mi CV siguiendo estos consejos y recibí más respuestas de empresas. ¡Recomendado!", 5, carlos));
        post2.getReviews().add(new Review(
                "El enfoque en logros cuantificables me abrió puertas. Gran aporte.", 4, ana));
        postRepository.save(post2);

        // Post 3: Errores comunes
        Post post3 = new Post(
                "Errores que Debes Evitar al Buscar Trabajo en 2025",
                "Desde enviar el mismo CV genérico a todas las ofertas hasta no seguir las instrucciones de la solicitud, estos errores pueden costarte oportunidades. Aprende a evitarlos y mejora tus posibilidades de éxito en el mercado laboral actual.",
                now.minusYears(1).minusMonths(7),
                "https://images.unsplash.com/photo-1453873531674-2151bcd01707?ixlib=rb-4.0.3&auto=format&fit=crop&w=1350&q=80",
                ana
        );
        post3.getReviews().add(new Review(
                "Me identifiqué con lo del CV genérico. Ahora personalizo cada solicitud y noto la diferencia.", 4, carlos));
        postRepository.save(post3);

        // Post 4: Networking
        Post post4 = new Post(
                "El Poder del Networking: Construye tu Red para Encontrar Empleo",
                "Conectar con profesionales de tu sector puede abrirte puertas inesperadas. Participa en eventos, usa LinkedIn de forma estratégica y no temas pedir recomendaciones. Descubre cómo el networking puede ser tu mejor aliado.",
                now.minusYears(1).minusMonths(3),
                "https://images.unsplash.com/photo-1521737604893-d14cc237f11d?ixlib=rb-4.0.3&auto=format&fit=crop&w=1350&q=80",
                carlos
        );
        // Sin reseñas para este post inicialmente
        postRepository.save(post4);

        // Generación de empresas
        for (int i = 1; i <= 30; i++) {
            Company company = new Company();
            company.setName("Tech Solutions " + i);
            company.setEmail("contacto" + i + "@techsolutions.com");
            company.setLocation(i % 2 == 0 ? "Madrid" : "Barcelona");
            company.setBio("Somos una empresa innovadora dedicada a " + (i % 2 == 0 ? "desarrollo de software" : "consultoría tecnológica") + ". ¡Buscamos talento como tú!");
            company.setImagePath("");
            company.setFavouriteUsersList(new ArrayList<>());
            companyRepository.save(company);
        }

        // Usuario adicional
        User martina = new User("Martina Pérez", "martina.perez@gmail.com", "martinini123", "659801423", "Vicálvaro",
                "Busco oportunidades en recursos humanos o gestión de proyectos.", 2, null, null, null, null);
        userRepository.save(martina);

        // Empresas adicionales
        Company company1 = new Company("Innovatech", "info@innovatech.com", "password123", "Madrid",
                "Líderes en soluciones tecnológicas para el futuro. Únete a nuestro equipo.", null);
        companyRepository.save(company1);

        Company company2 = new Company("JobConnect", "hr@jobconnect.es", "password123", "Barcelona",
                "Conectamos talento con las mejores oportunidades del mercado.", null);
        companyRepository.save(company2);
    }
}