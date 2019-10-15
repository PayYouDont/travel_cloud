package com.gospell.travel.common.util;

import android.content.Context;

import com.gospell.travel.common.annotation.Value;

import org.litepal.util.LogUtil;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class ReflectUtil {
    public static List<Field> getFields(Class<?> clazz) {
        //属性集合
        List<Field> fieldList = new ArrayList<> ();
        //获取实体类属性
        Field[] fields = clazz.getDeclaredFields();
        fieldList = addFildToList(fieldList, fields);
        //获取父类属性
        if(clazz.getSuperclass() instanceof Class) {
            Field[] supfields = clazz.getSuperclass().getDeclaredFields();
            fieldList = addFildToList(fieldList, supfields);
        }
        return fieldList;
    }
    public static List<Field> addFildToList(List<Field> fieldList,Field[] fields){
        for(int i=0;i<fields.length;i++) {
            Field field = fields[i];
            field.setAccessible(true);
            fieldList.add(field);
        }
        return fieldList;
    }
    public static Class<?> getGeneric(Collection<?> collection){
        if(collection.size()>0) {
            return collection.iterator().next().getClass();
        }
        return null;
    }
    public static List<Field> getFields(Collection<?> collection) {
        return getFields(getGeneric(collection));
    }
    public static void initFieldByAnnotation(Class clazz,Class annotationClass,OnAnnotationCallback onAnnotationCallback){
        initFieldByAnnotation (clazz,annotationClass,onAnnotationCallback,true);
    }
    public static void initFieldByAnnotation(Class clazz,Class annotationClass,OnAnnotationCallback onAnnotationCallback,boolean isSingle){
        List<Field> fieldList = getFields (clazz);
        fieldList.forEach (field -> {
            Annotation annotation = field.getAnnotation (annotationClass);
            if(annotation!=null){
                onAnnotationCallback.setField (annotation,field);
                if(isSingle){
                    return;
                }
            }
        });
    }
    public interface OnAnnotationCallback{
        void setField(Annotation annotation,Field field);
    }
    public static void initFieldByConfig(Object object, Context context){
        Properties props = new Properties();
        try {
            InputStream in = context.getAssets().open("appConfig");
            props.load(in);
        } catch (Exception e) {
            LogUtil.e (ReflectUtil.class.getName (),e);
        }
        initFieldByAnnotation (object.getClass (), Value.class,(annotation, field) -> {
            try {
                if(field.getType ().getName ().toLowerCase ().indexOf ("int")!=-1){
                    field.set (object,Integer.valueOf (props.getProperty (((Value)annotation).value ())));
                }else {
                    field.set (object,props.getProperty (((Value)annotation).value ()));
                }
            }catch (Exception e){
                LogUtil.e (ReflectUtil.class.getName (),e);
            }
        },false);
    }
}
