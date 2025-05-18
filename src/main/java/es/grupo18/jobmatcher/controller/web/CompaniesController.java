package es.grupo18.jobmatcher.controller.web;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.service.CompanyService;
import es.grupo18.jobmatcher.service.UserService;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CompaniesController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @GetMapping("/companies")
    public String listCompanies(Model model, Pageable page) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), 10);
        Page<CompanyDTO> companiesPage = companyService.findAll(pageable);

        model.addAttribute("companies", companiesPage.getContent());

        boolean hasPrev = companiesPage.hasPrevious();
        boolean hasNext = companiesPage.hasNext();

        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("prev", pageable.getPageNumber() - 1);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("next", pageable.getPageNumber() + 1);

        return "company/companies";
    }

    @GetMapping("/companies/new")
    public String showCompanyForm(Model model) {
        model.addAttribute("company", companyService.createEmpty());
        return "company/company_form";
    }

    @PostMapping("/companies/new")
    public String newCompany(Model model, @ModelAttribute CompanyDTO companyDTO) {
        if (companyDTO.name().isBlank() || companyDTO.email().isBlank() || companyDTO.location().isBlank()) {
            model.addAttribute("company", companyDTO);
            model.addAttribute("error", "Name, email and location cannot be empty");
            return "company/company_form";
        }

        companyService.save(companyDTO);
        return "redirect:/companies";
    }

    @GetMapping("/companies/{companyId}")
    public String getCompany(Model model, @PathVariable("companyId") long id) {
        try {
            model.addAttribute("company", companyService.findById(id));
            model.addAttribute("isAdmin",
                    userService.currentUserIsAdmin());
            return "company/show_company";
        } catch (NoSuchElementException e) {
            return "company/company_not_found";

        }
    }

    @GetMapping("/companies/{companyId}/edit")
    public String editCompany(Model model, @PathVariable("companyId") long id) {
        try {
            model.addAttribute("company", companyService.findById(id));
            return "company/company_form";
        } catch (NoSuchElementException e) {
            return "company/company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/edit")
    public String updateCompany(Model model,
            @PathVariable("companyId") long id,
            @ModelAttribute CompanyDTO updatedCompany) {
        try {
            if (updatedCompany.name().isBlank() || updatedCompany.email().isBlank()
                    || updatedCompany.location().isBlank()) {
                model.addAttribute("company", updatedCompany);
                model.addAttribute("error", "Name, email and location cannot be empty");
                return "company/company_form";
            }

            companyService.update(companyService.findById(id), updatedCompany);
            return "redirect:/companies/" + id;
        } catch (NoSuchElementException e) {
            return "company/company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/delete")
    public String deleteCompany(@PathVariable("companyId") long id) {
        try {
            companyService.deleteById(id);
            return "redirect:/companies";
        } catch (NoSuchElementException e) {
            return "company/company_not_found";
        }
    }

}
