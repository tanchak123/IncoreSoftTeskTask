package dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Singular;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeDto {

    private Long id;

    @JSONField(ordinal = 1)
    private String name;

    @JSONField(ordinal = 2)
    private String email;

    @Singular
    @JSONField(ordinal = 5)
    private List<DepartmentDto> departmentList = new ArrayList<>();

    @JSONField(format = "dd/MM/yyyy", ordinal = 4)
    private LocalDate createDate;

    @JSONField(ordinal = 3)
    private Integer experienceYears;
}
