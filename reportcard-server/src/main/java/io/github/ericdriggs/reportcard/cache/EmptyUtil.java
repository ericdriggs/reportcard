package io.github.ericdriggs.reportcard.cache;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class EmptyUtil {

    /**
     * @return if the cache does not have a value
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            String str = (String) obj;
            return StringUtils.isEmpty(str.trim());
        } else if (obj instanceof Collection<?>) {
            return CollectionUtils.isEmpty((Collection) obj);
        } else if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        } else if (obj.getClass().isArray()) {
            if (Array.getLength(obj) <= 0) {
                return true;
            }
        } else {
            {
                Method sizeMethod = getSizeMethod("length", obj);
                if (sizeMethod != null) {
                    return isSizeEmpty(invokeSizeMethod(sizeMethod, obj));
                }
            }
            {
                Method sizeMethod = getSizeMethod("size", obj);
                if (sizeMethod != null) {
                    return isSizeEmpty(invokeSizeMethod(sizeMethod, obj));
                }
            }
        }

        //Non-null POJO isn't empty
        return false;
    }

    /**
     * @param methodName
     * @return method matching methodName or <code>NULL</code> if no match
     */
    private static Method getSizeMethod(String methodName, Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private static boolean isSizeEmpty(Integer size) {
        if (size == null || size <= 0) {
            return true;
        }
        return false;
    }

    /**
     * @return true if method exists in class and is empty
     */
    private static int invokeSizeMethod(Method method, Object obj) {
        try {
            return (int) method.invoke(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}
