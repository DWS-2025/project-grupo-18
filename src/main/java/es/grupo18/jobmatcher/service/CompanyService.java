package es.grupo18.jobmatcher.service;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CompanyService {

    private List<Company> companies;
    private final UserService userService;

    public CompanyService(UserService userService) {
        this.userService = userService;
        this.companies = new ArrayList<>();
        loadInitialCompanies();
    }

    @PostConstruct
    private void loadInitialCompanies() {
        if (!companies.isEmpty()){
            return; // Avoids loading the companies more than once
        }

        final Company c1;
        final Company c2;
        final Company c3;
        final Company c4;
        final Company c5;
        final Company c6;
        final Company c7;
        final Company c8;

        List<User> favouriteUsersList_c1 = new ArrayList<>();
        List<User> favouriteUsersList_c2 = new ArrayList<>();
        List<User> favouriteUsersList_c3 = new ArrayList<>();
        List<User> favouriteUsersList_c4 = new ArrayList<>();
        List<User> favouriteUsersList_c5 = new ArrayList<>();
        List<User> favouriteUsersList_c6 = new ArrayList<>();
        List<User> favouriteUsersList_c7 = new ArrayList<>();
        List<User> favouriteUsersList_c8 = new ArrayList<>();

        // Not all comapnies have the user added to their favourite list

        favouriteUsersList_c1.add(userService.getUser());
        favouriteUsersList_c2.add(userService.getUser());
        favouriteUsersList_c5.add(userService.getUser());
        favouriteUsersList_c6.add(userService.getUser());
        favouriteUsersList_c8.add(userService.getUser());

        c1 = new Company(
                02,
                "Microsoft",
                "microsoft@outlook.com",
                "password123",
                "Buscamos programadores experimentados, ofrecemos un ambiente de trabajo flexible y beneficios de bienestar.",
                "📍California",
                "/img/microsoft.jpg",
                favouriteUsersList_c1);

        c2 = new Company(
                12,
                "Google",
                "google@gmail.com",
                "asdfg12345",
                "Innovamos en tecnología y buscamos talento apasionado por la inteligencia artificial y la computación en la nube.",
                "📍California",
                "/img/google.jpg",
                favouriteUsersList_c2);

        c3 = new Company(
                22,
                "Apple",
                "apple@email.com",
                "qwerty12345",
                "Creamos experiencias revolucionarias con nuestros productos, buscamos diseñadores y desarrolladores visionarios.",
                "📍Silicon Valley",
                "/img/apple.jpg",
                favouriteUsersList_c3);

        c4 = new Company(
                32,
                "Amazon",
                "amazon@amazon.com",
                "amazon123",
                "Lideramos el comercio electrónico y la computación en la nube, buscamos ingenieros que impulsen la innovación.",
                "📍Seattle",
                "/img/amazon.jpg",
                favouriteUsersList_c4);

        c5 = new Company(
                42,
                "Tesla",
                "tesla@gmail.com",
                "tesla123",
                "Revolucionamos la industria automotriz con tecnología eléctrica y autónoma, buscamos talento disruptivo.",
                "📍California",
                "/img/tesla_company.jpg",
                favouriteUsersList_c5);

        c6 = new Company(
                52,
                "Facebook",
                "facebook@outlook.com",
                "facebook123",
                "Construimos la red social más grande, buscamos ingenieros para conectar el mundo.",
                "📍Menlo Park",
                "/img/facebook_company.jpg",
                favouriteUsersList_c6);

        c7 = new Company(
                62,
                "Netflix",
                "netflix@gmail.com",
                "netflix123",
                "Revolucionamos el entretenimiento, buscamos creativos para contenido innovador.",
                "📍Los Gatos",
                "/img/netflix_company.jpg",
                favouriteUsersList_c7);

        c8 = new Company(
                72,
                "Uber",
                "uber@gmail.com",
                "uber123",
                "Transformamos la movilidad, buscamos talento en tecnología y logística.",
                "📍San Francisco",
                "/img/uber.jpg",
                favouriteUsersList_c8);

        companies.add(c1);
        companies.add(c2);
        companies.add(c3);
        companies.add(c4);
        companies.add(c5);
        companies.add(c6);
        companies.add(c7);
        companies.add(c8);

        System.out.println("Companies uploaded to memory");
    }

    public List<Company> getCompaniesList() { // Returns the list of companies
        return new ArrayList<>(new HashSet<>(companies));
    }

    public Company getCompanyByName(String name) { // Returns the company with the given name
        return companies.stream()
            .filter(company -> company.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

}
