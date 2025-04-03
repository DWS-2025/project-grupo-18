package es.grupo18.jobmatcher.controller.web;

import es.grupo18.jobmatcher.dto.CompanyDTO;
import es.grupo18.jobmatcher.service.CompanyService;
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
        model.addAttribute("company", new CompanyDTO(null, "", "", "", ""));
        return "company/new_company_form";
    }

    @PostMapping("/companies/new")
    public String newCompany(@ModelAttribute CompanyDTO companyDTO) {
        companyService.save(companyDTO);
        return "redirect:/companies";
    }

    @GetMapping("/companies/{companyId}")
    public String getCompany(Model model, @PathVariable("companyId") long id) {
        CompanyDTO company = companyService.findById(id);
        if (company != null) {
            model.addAttribute("company", company);
            return "company/show_company";
        } else {
            return "company/company_not_found";
        }
    }

    @GetMapping("/companies/{companyId}/edit")
    public String editCompany(Model model, @PathVariable("companyId") long id) {
        CompanyDTO company = companyService.findById(id);
        if (company != null) {
            model.addAttribute("company", company);
            return "company/company_form";
        } else {
            return "company/company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/edit")
    public String updateCompany(@PathVariable("companyId") long id, @ModelAttribute CompanyDTO updatedCompany) {
        CompanyDTO oldCompany = companyService.findById(id);
        if (oldCompany != null) {
            companyService.update(oldCompany, updatedCompany);
            return "redirect:/companies/" + id;
        } else {
            return "company/company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/delete")
    public String deleteCompany(@PathVariable("companyId") long id) {
        CompanyDTO company = companyService.findById(id);
        if (company != null) {
            companyService.delete(company);
        }
        return "redirect:/companies";
    }

}
