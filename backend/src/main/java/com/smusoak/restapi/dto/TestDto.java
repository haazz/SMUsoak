package com.smusoak.restapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestDto {
    private String key;
    private String value;
}
