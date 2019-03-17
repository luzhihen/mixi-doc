package com.mixi.java.util.concurrent.atomic;
import sun.misc.Unsafe;

/**
 * 原子操作(使用cas操作)类（包括boolean,integer,long），线程安全
 * 对于i+=1操作，本身为俩条命令，非原子操作
 * 包括原子更新基本类型、数组、引用类型
 * 其他类 AtomicInteger针对Interger
 * AtomicLong 针对long
 * AtomicIntegerArray 针对 int[]
 * AtomicLongArray 针对 long[]
 * 引用类型：基本类型只能更新一个变量，引用可更新多个变量
 * AtomicReference(针对对象,希望整个对象)
 * AtomicReferenceArray:针对引用类型数组
 * AtomicMarkableReference:多标记位(数据，标记位：boolean)
 * AtomicStampedReference：多版本号，解决ABA问题
 *
 *
 * 原子更新字段：AtomicIntegerFieldUpdater,更新某个类某个字段，字段必须为int,valatile
 * AtomicLongFieldUpdater
 * AtomicReferenceFieldUpdater
 *
 */
public class AtomicBoolean implements java.io.Serializable {
    private static final long serialVersionUID = 4654671469794556979L;
    /**
     * Unsafe类使Java拥有了像C语言的指针一样操作内存空间的能力，同时也带来了指针的问题。
     * 过度的使用Unsafe类会使得出错的几率变大，因此Java官方并不建议使用的，
     * 官方文档也几乎没有。Oracle正在计划从Java 9中去掉Unsafe类，如果真是如此影响就太大了。
     */
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    /**
     * 操作对象的内存偏移位置
     * boolean类其实是操作int类型
     */
    private static final long valueOffset;

    /**
     * 获取value字段的内存偏移地址
     */
    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                    (AtomicBoolean.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    /**
     * 真实数据，内存可见
     */
    private volatile int value;


    public AtomicBoolean(boolean initialValue) {
        value = initialValue ? 1 : 0;
    }

    public AtomicBoolean() {
    }

    public final boolean get() {
        return value != 0;
    }

    /**
     * 第一个参数为希望的数据，第二个为更新数据
     * boolean类型转换为int类型，调用unsafe的compareAndSwapInt操作
     * compareAndSwapInt native操作
     * cas:JNI来完成CPU指令的操作,，程序会根据当前处理器的类型来决定是否为cmpxchg指令添加lock前缀。如果程序是在多处理器上运行，就为cmpxchg指令加上lock前缀（lock cmpxchg）。反之，如果程序是在单处理器上运行，就省略lock前缀
     * （单处理器自身会维护单处理器内的顺序一致性，不需要lock前缀提供的内存屏障效果）。
     * 存在问题：ABA问题
     * @param expect
     * @param update
     * @return
     */
    public final boolean compareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    /**
     *
     * @param expect
     * @param update
     * @return
     */
    public boolean weakCompareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }

    public final void set(boolean newValue) {
        value = newValue ? 1 : 0;
    }

    public final void lazySet(boolean newValue) {
        int v = newValue ? 1 : 0;
        unsafe.putOrderedInt(this, valueOffset, v);
    }

    /**
     * 设置参数病返回原来的数据
     * 自旋cas
     * @param newValue
     * @return
     */
    public final boolean getAndSet(boolean newValue) {
        boolean prev;
        do {
            prev = get();
        } while (!compareAndSet(prev, newValue));
        return prev;
    }

    @Override
    public String toString() {
        return Boolean.toString(get());
    }

}
