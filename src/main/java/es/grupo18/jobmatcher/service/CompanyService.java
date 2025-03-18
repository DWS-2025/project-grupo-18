package es.grupo18.jobmatcher.service;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.model.User;
import es.grupo18.jobmatcher.repository.CompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Company findById(long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public void save(Company company) { // Saves a company
        companyRepository.save(company);
    }
    
    public void update(Company oldCompany, Company updatedCompany) {
        oldCompany.setName(updatedCompany.getName());
        oldCompany.setEmail(updatedCompany.getEmail());
        oldCompany.setPassword(updatedCompany.getPassword());
        oldCompany.setLocation(updatedCompany.getLocation());
        oldCompany.setBio(updatedCompany.getBio());
        oldCompany.setImagePath(updatedCompany.getImagePath());
        companyRepository.save(oldCompany);     
    }
    
    public void deleteById(long id) { // Deletes a company by its id

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
