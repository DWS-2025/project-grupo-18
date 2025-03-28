package es.grupo18.jobmatcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Page<Company> findAll(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Company findById(long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public Page<Company> findPaginated(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }    

    public long count() {
        return companyRepository.count();
    }

    public void save(Company company) { // Saves a company
        companyRepository.save(company);
    }
    
    public void update(Company oldCompany, Company updatedCompany) {
        oldCompany.setName(updatedCompany.getName());
        oldCompany.setEmail(updatedCompany.getEmail());
        oldCompany.setLocation(updatedCompany.getLocation());
        oldCompany.setBio(updatedCompany.getBio());
        oldCompany.setImagePath(updatedCompany.getImagePath());
        companyRepository.save(oldCompany);     
    }
    
    public void deleteById(long id) { // Deletes a company by its id
        companyRepository.deleteById(id);
    }

    public void delete(Company company) { // Deletes a company
        companyRepository.deleteById(company.getId());
    }

    // Methods to manage favourite users

    public void addOrRemoveFavouriteUser(long id, User user) {
        Company company = findById(id);
        if (company.getFavouriteUsersList().contains(user)) {
            company.getFavouriteUsersList().remove(user);
        } else {
            company.getFavouriteUsersList().add(user);
        }
        companyRepository.save(company);
    }

    public boolean isUserFavourite(long id, User user) {
        Company company = findById(id);
        return company.getFavouriteUsersList().contains(user);
    }

}
