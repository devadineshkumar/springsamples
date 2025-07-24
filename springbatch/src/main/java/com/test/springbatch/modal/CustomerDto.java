package com.test.springbatch.modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomerDto {

    private Long id;

    private String name;

    private String address;

    private String contactNo;

}
