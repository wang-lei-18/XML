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

    // 对象转XML
    public static String ObjectToXML(Object o) {
        try {
            if (o != null) {
                // 获取xml的Document
                Document document = DocumentHelper.createDocument();
                // 获取对象类
                Class clazz = o.getClass();
                // 创建根节点
                Element element = document.addElement(clazz.getName());
                // 处理对象转为xml
                handleXmlObject(element, o);
                return document.asXML();
            } else {
                throw new XMLException("转换对象不能为空");
            }
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
            if (String.class.equals(fieldClazz)) {
                // 类为String
                field.set(o, fieldEle.getText());
            } else if(checkIsBasicClass(fieldClazz)) {
                // 类为基础类
                field.set(o, handleFieldBase(fieldClazz, fieldEle));
            } else if (List.class.equals(fieldClazz)) {
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
                Byte.class.equals(clazz) || Short.class.equals(clazz)
                || Integer.class.equals(clazz) || Long.class.equals(clazz)
                || Float.class.equals(clazz) || Double.class.equals(clazz)
                || Character.class.equals(clazz) || Boolean.class.equals(clazz)
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
            Class genericClazz = getFieldListGenericClazz(field);
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
        if (null == e) {
            throw new XMLException("处理八大基础类型包装类节点为空");
        }
        if (null == clazz) {
            throw new XMLException("处理八大基础类型包装类类为空");
        }
        // 获取节点的内容
        String text = e.getText();
        // Byte类型
        if (Byte.class.equals(clazz)) {
            return new Byte(text);
        }
        // Short类型
        if (Short.class.equals(clazz)) {
            return new Short(text);
        }
        // Integer类型
        if (Integer.class.equals(clazz)) {
            return new Integer(text);
        }
        // Long类型
        if (Long.class.equals(clazz)) {
            return new Long(text);
        }
        // Float类型
        if (Float.class.equals(clazz)) {
            return new Float(text);
        }
        // Double类型
        if (Double.class.equals(clazz)) {
            return new Double(text);
        }
        // Character类型
        if (Character.class.equals(clazz)) {
            return new Character(text.charAt(0));
        }
        // Boolean类型
        if (Boolean.class.equals(clazz)) {
            return new Boolean(text);
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

    /**
     * 获取属性的泛型类
     * @param field
     * @return
     */
    public static Class getFieldListGenericClazz(Field field) {
        // 泛型类
        Class genericClazz = null;
        // 获取属性的类型
        Type fc = field.getGenericType();
        if (fc instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) fc;
            // 获取泛型的类
            genericClazz = (Class) pt.getActualTypeArguments()[0];
        }
        if (null == genericClazz) {
            throw new XMLException("未找到List对应的泛型类");
        }
        return genericClazz;
    }

    /**
     * 对象转XML：获取XML
     * @param field 属性
     * @param element 父节点
     * @param o 属性对象
     */
    public static void setXml(Field field, Element element, Object o) {
        try {
            // 设置private修饰的属性也能访问
            field.setAccessible(true);
            // 设置属性对应的节点
            Element fieldE = element.addElement(field.getName());
            if (String.class.equals(field.getType())) {
                // String类型 节点添加内容
                fieldE.setText(field.get(o).toString());
            } else if (checkIsBasicClass(field.getType())) {
                // 八大基本类型 节点添加内容
                fieldE.setText(field.get(o).toString());
            } else if (List.class.equals(field.getType())) {
                // List类型
                handleXmlList(fieldE, o, field);
            } else {
                // 自定义类
                handleXmlObject(fieldE, field.get(o));
            }
        }catch (Exception e) {
            throw new XMLException(e);
        }
    }

    /**
     * 对象转XML：属性对象为自定义对象时的处理方式
     * @param e 父节点
     * @param o 属性对象
     */
    public static void handleXmlObject(Element e, Object o) {
        // 获取对象的类
        Class clazz = o.getClass();
        // 获取类的所有属性，包括private修饰的
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 获取XML节点
            setXml(field, e, o);
        }
    }

    /**
     * 对象转XML：属性对象为List时的处理方式
     * @param e 父节点
     * @param field List对应的属性
     * @param o 属性对象
     */
    public static void handleXmlList(Element e, Object o, Field field) {
        try {
            // 获取List的泛型
            Class genericClazz = getFieldListGenericClazz(field);
            // 将泛型的名称放入XML节点中
            Element genericE =  e.addElement(genericClazz.getName());
            // 获取List数据
            List list = (ArrayList)field.get(o);
            for (Object obj : list) {
                // 将List数据转换成XML节点
                handleXmlObject(genericE, obj);
            }
        } catch (Exception err) {
            throw new XMLException(err);
        }
    }
}
