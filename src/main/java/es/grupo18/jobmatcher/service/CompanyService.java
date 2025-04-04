package es.grupo18.jobmatcher.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private UserService userService;

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

    public CompanyDTO save(CompanyDTO company) {
        Company companyDomain = toDomain(company);
        companyRepository.save(companyDomain);
        return toDTO(companyDomain);
    }

    public CompanyDTO createEmpty() {
        return new CompanyDTO(null, "", "", "", "");
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

    public void deleteById(long id) {
        userService.removeCompanyFromAllUsers(id);
        companyRepository.deleteById(id);
    }

    public CompanyDTO delete(CompanyDTO company) {
        Company companyDomain = toDomain(company);
        companyRepository.deleteById(companyDomain.getId());
        return toDTO(companyDomain);
    }

    public CompanyDTO addOrRemoveFavouriteUser(long companyId, UserDTO userDTO) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Long userId = userDTO.id();

        boolean alreadyFavourite = company.getFavouriteUsersList().stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (alreadyFavourite) {
            company.getFavouriteUsersList().removeIf(u -> u.getId().equals(userId));
        } else {
            company.getFavouriteUsersList().add(userMapper.toDomain(userDTO));
        }

        companyRepository.save(company);
        return toDTO(company);
    }

    public boolean isUserFavourite(long companyId, UserDTO userDTO) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Long userId = userDTO.id();
        return company.getFavouriteUsersList().stream()
                .anyMatch(u -> u.getId().equals(userId));
    }

    public List<CompanyDTO> getFavouriteCompaniesForCurrentUser() {
        return new ArrayList<>(userService.getFavouriteCompanies());
    }

    public List<CompanyDTO> getNonFavouriteCompaniesForCurrentUser() {
        Collection<CompanyDTO> all = findAll();
        Collection<CompanyDTO> favourites = userService.getFavouriteCompanies();

        return all.stream()
                .filter(company -> !favourites.contains(company))
                .collect(Collectors.toList());
    }

    public List<CompanyDTO> getMutualMatchesForCurrentUser() {
        UserDTO user = userService.getLoggedUser();
        Collection<CompanyDTO> favourites = userService.getFavouriteCompanies();

        return favourites.stream()
                .filter(company -> isUserFavourite(company.id(), user))
                .collect(Collectors.toList());
    }

    public void toggleFavouriteCompanyForCurrentUser(long companyId) {
        CompanyDTO company = findById(companyId);
        UserDTO user = userService.getLoggedUser();
        userService.addOrRemoveFavouriteCompany(user.id(), company);
        addOrRemoveFavouriteUser(companyId, user);
    }

    
    public boolean isCompanyFavouriteForCurrentUser(long companyId) {
        UserDTO user = userService.getLoggedUser();
        return isUserFavourite(companyId, user);
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
