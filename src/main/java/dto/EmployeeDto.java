package dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Singular;
import utils.LocalDateDeserializer;
import utils.LocalDateSerializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeDto {

    private Long id;

    private String name;

    private String email;

    @Singular
    private List<DepartmentDto> departmentList = new ArrayList<>();

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createDate;

    private Integer experienceYears;
}
