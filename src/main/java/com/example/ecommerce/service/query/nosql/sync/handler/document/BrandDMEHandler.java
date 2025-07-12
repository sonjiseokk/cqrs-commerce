package com.example.ecommerce.service.query.nosql.sync.handler.document;

import com.example.ecommerce.service.query.nosql.entity.BrandDocument;
import com.example.ecommerce.service.query.nosql.repository.BrandDocumentRepository;
import com.example.ecommerce.service.query.nosql.sync.CdcEvent;
import com.example.ecommerce.service.query.nosql.sync.handler.ProductDocumentModelEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class BrandDMEHandler extends ProductDocumentModelEventHandler {

    private final BrandDocumentRepository brandDocumentRepository;

    public BrandDMEHandler(ObjectMapper objectMapper, BrandDocumentRepository brandDocumentRepository) {
        super(objectMapper);
        this.brandDocumentRepository = brandDocumentRepository;
    }

    @Override
    protected String getSupportedTable() {
        return "brands";
    }

    @Override
    public void handle(CdcEvent event) {
        Map<String, Object> data;
        Long brandId;

        if (event.isDelete()) {
            // 삭제 이벤트 처리
            data = event.getBeforeData();
            if (data == null || !data.containsKey("id")) {
                return;
            }

            brandId = getLongValue(data, "id");
            brandDocumentRepository.deleteById(brandId);
            log.info("Deleted brand document with ID: {}", brandId);
            return;
        }

        // 생성 또는 업데이트 이벤트 처리
        data = event.getAfterData();
        if (data == null || !data.containsKey("id")) {
            return;
        }

        brandId = getLongValue(data, "id");

        // 브랜드 문서 생성 또는 업데이트
        BrandDocument document = BrandDocument.builder()
                .id(brandId)
                .name(getStringValue(data, "name"))
                .slug(getStringValue(data, "slug"))
                .description(getStringValue(data, "description"))
                .logoUrl(getStringValue(data, "logo_url"))
                .website(getStringValue(data, "website"))
                .build();

        brandDocumentRepository.save(document);
        log.info("Saved brand document with ID: {}", brandId);
    }
}
