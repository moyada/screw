package cn.moyada.screw.parser;

/**
 * @author xueyikang
 * @create 2018-04-10 00:42
 */
public class ProtostuffParser {//extends JsonParser {

//    private final Map<Class<?>, Schema<?>> schemaMap;
//
//    public ProtostuffParser() {
//        schemaMap = new HashMap<>();
//    }
//
//    @SuppressWarnings("unchecked")
//    private <T> Schema<T> getSchema(Class<T> tClass) {
//        Schema schema = schemaMap.get(tClass);
//        if(null == schema) {
//            schema = RuntimeSchema.getSchema(tClass);
//            schemaMap.put(tClass, schema);
//        }
//        return schema;
//    }
//
//    @Override
//    public String toJson(Object obj) {
////        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
////        Class<?> objClass = obj.getClass();
////        Schema<?> schema = getSchema(objClass);
////        byte[] bytes = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
//        return null;
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> String toString(T obj) {
//        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
//        Schema<T> schema = getSchema((Class<T>) obj.getClass());
//        byte[] bytes = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
//        return new String(bytes);
//    }
//
//    @Override
//    public <C> C toObject(String json, Class<C> c) {
//        Schema<C> schema = getSchema(c);
//        C obj = schema.newMessage();
//        ProtostuffIOUtil.mergeFrom(json.getBytes(), obj, schema);
//        return obj;
//    }
//
//    @Override
//    public <C> C[] toArray(String json, Class<C> c) {
//        return null;
//    }
//
//    @Override
//    public <C> List<C> toList(String json, Class<C> c) {
//        return null;
//    }
//
//    @Override
//    public <T, U> Map<T, U> toMap(String json, Class<T> t, Class<U> u) {
//        return null;
//    }
//
//    @Override
//    public <T, U> LinkedHashMap<T, List<U>> toLinkedMapList(String json, Class<T> t, Class<U> u) {
//        return null;
//    }
//
//    @Override
//    public <T, U> Map<T, List<U>> toMapList(String json, Class<T> t, Class<U> u) {
//        return null;
//    }
}
