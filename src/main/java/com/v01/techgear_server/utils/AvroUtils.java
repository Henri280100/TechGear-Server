package com.v01.techgear_server.utils;

import java.io.ByteArrayOutputStream;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import io.jsonwebtoken.io.IOException;

public class AvroUtils {
    public class AvroSerializer<T> {
    public byte[] serialize(T data, Schema schema) throws IOException, java.io.IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DatumWriter<T> datumWriter = new SpecificDatumWriter<>(schema);
        DataFileWriter<T> dataFileWriter = new DataFileWriter<>(datumWriter);
        dataFileWriter.create(schema, outputStream);
        dataFileWriter.append(data);
        dataFileWriter.close();
        return outputStream.toByteArray();
    }
}

public class AvroDeserializer<T> {
    public T deserialize(byte[] data, Schema schema, Class<T> clazz) throws IOException, java.io.IOException {
        DatumReader<T> datumReader = new SpecificDatumReader<>(clazz);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        return datumReader.read(null, decoder);
    }
}
}
