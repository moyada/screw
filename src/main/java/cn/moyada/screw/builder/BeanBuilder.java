package cn.moyada.screw.builder;

import cn.moyada.screw.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class BeanBuilder {

    protected String className = null;

    protected String packageName = null;

    protected Class superClass = null;

    protected List<Class> interfaceList = new ArrayList<>();

    protected List<ClassData.Construct> constructs = new ArrayList<>();

    protected List<ClassData.File> classFiles = new ArrayList<>();

    protected List<ClassData.Method> classMethods = new ArrayList<>();

    public abstract Class generate();

    public abstract Class generateWithClass(Class clazz);

    public BeanBuilder setClassName(String className) {
        if(StringUtil.isEmpty(className)) {
            throw new NullPointerException("className can not be null.");
        }
        if(checkPath(className)) {
            throw new IllegalArgumentException("className must begin with capital letter.");
        }
        this.className = className;
        return this;
    }

    public BeanBuilder setPackage(String packageName) {
        if(StringUtil.isEmpty(packageName)) {
            throw new NullPointerException("packageName can not be null.");
        }
        if(!checkPackage(packageName)) {
            throw new IllegalArgumentException("per package path must begin with capital letter..");
        }
        this.packageName = packageName;
        return this;
    }

    public BeanBuilder setSuperClass(Class superClass) {
        if(null == superClass) {
            throw new NullPointerException("superClass can not be null.");
        }
        this.superClass = superClass;
        return this;
    }

    public BeanBuilder addInterface(Class interfaceClass) {
        if(null == interfaceClass) {
            throw new NullPointerException("interface can not be null.");
        }
        this.interfaceList.add(interfaceClass);
        return this;
    }

    public BeanBuilder addConstruct(String body, Class... params) {
        this.constructs.add(new ClassData.Construct());
        return this;
    }

    public <T> BeanBuilder addFile(String name, Class<T> type) {
        return addFile(name, type, null);
    }

    public <T> BeanBuilder addFile(String name, Class<T> type, T value) {
        if(StringUtil.isEmpty(name)) {
            throw new NullPointerException("fileName can not be null.");
        }
        if(null == type) {
            throw new NullPointerException("fileType can not be null.");
        }
        if(!checkLetter(name)) {
            throw new IllegalArgumentException("file name must begin with letter.");
        }
        this.classFiles.add(new ClassData.File<T>(name, type, value));
        return this;
    }

    public BeanBuilder addMethod(ClassData.Method method) {
        if(null == method) {
            throw new NullPointerException("method can not be null.");
        }
        this.classMethods.add(method);
        return this;
    }

    private boolean checkLetter(String name) {
        return !Character.isLetter(name.indexOf(0));
    }

    private boolean checkPath(String name) {
        return !Character.isUpperCase(name.indexOf(0));
    }

    private boolean checkPackage(String packageName) {
        if(!packageName.contains(".")) {
            return checkPath(packageName);
        }
        StringTokenizer stringTokenizer = new StringTokenizer(packageName, ".");
        String path;
        while (stringTokenizer.hasMoreTokens()) {
            path = stringTokenizer.nextToken();
            if(!checkPath(path)) {
                return false;
            }
        }
        return true;
    }

    protected void clear() {
        className = null;
        packageName = null;
        superClass = null;
        constructs.clear();
        interfaceList.clear();
        classFiles.clear();
        classMethods.clear();
    }
}
