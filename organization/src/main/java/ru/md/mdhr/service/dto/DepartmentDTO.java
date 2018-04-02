package ru.md.mdhr.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the Department entity.
 */
public class DepartmentDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private Set<PositionDTO> positions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PositionDTO> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDTO> positions) {
        this.positions = positions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DepartmentDTO departmentDTO = (DepartmentDTO) o;
        if(departmentDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), departmentDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DepartmentDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
