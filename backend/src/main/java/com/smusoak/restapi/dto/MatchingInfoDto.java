package com.smusoak.restapi.dto;
import lombok.*;

@NoArgsConstructor
@Setter
public class MatchingInfoDto {
    @Getter
    @Data
    public static class MatchingRequest {
        private int minPartnerAge;
        private int maxPartnerAge;
        private String partnerGender;
        private String userMail;
    }
}

