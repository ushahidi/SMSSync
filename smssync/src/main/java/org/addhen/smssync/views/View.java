/*******************************************************************************
 *  Copyright (c) 2010 - 2013 Ushahidi Inc
 *  All rights reserved
 *  Contact: team@ushahidi.com
 *  Website: http://www.ushahidi.com
 *  GNU Lesser General Public License Usage
 *  This file may be used under the terms of the GNU Lesser
 *  General Public License version 3 as published by the Free Software
 *  Foundation and appearing in the file LICENSE.LGPL included in the
 *  packaging of this file. Please review the following information to
 *  ensure the GNU Lesser General Public License version 3 requirements
 *  will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 ******************************************************************************/

package org.addhen.smssync.views;

import android.app.Activity;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Base class for Views
 *
 * Handles dynamically loading all the sub-classes members tagged with @Widget annotation
 */
public abstract class View {

    /**
     * View Map View
     *
     * @param activity Activity
     */
    public View(Activity activity) {
        for (Class<?> clazz : new Class[]{getClass(),
                getClass().getSuperclass()}) {
            if (clazz != null && View.class.isAssignableFrom(clazz)) {
                for (Field field : clazz.getDeclaredFields()) {
                    try {
                        Annotation annotation = field
                                .getAnnotation(Widget.class);
                        if (annotation instanceof Widget) {
                            Widget widgetAnnotation = (Widget) annotation;
                            if (!field.isAccessible()) {
                                field.setAccessible(true);
                            }
                            field.set(this, activity
                                    .findViewById(widgetAnnotation.value()));
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e(getClass().getSimpleName(),
                                "IllegalArgumentException", e);
                    } catch (IllegalAccessException e) {
                        Log.e(getClass().getSimpleName(),
                                "IllegalAccessException", e);
                    }
                }
            }
        }
    }

    /**
     * View
     *
     * @param view View
     */
    public View(android.view.View view) {
        for (Class<?> clazz : new Class[]{getClass(),
                getClass().getSuperclass()}) {
            if (clazz != null && View.class.isAssignableFrom(clazz)) {
                for (Field field : clazz.getDeclaredFields()) {
                    try {
                        Annotation annotation = field
                                .getAnnotation(Widget.class);
                        if (annotation instanceof Widget) {
                            Widget widgetAnnotation = (Widget) annotation;
                            if (!field.isAccessible()) {
                                field.setAccessible(true);
                            }
                            field.set(this,
                                    view.findViewById(widgetAnnotation.value()));
                        }
                    } catch (IllegalArgumentException e) {
                        Log.e(getClass().getSimpleName(),
                                "IllegalArgumentException", e);
                    } catch (IllegalAccessException e) {
                        Log.e(getClass().getSimpleName(),
                                "IllegalAccessException", e);
                    }
                }
            }
        }
    }

}
