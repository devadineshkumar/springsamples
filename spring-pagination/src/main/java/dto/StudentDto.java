package dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDto {
    private Long id;
    private String name;
    private String section;
    private String address;
    private int totalMark;
    private int rank;
}

