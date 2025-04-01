package es.grupo18.jobmatcher.mapper;

import es.grupo18.jobmatcher.model.Company;
import es.grupo18.jobmatcher.dto.CompanyDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    CompanyDTO toDTO(Company company);
    Company toEntity(CompanyDTO dto);

    List<CompanyDTO> toDTOs(List<Company> companies);
    List<Company> toEntities(List<CompanyDTO> dtos);
}
