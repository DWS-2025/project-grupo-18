package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.mapper.CompanyMapper;
import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.service.CompanyService;
import es.grupo18.jobmatcher.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/companies")
public class CompanyRestController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<CompanyDTO> getAll(Pageable pageable) {
        return companyMapper.toDTOs(companyService.findAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        Company company = companyService.findById(id);
        return (company == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(companyMapper.toDTO(company));
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> create(@RequestBody CompanyDTO dto) {
        Company company = companyMapper.toEntity(dto);
        companyService.save(company);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(company.getId()).toUri();
        return ResponseEntity.created(location).body(companyMapper.toDTO(company));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> update(@PathVariable Long id, @RequestBody CompanyDTO dto) {
        Company existing = companyService.findById(id);
        if (existing == null)
            return ResponseEntity.notFound().build();
        Company updated = companyMapper.toEntity(dto);
        updated.setId(id);
        companyService.save(updated);
        return ResponseEntity.ok(companyMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CompanyDTO> delete(@PathVariable Long id) {
        Company company = companyService.findById(id);
        if (company == null)
            return ResponseEntity.notFound().build();
        companyService.delete(company);
        return ResponseEntity.ok(companyMapper.toDTO(company));
    }

    @PostMapping("/{id}/favourites")
    public ResponseEntity<Void> addToFavourites(@PathVariable Long id) {
        User user = userService.getLoggedUser();
        Company company = companyService.findById(id);
        if (!user.getFavouriteCompaniesList().contains(company)) {
            user.getFavouriteCompaniesList().add(company);
            userService.save(user);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/favourites")
    public ResponseEntity<Void> removeFromFavourites(@PathVariable Long id) {
        User user = userService.getLoggedUser();
        Company company = companyService.findById(id);
        user.getFavouriteCompaniesList().remove(company);
        userService.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/favourites")
    public ResponseEntity<Boolean> isFavourite(@PathVariable Long id) {
        User user = userService.getLoggedUser();
        Company company = companyService.findById(id);
        boolean isFav = user.getFavouriteCompaniesList().contains(company);
        return ResponseEntity.ok(isFav);
    }
    
} 
