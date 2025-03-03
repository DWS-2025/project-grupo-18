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
            return; // Evita cargar empresas si ya están en memoria
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
                "California",
                "https://static.vecteezy.com/system/resources/previews/022/100/816/non_2x/microsoft-logo-transparent-free-png.png",
                favouriteUsersList_c1);

        c2 = new Company(
                12,
                "Google",
                "google@gmail.com",
                "asdfg12345",
                "California",
                "Innovamos en tecnología y buscamos talento apasionado por la inteligencia artificial y la computación en la nube.",
                "https://freelogopng.com/images/all_img/1657952440google-logo-png-transparent.png",
                favouriteUsersList_c2);

        c3 = new Company(
                22,
                "Apple",
                "apple@email.com",
                "qwerty12345",
                "Silicon Valley",
                "Creamos experiencias revolucionarias con nuestros productos, buscamos diseñadores y desarrolladores visionarios.",
                "https://wallpapers.com/images/hd/black-apple-logo-4k-yn7rtft0sl2x3dhe.jpg",
                favouriteUsersList_c3);

        c4 = new Company(
                32,
                "Amazon",
                "amazon@amazon.com",
                "amazon123",
                "Seattle",
                "Lideramos el comercio electrónico y la computación en la nube, buscamos ingenieros que impulsen la innovación.",
                "https://images.squarespace-cdn.com/content/v1/5d04183c2027f10001d3c405/1563888556224-GD8RXHAUD6NXG36IJYQY/nogotell-amazon.png",
                favouriteUsersList_c4);

        c5 = new Company(
                42,
                "Tesla",
                "tesla@gmail.com",
                "tesla123",
                "California",
                "Revolucionamos la industria automotriz con tecnología eléctrica y autónoma, buscamos talento disruptivo.",
                "https://www.pngmart.com/files/22/Tesla-Logo-PNG-Transparent.png",
                favouriteUsersList_c5);

        c6 = new Company(
                52,
                "Facebook",
                "facebook@outlook.com",
                "facebook123",
                "Construimos la red social más grande, buscamos ingenieros para conectar el mundo.",
                "Menlo Park",
                "https://upload.wikimedia.org/wikipedia/commons/5/51/Facebook_f_logo_%282019%29.svg",
                favouriteUsersList_c6);

        c7 = new Company(
                62,
                "Netflix",
                "netflix@gmail.com",
                "netflix123",
                "Revolucionamos el entretenimiento, buscamos creativos para contenido innovador.",
                "Los Gatos",
                "https://upload.wikimedia.org/wikipedia/commons/7/75/Netflix_icon.svg",
                favouriteUsersList_c7);

        c8 = new Company(
                72,
                "Uber",
                "uber@gmail.com",
                "uber123",
                "Transformamos la movilidad, buscamos talento en tecnología y logística.",
                "San Francisco",
                "https://th.bing.com/th/id/OIP.QMOhFSBODe1KQumgVL-LqwHaHa?rs=1&pid=ImgDetMain",
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
