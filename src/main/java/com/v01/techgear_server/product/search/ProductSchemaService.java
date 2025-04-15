package com.v01.techgear_server.product.search;

import org.typesense.api.Client;

public interface ProductSchemaService {
    void createProductSchema(Client typesenseClient) throws Exception;
}