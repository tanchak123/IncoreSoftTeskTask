package dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.Singular;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class DepartmentDto {

    private Long id;

    @JSONField(name = "createDate", format = "dd/MM/yyyy", ordinal = 2)
    private LocalDate createDate;

    @JSONField(ordinal = 1)
    private String name;

    @JSONField(ordinal = 3)
    @Singular
    private List<EmployeeDto> employeeList = new ArrayList<>();

}
