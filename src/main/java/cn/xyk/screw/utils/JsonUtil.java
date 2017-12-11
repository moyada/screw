package cn.xyk.screw.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xueyikang on 2017/8/6.
 */
public class JsonUtil {

    private static final Gson gson = new GsonBuilder().create();
    private static final JsonParser parser = new JsonParser();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T getObject(String json, Class<T> cls) {
        return gson.fromJson(json, cls);
    }

    public static <T> List<T> getList(String json, Class<T> cls) {
        JsonArray array = parser.parse(json).getAsJsonArray();
        int size = array.size();
        List<T> list = Lists.newArrayListWithExpectedSize(size);
        for(int index = 0; index < size; index++) {
            list.add(gson.fromJson(array.get(index), cls));
        }
        return list;
    }

    public <C, T> Map<C, T> getMap(String json, Class<C> keyCls, Class<T> valueCls) {
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> jsonSet = jsonObject.entrySet();
        Map<C, T> resultMap = Maps.newHashMapWithExpectedSize(jsonSet.size());
        C key;
        JsonElement itemValue;
        for(Map.Entry<String, JsonElement> item : jsonSet) {
            key = keyCls.cast(item.getKey());
            itemValue = item.getValue();
            resultMap.put(key, gson.fromJson(itemValue, valueCls));
        }
        return resultMap;
    }

    public <C, T> Map<C, List<T>> getMapList(String json, Class<C> keyCls, Class<T> valueCls) {
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> jsonSet = jsonObject.entrySet();
        Map<C, List<T>> resultMap = Maps.newHashMapWithExpectedSize(jsonSet.size());
        C key;
        List<T> value;
        JsonArray jsonArray;
        int size, index;
        for(Map.Entry<String, JsonElement> item : jsonSet) {
            key = keyCls.cast(item.getKey());
            jsonArray = item.getValue().getAsJsonArray();
            size = jsonArray.size();
            value = Lists.newArrayListWithExpectedSize(jsonArray.size());
            for(index = 0; index < size; index++) {
                value.add(gson.fromJson(jsonArray.get(index), valueCls));
            }
            resultMap.put(key, value);
        }
        return resultMap;
    }
}
