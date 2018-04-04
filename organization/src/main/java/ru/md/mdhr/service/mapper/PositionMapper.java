package ru.md.mdhr.service.mapper;

import ru.md.mdhr.domain.*;
import ru.md.mdhr.service.dto.PositionDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Position and its DTO PositionDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PositionMapper extends EntityMapper<PositionDTO, Position> {

    default PositionDTO toDto(Position entity) {
        if ( entity == null ) {
            return null;
        }

        PositionDTO positionDTO = new PositionDTO();

        positionDTO.setId( entity.getId() );
        positionDTO.setName( entity.getName() );
        positionDTO.setGrade( entity.getGrade() );
        positionDTO.setDepartmentId(entity.getDepartment().getId());
        positionDTO.setDepartmentName(entity.getDepartment().getName());

        return positionDTO;
    }

    default Position fromId(Long id) {
        if (id == null) {
            return null;
        }
        Position position = new Position();
        position.setId(id);
        return position;
    }
}
