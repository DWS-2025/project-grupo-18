package es.grupo18.jobmatcher.controller.rest;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.service.CompanyService;
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

    @GetMapping
    public Page<CompanyDTO> getAll(Pageable pageable) {
        return companyService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        CompanyDTO company = companyService.findById(id);
        return (company == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(company);
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> create(@RequestBody CompanyDTO dto) {
        CompanyDTO created = companyService.save(dto);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> update(@PathVariable Long id, @RequestBody CompanyDTO dto) {
        CompanyDTO existing = companyService.findById(id);
        if (existing == null)
            return ResponseEntity.notFound().build();
        CompanyDTO updated = new CompanyDTO(id, dto.name(), dto.email(), dto.location(), dto.bio());
        updated = companyService.update(existing, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        CompanyDTO company = companyService.findById(id);
        if (company == null)
            return ResponseEntity.notFound().build();
        companyService.delete(company);
        return ResponseEntity.noContent().build(); // No se devuelve el objeto borrado
    }

    @PostMapping("/{id}/favourites")
    public ResponseEntity<Void> addToFavourites(@PathVariable Long id) {
        CompanyDTO company = companyService.findById(id);
        if (company == null)
            return ResponseEntity.notFound().build();
        companyService.toggleFavouriteCompanyForCurrentUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/favourites")
    public ResponseEntity<Void> removeFromFavourites(@PathVariable Long id) {
        CompanyDTO company = companyService.findById(id);
        if (company == null)
            return ResponseEntity.notFound().build();
        companyService.toggleFavouriteCompanyForCurrentUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/favourites")
    public ResponseEntity<Boolean> isFavourite(@PathVariable Long id) {
        CompanyDTO company = companyService.findById(id);
        if (company == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(companyService.isCompanyFavouriteForCurrentUser(id));
    }

    @GetMapping("/matches")
    public ResponseEntity<List<CompanyDTO>> getMutualMatches() {
        return ResponseEntity.ok(companyService.getMutualMatchesForCurrentUser());
    }
}
