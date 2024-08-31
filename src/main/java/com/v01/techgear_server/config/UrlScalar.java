package com.v01.techgear_server.config;

import java.net.URI;
import java.net.URISyntaxException;

import graphql.language.StringValue;
import graphql.schema.*;


public class UrlScalar {
    public static final GraphQLScalarType URL = GraphQLScalarType.newScalar()
        .name("URL")
        .description("A valid URL")
        .coercing(new Coercing<URI, String>() {
            @Override
            public String serialize(Object dataFetcherResult) {
                return dataFetcherResult.toString();
            }

            @Override
            public URI parseValue(Object input) {
                try {
                    return new URI(input.toString());
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Invalid URL");
                }
            }

            @Override
            public URI parseLiteral(Object input) {
                if (input instanceof StringValue) {
                    try {
                        return new URI(((StringValue) input).getValue());
                    } catch (URISyntaxException e) {
                        throw new IllegalArgumentException("Invalid URL");
                    }
                }
                throw new IllegalArgumentException("Invalid URL");
            }
        }).build();
}
