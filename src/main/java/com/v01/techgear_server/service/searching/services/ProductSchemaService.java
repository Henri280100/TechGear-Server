package com.v01.techgear_server.service.searching.services;

import org.typesense.api.Client;

public interface ProductSchemaService {
    void createProductSchema(Client typesenseClient);
    void deleteProductSchema(Client typesenseClient);
}