package com.v01.techgear_server.config;

import graphql.language.IntValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

public class LongScalar {
    public static final GraphQLScalarType LONG = GraphQLScalarType.newScalar()
            .name("Long")
            .description("A custom Long")
            .coercing(new Coercing<Long, Long>() {
                @Override
                public Long serialize(Object dataFetcherResult) {
                    return ((Number) dataFetcherResult).longValue();
                }

                @Override
                public Long parseValue(Object input) {
                    return Long.parseLong((String) input);
                }

                @Override
                public Long parseLiteral(Object input) {
                    if (input instanceof IntValue) {
                        return ((IntValue) input).getValue().longValue();
                    }
                    throw new IllegalArgumentException("Invalid long value");
                }
            })
            .build();
}
