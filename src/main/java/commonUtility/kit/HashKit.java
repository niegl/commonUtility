package commonUtility.kit;

public class HashKit {
    /**
     * 计算两个对象的hashcode值
     * @param object1
     * @param object2
     * @return
     */
    public static int hashCode(Object object1, Object object2) {
        int result = 1;
        result = result * 59 + (object1 == null ? 43 : object1.hashCode());
        result = result * 59 + (object2 == null ? 43 : object2.hashCode());
        return result;
    }
    /**
     * 计算三个对象的hashcode值
     * @param object1
     * @param object2
     * @param object3
     * @return
     */
    public static int hashCode(Object object1, Object object2, Object object3) {
        int result = 1;
        result = result * 59 + (object1 == null ? 43 : object1.hashCode());
        result = result * 59 + (object2 == null ? 43 : object2.hashCode());
        result = result * 59 + (object3 == null ? 43 : object3.hashCode());
        return result;
    }
}
