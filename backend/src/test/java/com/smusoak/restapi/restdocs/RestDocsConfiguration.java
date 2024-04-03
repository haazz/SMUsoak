package com.smusoak.restapi.restdocs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.Preprocessors;

@TestConfiguration
public class RestDocsConfiguration {

    @Bean
    public RestDocumentationResultHandler write() {
        return MockMvcRestDocumentation.document(
                "{class-name}/{method-name}", // 저장 디렉토리 명 정리
                // 문서 가독성 높이기
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
        );
    }
}
