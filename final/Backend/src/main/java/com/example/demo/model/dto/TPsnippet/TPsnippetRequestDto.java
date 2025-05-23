package com.example.demo.model.dto.TPsnippet;


import com.example.demo.model.entity.TripPlan;
import com.example.demo.model.entity.TripSnippet;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TPsnippetRequestDto {
   @NotNull(message = "여행 경비를 입력해주세요")
   @Range(min = 0)
   private Integer price;
   @NotBlank(message = "여행 계획을 입력해주세요")
   @Size(max = 200)
   private String schedule;
   
   @NotBlank(message = "카테고리를 선택해주세요")
   private String category;

   @NotBlank(message = "날짜를 입력해주세요")
   private String date;
   
   private Integer no;

   public TripSnippet toEntity() { // service 단에서 referencebyid로 attraction 추가 필요
       return TripSnippet.builder()
               .price(this.price)
               .schedule(this.schedule)
               .category(this.category)
               .date(this.date)
               .build();
   }
}
