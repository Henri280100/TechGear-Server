package com.v01.techgear_server.config;



import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

public class LocalDateScalar {
    public static final GraphQLScalarType LOCAL_DATE = GraphQLScalarType.newScalar()
            .name("LocalDate")
            .description("A LocalDate scalar")
            .coercing(new Coercing<LocalDate, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    return ((LocalDate) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE);
                }

                @Override
                public LocalDate parseValue(Object input) {
                    return LocalDate.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE);
                }

                @Override
                public LocalDate parseLiteral(Object input) {
                    if (input instanceof StringValue value) {
                        return LocalDate.parse(value.getValue(), DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                    throw new IllegalArgumentException("Invalid LocalDate");
                }
            }).build();
}
