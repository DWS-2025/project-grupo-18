package es.grupo18.jobmatcher.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.grupo18.jobmatcher.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> findByLocation(String location, Pageable page);
}
