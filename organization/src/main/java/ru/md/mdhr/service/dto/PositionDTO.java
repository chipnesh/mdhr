package ru.md.mdhr.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Position entity.
 */
public class PositionDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    @Min(value = 1)
    @Max(value = 10)
    private Integer grade;

    @NotNull
    private String departmentName;

    private Long departmentId;

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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PositionDTO positionDTO = (PositionDTO) o;
        if(positionDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), positionDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PositionDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", grade=" + getGrade() +
            "}";
    }
}
