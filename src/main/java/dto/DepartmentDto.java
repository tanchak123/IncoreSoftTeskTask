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
public class DepartmentDto {

    private Long id;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createDate;

    private String name;

    @Singular
    private List<EmployeeDto> employeeList = new ArrayList<>();

}
