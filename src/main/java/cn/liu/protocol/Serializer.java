package cn.liu.protocol;

import cn.liu.message.Message;
import com.google.gson.Gson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.protocol
 * @Date 2025/7/6 10:32
 * @description:
 */
public interface Serializer {

    // 反序列化
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    // 序列化
    <T> byte[] serialize(T object);

    enum Algorithm implements Serializer {
        // Java序列化实现
        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    Object object = ois.readObject();

                    return (T) object;
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("SerializerAlgorithm.Java 反序列化错误", e);
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("SerializerAlgorithm.Java 序列化错误", e);
                }

            }
        },

        // Json实现 引入Gson依赖
        Json {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                return new Gson().fromJson(new String(bytes, StandardCharsets.UTF_8), clazz);
            }

            @Override
            public <T> byte[] serialize(T object) {
                return new Gson().toJson(object).getBytes(StandardCharsets.UTF_8);
            }
        }
    }
}
