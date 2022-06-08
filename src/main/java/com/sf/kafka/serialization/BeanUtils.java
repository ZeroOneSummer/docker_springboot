package com.sf.kafka.serialization;

import java.io.*;

public class BeanUtils {

    private BeanUtils() {}

    /** 
     * 对象序列化为byte数组
     */  
    public static byte[] objToByte(Object obj) {
        byte[] baos = null;
        try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteArray)){
            outputStream.writeObject(obj);  
            outputStream.flush();  
            baos = byteArray.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();  
        }  
        return baos;
    }

    /** 
     * 字节数组转为Object对象
     */  
    public static Object byteToObj(byte[] bytes) {
        Object readObject = null;  
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             ObjectInputStream inputStream = new ObjectInputStream(in)){
             readObject = inputStream.readObject();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }   
        return readObject;  
    }  
}  
