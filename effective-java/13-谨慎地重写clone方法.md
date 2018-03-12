# 13. 谨慎地重写clone方法

> 第三版

​    

### Object 的 clone 方法

Object 的 clone 方法是 protected 的。源码：

```java
protected native Object clone() throws CloneNotSupportedException;
```

如果某类要重写 clone 方法，必须要实现 Cloneable 接口。

Cloneable 接口里面虽然没有定义 clone 方法，但如果不实现 Cloneable 接口，调用 super.clone() 方法会报 CloneNotSupportedException 异常。

​    

### 如何重写 clone 方法

简单对象的 clone 方法：

```java
@Override
public MyClass clone() throws CloneNotSupportedException {
    try {
        return (MyClassv) super.clone();
    } catch (CloneNotSupportedException e) {
        // ...
    }
}
```

#### super.clone 方法的注意事项

如果是复杂类，比如拥有的成员是一个对象，直接 super.clone 会发现**只复制了引用**，而不是重新创建（克隆）对象。

解决方法：

```java
@Override public Stack clone() {
    try {
        Stack result = (Stack) super.clone();
        result.elements = elements.clone(); // Stack类里有Object[] elements成员
        return result;
    } catch (CloneNotSupportedException e) {
        // ...
    }
}
```

事实上，**数组是 clone 机制的唯一有力的用途。** 

还要注意，如果 elements 成员属性是 **final** 的，则该解决方案将不起作用，因为克隆将被禁止向该属性分配新的值。

​    

### 如何阻止重写 clone 方法

就如上面写的注意事项，clone 只会复制成员变量的引用（像 String、Integer、Double 等类是直接深度拷贝），所以真正要实现克隆是很复杂的，应尽量避免重写。

阻止子类重写 clone 方法：

```java
@Override
protected final Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
}
```

​    

### 代替 clone 的其他方法

```java
// 1.构造方法
public Yum(Yum yum) { ... };

// 2.静态工厂方法
public static Yum newInstance(Yum yum) { ... };
```

优点：它们不依赖风险很大的语言外的对象创建机制；不要求遵守那些不太明确的惯例；不会与 final 属性的正确使用相冲突; 不会抛出不必要的检查异常; 而且不需要类型转换。