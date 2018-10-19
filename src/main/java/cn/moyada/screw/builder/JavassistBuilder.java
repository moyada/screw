package cn.moyada.screw.builder;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class JavassistBuilder extends BeanBuilder {

    private final ClassPool classPool;

    public JavassistBuilder() {
        classPool = ClassPool.getDefault();
    }

    public static BeanBuilder build() {
        return new JavassistBuilder();
    }

    @Override
    public Class generate() {
        CtClass newClass = classPool.makeClass(packageName + "." + className);

        return newClass(newClass);
    }

    @Override
    public Class generateWithClass(Class clazz) {
        CtClass newClass;
        try {
            newClass = classPool.get(clazz.getName());
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }

        return newClass(newClass);
    }

    private Class newClass(CtClass newClass) {
        addSuperClass(newClass);

        addInterface(newClass);

        addField(newClass);

        addMethod(newClass);

        addSuperClass(newClass);

        addConstruct(newClass);

        Class clazz;
        try {
            clazz = newClass.toClass();
        } catch (CannotCompileException e) {
            throw new RuntimeException(e);
        } finally {
            clear();
        }

        return clazz;
    }

    private void addSuperClass(CtClass newClass) {
        if(null == superClass) {
            return;
        }

        String name = superClass.getName();
        try {
            newClass.setSuperclass(classPool.get(name));
        } catch (CannotCompileException | NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void addInterface(CtClass newClass) {
        if(interfaceList.isEmpty()) {
            return;
        }

        int size = interfaceList.size();
        CtClass[] interfaceArr = new CtClass[size];
        Class interfaceClass;
        for (int index = 0; index < size; index++) {
            interfaceClass = interfaceList.get(index);
            try {
                interfaceArr[index] = classPool.get(interfaceClass.getName());
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        newClass.setInterfaces(interfaceArr);
    }

    private void addField(CtClass newClass) {
        if(classFiles.isEmpty()) {
            return;
        }

    }

    private void addMethod(CtClass newClass) {
        if(classMethods.isEmpty()) {
            return;
        }

    }

    private void addConstruct(CtClass newClass) {
        if(constructs.isEmpty()) {
            return;
        }
    }
}
