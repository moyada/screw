package cn.moyada.screw.bytecode;

/**
 * @author xueyikang
 * @create 2018-04-27 01:10
 */
public class MethodInfo {

    private String methodName;

    private String body;

    private Class<?>[] parameterTypes;

    private Class<?> returnType;

    private Class<?>[] exceptions;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Class<?>[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(Class<?>[] exceptions) {
        this.exceptions = exceptions;
    }
}
