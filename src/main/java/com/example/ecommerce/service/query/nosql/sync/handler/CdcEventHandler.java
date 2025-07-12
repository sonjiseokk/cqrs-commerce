package com.example.ecommerce.service.query.nosql.sync.handler;

import com.example.ecommerce.service.query.nosql.sync.CdcEvent;

public interface CdcEventHandler {

    boolean canHandle(CdcEvent event);

    void handle(CdcEvent event);
}