package com.example.demo.model.dto.TPsnippet;

import com.example.demo.model.entity.TripSnippet;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TPsnippetUpdateDto {
    @Size(max = 100)
    private String price;

    @Size(max = 200)
    private String schedule;

    private Integer no;
}
