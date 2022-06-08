package com.sf.kafka.serialization;

import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class ObjectSerializer implements Serializer<Object> {

    @Override
    public byte[] serialize(String s, Object o) {
        return BeanUtils.objToByte(o);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
