package cn.moyada.screw.tools;

import cn.moyada.screw.model.OrderDO;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 生成insert or update 语句
 * Created by xueyikang on 2017/2/20.
 */
public class SQLBuilder {
    private String idFormat = "    <result id=\"%s\" property=\"%s\"/>";
    private String columnFormat = "    <result column=\"%s\" property=\"%s\"/>";

    public static <C> String buildInsertSql(String tableName, Class<C> cClass, int size) {
        StringBuilder sql = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append("(");
        try {
            sql.append(getAllFieldParam(cClass, null));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        sql.delete(sql.length()-2, sql.length())
                .append(") VALUE");
        try {
            for (int index = 0; index < size; index++) {
                sql.append("(")
                        .append(getAllFieldValue(cClass, index))
                        .delete(sql.length()-2, sql.length())
                        .append("), ");
            }
            sql.delete(sql.length()-2, sql.length())
                .append(" ON DUPLICATE KEY UPDATE ")
                .append(getAllFieldUpdate(cClass))
                .delete(sql.length()-2, sql.length());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        sql.append(";");
        return sql.toString();
    }

    private static <C> String getAllFieldValue(Class<C> cClass, int index) throws ClassNotFoundException {
        StringBuilder sql = new StringBuilder("");
        Class eClass = cClass.getSuperclass();
        if(null != eClass) {
            sql.append(getAllFieldValue(eClass, index));
        }
        Field[] fields = cClass.getDeclaredFields();
        Type type;
        for (Field field : fields) {
            type = field.getGenericType();
            if(type.getTypeName().startsWith("java")) {
                sql.append("#{")
                        .append(field.getName())
                        .append("_")
                        .append(index)
                        .append("}, ");
            }
            else {
                sql.append(getAllFieldValue(Class.forName(type.getTypeName()), index));
            }
        }
        return sql.toString();
    }

    public static <C> void buildSelectSql(String tableName, Class<C> cClass, String alias) {
        StringBuilder sql = new StringBuilder("SELECT ");
        try {
            sql.append(getAllFieldParam(cClass, alias));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        sql.delete(sql.length()-4, sql.length()).append("\nFROM ").append(tableName);
        if(null != alias) {
            sql.append(" ").append(alias);
        }

        sql.append(";");
        System.out.println(sql.toString());
    }

    public static <C> void buildInsertSql(String tableName, Class<C> cClass) {
        StringBuilder sql = new StringBuilder("INSERT INTO ")
                .append(tableName)
                .append("(");
        try {
            sql.append(getAllFieldParam(cClass, null));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        sql.delete(sql.length()-4, sql.length())
                .append(") \nVALUE(");
        try {
            sql.append(getAllFieldValue(cClass, true))
                    .delete(sql.length()-4, sql.length())
                    .append(") \nON DUPLICATE KEY UPDATE ")
                    .append(getAllFieldUpdate(cClass))
                    .delete(sql.length()-2, sql.length());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        sql.append(";");
        System.out.println(sql.toString());
    }

    private static <C> String getAllFieldUpdate(Class<C> cClass) throws ClassNotFoundException {
        StringBuilder sql = new StringBuilder("");
        Class eClass = cClass.getSuperclass();
        if(null != eClass) {
            sql.append(getAllFieldUpdate(eClass));
        }
        Field[] fields = cClass.getDeclaredFields();
        Type type;
        String column;
        for (Field field : fields) {
            type = field.getGenericType();
            if(type.getTypeName().startsWith("java")) {
                column = convert(field.getName());
                sql.append("\n")
                        .append(column)
                        .append(" = VALUES(")
                        .append(column)
                        .append("), ");
            }
            else {
                sql.append(getAllFieldUpdate(Class.forName(type.getTypeName())));
            }
        }
        return sql.toString();
    }

    private static <C> String getAllFieldParam(Class<C> cClass, String alias) throws ClassNotFoundException {
        StringBuilder sql = new StringBuilder("");
        Class eClass = cClass.getSuperclass();
        if(null != eClass && !"Object".equals(eClass.getSimpleName())) {
            sql.append(getAllFieldParam(eClass, alias));
        }
        Field[] fields = cClass.getDeclaredFields();
        Type type;
        int count = 0;
        for (Field field : fields) {
            if((++count % 4) == 0) {
                sql.append("\n");
            }
            type = field.getGenericType();
            if(type.getTypeName().startsWith("java")) {
                if(null != alias) {
                    sql.append(alias).append(".");
                }
                sql.append(convert(field.getName())).append(", ");
            }
            else {
                sql.append(getAllFieldParam(Class.forName(type.getTypeName()), alias));
            }
        }
        return sql.append("\n").toString();
    }


    private static <C> void printMapping(Class<C> cClass, boolean first) throws ClassNotFoundException {
        if(first) {
            System.out.println("<resultMap id=\"resultMap\" type=\"" + cClass.getName() + "\">");
        }
        String idFormat = "    <id column=\"%s\" property=\"%s\"/>";
        String columnFormat = "    <result column=\"%s\" property=\"%s\"/>";
        Class eClass = cClass.getSuperclass();
        if(null != eClass) {
            printMapping(eClass, false);
        }
        Field[] fields = cClass.getDeclaredFields();
        Type type;
        for (Field field : fields) {
            type = field.getGenericType();
            if(type.getTypeName().startsWith("java")) {
                String property = field.getName();
                String column = convert(field.getName());
                if("id".equals(property)) {
                    System.out.println(String.format(idFormat, column, property));
                }
                else {
                    System.out.println(String.format(columnFormat, column, property));
                }
            }
        }
        if(first) {
            System.out.println("</resultMap>");
        }
    }

    private static String convert(String str) {
        char[] chs = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char ch : chs) {
            if (Character.isUpperCase(ch)) {
                sb.append('_')
                        .append(Character.toLowerCase(ch));
            }
            else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static <C> String getAllFieldValue(Class<C> cClass, boolean className) throws ClassNotFoundException {
        StringBuilder sql = new StringBuilder("");
        Class eClass = cClass.getSuperclass();
        if(null != eClass) {
            sql.append(getAllFieldValue(eClass, true));
        }
        Field[] fields = cClass.getDeclaredFields();
        Type type;
        String simpleName = cClass.getSimpleName();
        if(className || "Object".equals(simpleName)) {
            simpleName = "";
        }
        else {
            simpleName = String.valueOf(simpleName.charAt(0)).toLowerCase()+simpleName.substring(1)+".";
        }
        for (Field field : fields) {
            type = field.getGenericType();
            if(type.getTypeName().startsWith("java")) {
                sql.append("#{")
                        .append(simpleName)
                        .append(field.getName())
                        .append("}, ");
            }
            else {
                sql.append(getAllFieldValue(Class.forName(type.getTypeName()), false));
            }
        }
        return sql.append("\n").toString();
    }

    public static void main(String[] args) throws ClassNotFoundException {
        printMapping(OrderDO.class, true);
        buildInsertSql("order", OrderDO.class);
        buildSelectSql("order", OrderDO.class, "a");
    }
}
