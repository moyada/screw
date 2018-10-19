package cn.moyada.screw.builder;

public final class ClassData {

    public static void main(String[] args) {
        JavassistBuilder.build()
                .setPackage("cn.moyada.async")
                .setClassName("Master");
    }

    static class Construct {

        Scope scope;

        Class[] parameterTypes;

        String body;
    }

    static class File<T> {

        String name;

        Class<T> clazz;

        T value;

        Scope scope;

        Type type;

        public File(String name, Class<T> clazz, T value) {
            this.name = name;
            this.clazz = clazz;
            this.value = value;
        }

        public void setScope(Scope scope) {
            this.scope = scope;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }

    static class Method {

        String name;

        String body;

        Class[] parameterTypes;

        Class returnType;

        Throwable[] exceptions;

        Scope scope;

        Type type;
    }

    enum Scope {

        PUBLIC,

        PRIVATE,

        PROTECTED,

        DEFAULT,
    }

    enum Type {

        VOLATILE,

        FINAL,

        STATIC,
    }
}
