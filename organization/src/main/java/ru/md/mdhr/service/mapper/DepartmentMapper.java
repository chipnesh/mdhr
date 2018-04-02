package ru.md.mdhr.service.mapper;

import ru.md.mdhr.domain.*;
import ru.md.mdhr.service.dto.DepartmentDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Department and its DTO DepartmentDTO.
 */
@Mapper(componentModel = "spring", uses = {PositionMapper.class})
public interface DepartmentMapper extends EntityMapper<DepartmentDTO, Department> {


    @Mapping(target = "positions", ignore = true)
    Department toEntity(DepartmentDTO departmentDTO);

    default Department fromId(Long id) {
        if (id == null) {
            return null;
        }
        Department department = new Department();
        department.setId(id);
        return department;
    }
}
