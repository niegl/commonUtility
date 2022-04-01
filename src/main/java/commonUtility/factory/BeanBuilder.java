package commonUtility.factory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jack
 * @version 1.0
 * @date 2019/7/4 11:13
 * @since JavaFX2.0 JDK1.8
 */
public class BeanBuilder implements Builder {

    @Override
    public <T> T getBean(Class<T> type) {
        T object = null;
        try {
            object = type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }
}
