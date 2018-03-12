# 10. 重写equals方法时遵守通用约定

> 第三版

​    

重写 equals 方法时，需要判断类型，此时不必要判断 null ：

```java
@Override public boolean equals(Object o) {
    if (!(o instanceof MyType))
        return false;
    MyType mt = (MyType) o;
    ...
}
```

如果 o 为 null， `o instanceof MyType` 会直接返回 false。

​    

对于 **float** 基本类型的属性，使用静态 `Float.compare(float, float)` 方法；

对于 **double** 基本类型的属性，使用 `Double.compare(double, double)` 方法。

由于存在 `Float.NaN`，`-0.0f` 和类似的 double 类型的值，所以需要对 float 和 double 属性进行特殊的处理。

注意，使用静态方法 Float.equals 和 Double.equals 方法对 float 和 double 基本类型的属性进行比较，会导致每次比较时发生自动装箱，引发非常差的性能。

​    

如果**数组**属性中的每个元素都很重要，请使用其中一个重载的 Arrays.equals 方法。

某些**对象**引用的属性可能合法地包含 null。 为避免出现 NullPointerException 异常，请使用静态方法 Objects.equals(Object, Object) 检查这些属性是否相等。

​    

**重写 equals 方法时，hashCode 方法也需要重写。**

**equals 方法的参数必须是 Object 的。**如果没有加 @Override 注解，会变成 equals 的重载，而不是重写。

