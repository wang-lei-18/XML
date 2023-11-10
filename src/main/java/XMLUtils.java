import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * XML工具类
 * 1、属性名与xml节点名称必须一致
 * 2、属性类型为8大基本类型时，只能是8大基本类型对应的包装类
 * 3、集合只支持List(实际上是使用ArrayList实现)
 */
public class XMLUtils {

    /**
     * xml转对象
     * @param XMLStr xml字符串
     * @param clazz 需要转换的对象
     * @return
     */
    public static Object XMLToObject(String XMLStr, Class clazz) {
        try {
            // 获取xml字符转成Document
            Document doc = DocumentHelper.parseText(XMLStr);
            // 通过doc获取根节点
            Element root = doc.getRootElement();
            return handleFieldObject(clazz, root);
        } catch (Exception e) {
            throw new XMLException(e);
        }
    }

    /**
     * 给属性赋值
     * @param o 需要赋值的对象
     * @param field 赋值的属性
     * @param e 属性对应的xml节点
     */
    public static void setFieldData(Object o, Field field, Element e) {
        try {
            // 访问private修饰的属性
            field.setAccessible(true);
            // 获取属性的class
            Class fieldClazz = field.getType();
            // 获取属性对应的节点
            Element fieldEle = e.element(field.getName());
            if (fieldClazz.equals(String.class)) {
                // 类为String
                field.set(o, fieldEle.getText());
            } else if(checkIsBasicClass(fieldClazz)) {
                // 类为基础类
                field.set(o, handleFieldBase(fieldClazz, fieldEle));
            } else if (fieldClazz.equals(List.class)) {
                // 类为List
                field.set(o, handleFieldList(fieldClazz, fieldEle, field));
            } else {
                // 自定义类
                // 创建属性对象
                Object childObj = handleFieldObject(fieldClazz, fieldEle);
                // 属性赋值
                field.set(o, childObj);
            }
        } catch (Exception err) {
            throw new XMLException(err);
        }
    }

    /**
     * 判断是否为基础类
     * 基础类：八大基本对象的包装类
     * @param clazz 待判断的类
     * @return 是：true，否：false
     */
    public static boolean checkIsBasicClass(Class clazz) {
        if (
                clazz.equals(Byte.class) || clazz.equals(Short.class)
                || clazz.equals(Integer.class) || clazz.equals(Long.class)
                || clazz.equals(Float.class) || clazz.equals(Double.class)
                || clazz.equals(Character.class) || clazz.equals(Integer.class)
        ) {
            return true;
        }
        return false;
    }

    /**
     * 处理属性为自定义类
     * @param clazz 属性对应的类
     * @param e 属性对应的节点
     * @return 属性的值
     */
    public static Object handleFieldObject(Class clazz, Element e) {
        try {
            // 创建对象
            Object o = createObject(clazz);
            // 获取类属性 包括private声明的
            Field[] fields = clazz.getDeclaredFields();
            // 遍历类属性
            for (Field field : fields) {
                setFieldData(o, field, e);
            }
            return o;
        } catch (Exception error) {
            throw new XMLException(error);
        }
    }

    /**
     * 处理属性为List类型
     * @param clazz 属性的类
     * @param e 属性对应节点
     * @param field 属性
     * @return 属性的值
     */
    public static Object handleFieldList(Class clazz, Element e, Field field) {
        try {
            Object childObj = null;
            Object listObj = new ArrayList<>();
            // 获取属性的类型
            Type fc = field.getGenericType();
            // 判断是否带有泛型的类型
            if (fc instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) fc;
                // 获取泛型的类
                Class genericClazz = (Class)pt.getActualTypeArguments()[0];
                // 获取泛型类对应的节点
                List<Element> objEs = e.elements(genericClazz.getName());
                // 遍历节点
                for (Element objE : objEs) {
                    // 获取对象
                    childObj = handleFieldObject(genericClazz, objE);
                    // 获取list的add方法
                    Method addMethod = clazz.getMethod("add", Object.class);
                    // 执行add方法
                    addMethod.invoke(listObj, childObj);
                }
            }
            return listObj;
        } catch (Exception err) {
            throw new XMLException(err);
        }
    }

    /**
     * 处理属性为8大基础类型的包装类
     * @param clazz 属性类
     * @param e 属性节点
     * @return 属性的值
     */
    public static Object handleFieldBase(Class clazz, Element e) {
        // Byte类型
        if (clazz.equals(Byte.class)) {
            return new Byte(e.getText());
        }
        // Short类型
        if (clazz.equals(Short.class)) {
            return new Short(e.getText());
        }
        // Integer类型
        if (clazz.equals(Integer.class)) {
            return new Integer(e.getText());
        }
        // Long类型
        if (clazz.equals(Long.class)) {
            return new Long(e.getText());
        }
        // Float类型
        if (clazz.equals(Float.class)) {
            return new Float(e.getText());
        }
        // Double类型
        if (clazz.equals(Double.class)) {
            return new Double(e.getText());
        }
        // Character类型
        if (clazz.equals(Character.class)) {
            return new Character(e.getText().charAt(0));
        }
        // Boolean类型
        if (clazz.equals(Boolean.class)) {
            return new Boolean(e.getText());
        }
        return null;
    }

    /**
     * 创建实例
     * @param clazz 属性类
     * @return 返回创建实例
     */
    public static Object createObject(Class clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new XMLException("没有无参构造方法");
        }
    }
}
