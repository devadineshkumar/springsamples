package dto;

import lombok.Data;

@Data
public class SearchCriteria {
    private String name;
    private String address;
    private String section;
    private Integer totalMark;
    private Integer rank;
}

