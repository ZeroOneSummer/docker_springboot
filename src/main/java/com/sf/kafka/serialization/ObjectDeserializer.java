package com.sf.kafka.serialization;

import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class ObjectDeserializer implements Deserializer<Object> {

    @Override
    public Object deserialize(String s, byte[] bytes) {
        return BeanUtils.byteToObj(bytes);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
