package es.grupo18.jobmatcher.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.service.CompanyService;

@Controller
public class CompaniesController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/companies")
    public String listCompanies(Model model, Pageable page) {
        Pageable pageable = PageRequest.of(page.getPageNumber(), 10);

        model.addAttribute("companies", companyService.findAll(pageable));

        boolean hasPrev = pageable.getPageNumber() >= 1;
        boolean hasNext = (pageable.getPageNumber() + 1) * pageable.getPageSize() < companyService.count();

        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("prev", pageable.getPageNumber() - 1);
        model.addAttribute("hasNext", hasNext);
        model.addAttribute("next", pageable.getPageNumber() + 1);

        return "companies";
    }

    @GetMapping("/companies/new")
    public String showCompanyForm(Model model) {
        model.addAttribute("company", new Company());
        return "new_company_form";
    }

    @PostMapping("/companies/new")
    public String newCompany(Model model, Company company) {
        companyService.save(company);
        return "redirect:/companies";
    }

    @GetMapping("/companies/{companyId}")
    public String getCompany(Model model, @PathVariable("companyId") long id) {
        Optional<Company> company = Optional.ofNullable(companyService.findById(id));
        if (company.isPresent()) {
            model.addAttribute("company", company.get());
            return "show_company";
        } else {
            return "company_not_found";
        }
    }

    @GetMapping("/companies/{companyId}/edit")
    public String editCompany(Model model, @PathVariable("companyId") long id) {
        Optional<Company> company = Optional.ofNullable(companyService.findById(id));
        if (company.isPresent()) {
            model.addAttribute("company", company.get());
            return "company_form";
        } else {
            return "company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/edit")
    public String updateCompany(Model model, @PathVariable("companyId") long id, Company updatedCompany) {
        Optional<Company> op = Optional.ofNullable(companyService.findById(id));
        if (op.isPresent()) {
            Company oldCompany = op.get();
            companyService.update(oldCompany, updatedCompany);
            return "redirect:/companies/" + id;
        } else {
            return "company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/delete")
    public String deleteCompany(@PathVariable("companyId") long id) {
        companyService.delete(companyService.findById(id));
        return "redirect:/companies";
    }

}
