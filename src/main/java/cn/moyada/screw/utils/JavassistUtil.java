package cn.moyada.screw.utils;

import cn.moyada.screw.bytecode.MethodInfo;
import cn.moyada.screw.exception.ClassAlreadyExistsException;
import cn.moyada.screw.model.OrderDetailDO;
import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author xueyikang
 * @create 2018-04-27 00:40
 */
public class JavassistUtil {

    // CtClass对象容器
    private static final ClassPool classPool;
    
    private static final Method defineClass;

    static {
        classPool = ClassPool.getDefault();
        try {
            defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改继承类
     * @param clazz 当前类
     * @param superClazz 父类
     * @throws NotFoundException
     * @throws IOException
     * @throws CannotCompileException
     */
    public static void setSuperClass(Class clazz, Class superClazz) {
        String clazzName = clazz.getName();

        CtClass ctClass;
        try {
            ctClass = classPool.get(clazzName);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return;
        }

        CtClass superClass;
        try {
            superClass = classPool.get(superClazz.getName());
        } catch (NotFoundException e) {
            e.printStackTrace();
            return;
        }

        try {
            ctClass.setSuperclass(superClass);
        } catch (CannotCompileException e) {
            e.printStackTrace();
            return;
        }

        try {
            reloadClass(clazzName, ctClass);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建新类
     * @param className 类名
     * @param methodInfos 方法信息
     * @return 新类
     * @throws CannotCompileException
     * @throws NotFoundException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Class newClass(String className, MethodInfo... methodInfos) throws CannotCompileException, NotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(checkExists(className)) {
            throw new ClassAlreadyExistsException(className);
        }

        CtClass ctClass = classPool.makeClass(className);
        for (MethodInfo methodInfo : methodInfos) {
            ctClass.addMethod(buildMethod(ctClass, methodInfo));
        }
        reloadClass(className, ctClass);
        return ctClass.toClass();
    }

    /**
     * 创建接口
     * @param interfaceName 接口名称
     * @param methodInfos 方法信息
     * @return 新接口
     * @throws CannotCompileException
     * @throws NotFoundException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Class newInterface(String interfaceName, MethodInfo... methodInfos) throws CannotCompileException, NotFoundException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(checkExists(interfaceName)) {
            throw new ClassAlreadyExistsException(interfaceName);
        }

        CtClass ctClass = classPool.makeInterface(interfaceName);
        for (MethodInfo methodInfo : methodInfos) {
            ctClass.addMethod(buildAbstractMethod(ctClass, methodInfo));
        }

        reloadClass(interfaceName, ctClass);
        return ctClass.toClass();
    }

    /**
     * 重新加载class
     * @param className 类路径
     * @param ctClass 类实例
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static void reloadClass(String className, CtClass ctClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, CannotCompileException {
        byte[] bytes = ctClass.toBytecode();
        defineClass.invoke(ClassLoader.getSystemClassLoader(), className, bytes,0 , bytes.length);
    }

    /**
     * 检查类是否已存在
     * @param className 类路径
     * @return
     */
    private static boolean checkExists(String className) {
        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            return true;
        }
        return false;
    }

    /**
     * 封装类对象
     * @param clazz 原始类
     * @return javassist类
     * @throws NotFoundException
     */
    private static CtClass getCtClass(Class<?> clazz) throws NotFoundException {
        return classPool.get(clazz.getName());
    }

    /**
     * 封装类对象
     * @param classes 原始类数组
     * @return javassist类数组
     * @throws NotFoundException
     */
    private static CtClass[] getCtClass(Class<?>[] classes) throws NotFoundException {
        String classNames[] = new String[classes.length];
        for (int index = 0; index < classes.length; index++) {
            classNames[index] = classes[index].getName();
        }
        return classPool.get(classNames);
    }

    /**
     * 封装方法
     * @param ctClass javassist类
     * @param methodInfo 方法信息
     * @return javassist方法
     * @throws NotFoundException
     */
    private static CtMethod buildMethod(CtClass ctClass, MethodInfo methodInfo) throws NotFoundException, CannotCompileException {
        return CtNewMethod.make(getCtClass(methodInfo.getReturnType()),
                methodInfo.getMethodName(), getCtClass(methodInfo.getParameterTypes()), getCtClass(methodInfo.getExceptions()), methodInfo.getBody(), ctClass);
//        return new CtMethod(getCtClass(methodInfo.getReturnType()),
//                methodInfo.getMethodName(), getCtClass(methodInfo.getParameterTypes()), ctClass);
    }

    /**
     * 封装抽象方法
     * @param ctClass javassist类
     * @param methodInfo 方法信息
     * @return javassist方法
     * @throws NotFoundException
     */
    private static CtMethod buildAbstractMethod(CtClass ctClass, MethodInfo methodInfo) throws NotFoundException {
        return CtNewMethod.abstractMethod(getCtClass(methodInfo.getReturnType()),
                methodInfo.getMethodName(), getCtClass(methodInfo.getParameterTypes()), getCtClass(methodInfo.getExceptions()), ctClass);
    }

    public static void reset(String className) throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CtClass ctClass = classPool.get(className);

        CtClass paramType = classPool.getCtClass("java.lang.Class[]");
        CtMethod ctMethod = ctClass.getDeclaredMethod("getProxy", new CtClass[]{paramType});
//        ctMethod.setBody("System.out.println(\"faker\");");
        ctMethod.instrument(
                new ExprEditor() {
                    public void edit(MethodCall m) throws CannotCompileException {
                        m.replace("System.out.println(\"faker\");");
                    }
                });
        reloadClass(className, ctClass);
        System.out.println(className);
    }

    public static void main(String[] args) throws Exception {
        String className = "cn.moyada.screw.model.OrderDetailDO";
//        String superClassName = "cn.moyada.screw.model.OrderDO";
        JavassistUtil.reset(className);
        new OrderDetailDO().test();
//        javassistUtil.changeSuperClass(className, superClassName);
//        Class<?> aClass = Class.forName(className);
//        System.out.println(aClass.getSuperclass());
    }
}
