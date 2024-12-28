package com.example.moneyminder.VMs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVM {
    private Long id;
    private String name;
    private String description;
}
