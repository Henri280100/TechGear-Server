package com.v01.techgear_server.config;



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

public class DateTimeScalar {
    public static final GraphQLScalarType DATE_TIME = GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("A DateTime scalar")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult) {
                    return ((LocalDateTime) dataFetcherResult).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }

                @Override
                public LocalDateTime parseValue(Object input) {
                    return LocalDateTime.parse((String) input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }

                @Override
                public LocalDateTime parseLiteral(Object input) {
                    // if (input instanceof StringValue) {
                    // return LocalDateTime.parse(((StringValue) input).getValue(),
                    // DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    // }
                    // throw new IllegalArgumentException("Invalid DateTime");
                    return input instanceof StringValue sv
                            ? LocalDateTime.parse(sv.getValue(),
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            : null;
                }
            }).build();
}
