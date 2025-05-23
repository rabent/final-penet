package com.example.demo.service;

import com.example.demo.model.dto.AI.AttractionTipResponseDto;
import com.example.demo.model.dto.Attraction.AttractionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AICommentService {

    private final ChatClient chatClient;
    private final AttractionService attractionService;

    public AttractionTipResponseDto generateAttractionTip(Integer no) {
        AttractionResponseDto attractionDetail = attractionService.getAttractionDetail(no);

        String prompt = String.format("""
                    [여행지 정보]
                    이름: %s
                    지역: %s %s
                    주소: %s
                    유형: %s
                    설명: %s
                    
                    이 여행지에 처음 방문하는 여행자에게 도움이 될 만한 한 줄 팁을 한국어로 만들어주세요.
                    - 역사적 사실, 숨은 포인트, 사진 찍기 좋은 시간대, 추천 활동 등 실질적으로 유용하거나 특별한 정보를 포함해주세요.
                    - 너무 일반적인 문장(예: "즐거운 여행 되세요")은 피하고, 이 장소만의 매력을 살려주세요.
                    """,
                    attractionDetail.getTitle(),
                    attractionDetail.getAreaName() != null ? attractionDetail.getAreaName() : "",
                    attractionDetail.getSiGunguName() != null ? attractionDetail.getSiGunguName() : "",
                    attractionDetail.getFullAddress() != null ? attractionDetail.getFullAddress() : "",
                    attractionDetail.getContentTypeName() != null ? attractionDetail.getContentTypeName() : "",
                    attractionDetail.getOverview() != null ? attractionDetail.getOverview() : ""
                );

        String content = chatClient.prompt(prompt)
                .system(t -> t.param("language", "korean"))
                .call()
                .content();
        AttractionTipResponseDto dto = new AttractionTipResponseDto(content);
        return dto;
    }
}
