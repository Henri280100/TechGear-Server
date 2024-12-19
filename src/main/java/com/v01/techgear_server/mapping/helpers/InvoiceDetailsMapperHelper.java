package com.v01.techgear_server.mapping.helpers;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvoiceDetailsMapperHelper {
    private InvoiceDetailsMapperHelper() {}

    public static BigDecimal calculateLineTotal(BigDecimal lineTotal) {
        return lineTotal != null ? lineTotal : BigDecimal.ZERO;
    }
}
