package com.example.demo.model.dto.TPsnippet;

import com.example.demo.model.entity.TripSnippet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TPsnippetUpdateDto {
    @Range(min = 0)
    private Integer price;

    @Size(max = 200)
    private String schedule;

    private String category;

    @Size(max = 50)
    private String date;

    private Integer no;
}
