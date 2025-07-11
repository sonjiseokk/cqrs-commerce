package com.example.ecommerce.service.query.nosql.sync;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebeziumEnvelope {
    private Object schema; // 무시하거나 필요하면 정의
    private CdcEvent payload;
}

