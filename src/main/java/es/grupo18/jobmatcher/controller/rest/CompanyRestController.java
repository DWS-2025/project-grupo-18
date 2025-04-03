package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.service.CompanyService;
import es.grupo18.jobmatcher.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private UserService userService;

    @GetMapping
    public Page<CompanyDTO> getAll(Pageable pageable) {
        return companyService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(companyService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> create(@RequestBody CompanyDTO dto) {
        dto = companyService.save(dto);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> update(@PathVariable Long id, @RequestBody CompanyDTO dto) {
        try {
            dto = companyService.update(companyService.findById(id), dto);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CompanyDTO> delete(@PathVariable Long id) {
        try {
            companyService.deleteById(id);
            return ResponseEntity.ok(companyService.findById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/favourites")
    public ResponseEntity<Void> addToFavourites(@PathVariable Long id) {
        try {
            userService.addOrRemoveFavouriteCompany(userService.getLoggedUser().id(), companyService.findById(id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/favourites")
    public ResponseEntity<Void> removeFromFavourites(@PathVariable Long id) {
        try {
            userService.addOrRemoveFavouriteCompany(userService.getLoggedUser().id(), companyService.findById(id));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/favourites")
    public ResponseEntity<Boolean> isFavourite(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.isCompanyFavourite(companyService.findById(id)));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/matches")
    public ResponseEntity<List<CompanyDTO>> getMutualMatches() {
        return ResponseEntity.ok(companyService.getMutualMatchesForCurrentUser());
    }
    
}
