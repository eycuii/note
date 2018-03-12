# 11. 重写equals方法时同时也要重写hashcode方法

> 第三版

​    

### 规定

1. 重复调用 hashCode 方法时，必须始终返回相同的值。
2. 如果 equals(Object) 为 true，那么 hashCode 就必须是相同的整数。
3. 而如果 equals(Object) 为 false，并**不要求**在每个对象上调用 hashCode 都必须产生不同的结果。因为 hashCode 可能会冲突了。

​    

### 计算 hashCode 的方法

如下，根据几个字段混合计算出 hashCode 时，可以用 `result = 31 * result + 基本类型的hashCode` 方法。

```java
@Override public int hashCode() {
    int result = Short.hashCode(areaCode);
    result = 31 * result + Short.hashCode(prefix);
    result = 31 * result + Short.hashCode(lineNum);
    return result;
}
```

之所以选择 31，因为它是一个奇数的素数。 如果它是偶数，并且乘法溢出，信息将会丢失，因为乘以 2 相当于移位。 使用素数的好处不太明显，但习惯上都是这么做的。 31 的一个很好的特性，是在一些体系结构中乘法可以被替换为移位和减法以获得更好的性能：`31 * i ==（i << 5） - i`。 现代 JVM 可以自动进行这种优化。

> 虽然在这个项目的方法产生相当好的哈希函数，但并不是最先进的。 它们的质量与 Java 平台类库的值类型中找到的哈希函数相当，对于大多数用途来说都是足够的。 如果真的需要哈希函数而不太可能产生碰撞，请参阅 Guava 框架的的 [com.google.common.hash.Hashing](http://com.google.common.hash.hashing/) [Guava] 方法。

​    

#### 不建议使用 Objects.hash 方法

`Objects` 类有一个静态方法，它接受任意数量的对象并为它们返回一个哈希码。 这个名为 hash 的方法可以让你编写一行 hashCode 方法，其质量与根据这个项目中的上面编写的方法相当。 不幸的是，它们的运行速度更慢，因为它们需要创建数组以传递可变数量的参数，以及如果任何参数是基本类型，则进行装箱和取消装箱。 这种哈希函数的风格建议仅在性能不重要的情况下使用。 例：

```java
@Override public int hashCode() {
   return Objects.hash(lineNum, prefix, areaCode);
}
```

