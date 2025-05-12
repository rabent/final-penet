package com.example.demo.model.dto.TPsnippet;


import com.example.demo.model.dto.Attraction.AttractionSummaryDto;
import com.example.demo.model.entity.Attraction;
import com.example.demo.model.entity.TripSnippet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TPsnippetRequestDto {
   @NotBlank(message = "여행 경비를 입력해주세요")
   @Size(max = 100)
   private String price;
   @NotBlank(message = "여행 계획을 입력해주세요")
   @Size(max = 200)
   private String schedule;

   public TripSnippet toEntity() { // service 단에서 referencebyid로 attraction 추가 필요
       return TripSnippet.builder()
               .price(this.price)
               .schedule(this.schedule)
               .build();
   }
}
