package es.grupo18.jobmatcher.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.dto.UserDTO;
import es.grupo18.jobmatcher.mapper.CompanyMapper;
import es.grupo18.jobmatcher.mapper.UserMapper;
import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.repository.CompanyRepository;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private UserMapper userMapper;

    public Page<CompanyDTO> findAll(Pageable pageable) {
        return companyRepository.findAll(pageable).map(this::toDTO);
    }

    public Collection<CompanyDTO> findAll() {
        return toDTOs(companyRepository.findAll());
    }

    public CompanyDTO findById(long id) {
        return toDTO(companyRepository.findById(id).orElse(null));
    }

    public Page<CompanyDTO> findPaginated(Pageable pageable) {
        return companyRepository.findAll(pageable).map(this::toDTO);
    }

    public long count() {
        return companyRepository.count();
    }

    public CompanyDTO save(CompanyDTO company) { // Saves a company
        Company companyDomain = toDomain(company);
        companyRepository.save(companyDomain);
        return toDTO(companyDomain);
    }

    public CompanyDTO update(CompanyDTO oldCompanyDTO, CompanyDTO updatedCompanyDTO) {
        Company oldCompany = toDomain(oldCompanyDTO);
        Company updatedCompany = toDomain(updatedCompanyDTO);
        oldCompany.setName(updatedCompany.getName());
        oldCompany.setEmail(updatedCompany.getEmail());
        oldCompany.setLocation(updatedCompany.getLocation());
        oldCompany.setBio(updatedCompany.getBio());
        companyRepository.save(oldCompany);
        return toDTO(oldCompany);
    }

    public void deleteById(long id) { // Deletes a company by its id
        companyRepository.deleteById(id);
    }

    public CompanyDTO delete(CompanyDTO company) { // Deletes a company
        Company companyDomain = toDomain(company);
        companyRepository.deleteById(companyDomain.getId());
        return toDTO(companyDomain);
    }

    // Methods to manage favourite users

    public CompanyDTO addOrRemoveFavouriteUser(long id, UserDTO user) {
        Company company = toDomain(findById(id));
        if (company.getFavouriteUsersList().contains(userMapper.toDomain(user))) {
            company.getFavouriteUsersList().remove(userMapper.toDomain(user));
        } else {
            company.getFavouriteUsersList().add(userMapper.toDomain(user));
        }
        companyRepository.save(company);
        return toDTO(company);
    }

    public boolean isUserFavourite(long id, UserDTO user) {
        Company company = toDomain(findById(id));
        return company.getFavouriteUsersList().contains(userMapper.toDomain(user));
    }

    CompanyDTO toDTO(Company company) {
        return companyMapper.toDTO(company);
    }

    Company toDomain(CompanyDTO dto) {
        return companyMapper.toDomain(dto);
    }

    List<CompanyDTO> toDTOs(List<Company> companies) {
        return companyMapper.toDTOs(companies);
    }

    List<Company> toDomains(List<CompanyDTO> dtos) {
        return companyMapper.toDomains(dtos);
    }

}
