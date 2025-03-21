package es.grupo18.jobmatcher.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.grupo18.jobmatcher.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    
}
