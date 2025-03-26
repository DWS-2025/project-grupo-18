package es.grupo18.jobmatcher.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.service.CompanyService;

public class CompaniesController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/companies")
    public String getCompanies(Model model) {
        model.addAttribute("companies", companyService.findAll());
        return "companies";
    }

    @PostMapping("/companies/new")
    public String newCompany(Model model, Company company) {
        companyService.save(company);
        return "redirect:/companies";
    }

    @GetMapping("/companies/{companyId}")
    public String getCompany(Model model, @PathVariable long id) {
        Optional<Company> company = Optional.ofNullable(companyService.findById(id));
        if (company.isPresent()) {
            model.addAttribute("company", company.get());
            return "show_company";
        } else {
            return "company_not_found";
        }
    }

    @GetMapping("/companies/{companyId}/edit")
    public String editCompany(Model model, @PathVariable long id) {
        Optional<Company> company = Optional.ofNullable(companyService.findById(id));
        if (company.isPresent()) {
            model.addAttribute("company", company.get());
            return "edit_company";
        } else {
            return "company_not_found";
        }
    }

    @PostMapping("/companies/{companyId}/edit")
    public String updateCompany(Model model, @PathVariable long id, Company updatedCompany) {
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
    public String deleteCompany(@PathVariable long id) {
        Optional<Company> op = Optional.ofNullable(companyService.findById(id));
        if (op.isPresent()) {
            companyService.delete(op.get());
            return "redirect:/companies";
        } else {
            return "company_not_found";
        }
    }

}
