/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */
package org.addhen.smssync.util;

import android.util.Log;
import android.view.InflateException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Objects {

    /**
     * Helps to avoid using {@code @SuppressWarnings( "unchecked"})} when casting to a generic
     * type.
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends T> X cast(T o) {
        return (X) o;
    }

    public static Type[] getGenericTypes(Object obj) {
        ParameterizedType superclass = (ParameterizedType) obj.getClass()
                .getGenericSuperclass();
        return superclass.getActualTypeArguments();
    }

    public static Type getGenericType(Object obj, int index) {
        ParameterizedType superclass = (ParameterizedType) obj.getClass()
                .getGenericSuperclass();
        return superclass.getActualTypeArguments()[index];
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<?> targetClass) {
        try {
            return (T) targetClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class type, Class constructor,
                                       Object... params) {
        try {
            return (T) type.getConstructor(constructor).newInstance(params);
        } catch (InstantiationException e) {
            Log.e(Objects.class.getName(), "InstantiationException", e);
        } catch (IllegalAccessException e) {
            Log.e(Objects.class.getName(), "IllegalAccessException", e);
        } catch (InvocationTargetException e) {
            Log.e(Objects.class.getName(), "InvocationTargetException", e);
        } catch (NoSuchMethodException e) {
            Log.e(Objects.class.getName(), "NoSuchMethodException", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class type, Class[] constructors,
                                       Object... params) {
        try {
            return (T) type.getConstructor(constructors).newInstance(params);
        } catch (InstantiationException e) {
            Log.e(Objects.class.getName(), "InstantiationException", e);
        } catch (IllegalAccessException e) {
            Log.e(Objects.class.getName(), "IllegalAccessException", e);
        } catch (IllegalStateException e) {
            Log.e(Objects.class.getName(), "IllegalStateException", e);
        } catch (InvocationTargetException e) {
            Log.e(Objects.class.getName(), "InvocationTargetException", e);
            for (StackTraceElement exception : e.getStackTrace()) {
                Log.e(Objects.class.getName(),
                        String.format("%s", exception.toString()));
            }
        } catch (NoSuchMethodException e) {
            Log.e(Objects.class.getName(), "NoSuchMethodException", e);
        } catch (InflateException e) {
            Log.e(Objects.class.getName(), "InflateException", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Type type, Class[] constructorClasses,
                                       Object[] constructorParams) {
        try {
            Log.i("Type", type.toString());
            if (type.getClass().getConstructors().length > 0) {
                for (Constructor construct : type.getClass().getConstructors()) {
                    Log.i("Constructor", construct.toString());
                }
                Constructor<? extends Type> constructor = type.getClass()
                        .getConstructor(constructorClasses);
                return (T) constructor.newInstance(constructorParams);
            } else {
                // HACK to resolve generic types losing their class information
                // at runtime
                String typeName = type.toString().replaceFirst("class ", "");
                for (Constructor construct : Class.forName(typeName)
                        .getConstructors()) {
                    Log.i("Constructor", construct.toString());
                }
                Constructor<? extends Type> constructor = (Constructor<? extends Type>) Class
                        .forName(typeName).getConstructor(constructorClasses);
                return (T) constructor.newInstance(constructorParams);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
