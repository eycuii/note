# 31. 使用限定通配符来增加API的灵活性

> 第三版

​    

通常，**如果类型参数在方法声明中只出现一次，请将其替换为通配符**。

```java
public static <E> void swap(List<E> list, int i, int j);

public static void swap(List<?> list, int i, int j); // 推荐（更简单）
```

然而，第二个 `swap `方法声明有一个问题。 这个简单的实现不会编译：

```java
public static void swap(List<?> list, int i, int j) {
    list.set(i, list.set(j, list.get(i))); // 编译出错
}
```

问题是列表的类型是 `List <？>`，并且不能将除 null 外的任何值放入 `List <？>` 中。幸运的是，有一种方法可以在不使用不安全的转换或原始类型的情况下实现此方法。这个想法是写一个私有辅助方法来捕捉通配符类型。辅助方法必须是泛型方法才能捕获类型。如：

```java
public static void swap(List<?> list, int i, int j) {
    swapHelper(list, i, j);
}

// Private helper method for wildcard capture
private static <E> void swapHelper(List<E> list, int i, int j) {
    list.set(i, list.set(j, list.get(i)));
}
```

`swapHelper ` 方法知道该列表是一个 `List <E>`。 因此，它知道从这个列表中获得的任何值都是 E 类型，并且可以安全地将任何类型的 `E` 值放入列表中。 

这个稍微复杂的 `swap` 的实现可以干净地编译。客户端不需要面对更复杂的 `swapHelper` 声明，但他们从中受益。 辅助方法具有我们认为对公共方法来说过于复杂的签名。

总之，在你的 API 中使用通配符类型，虽然棘手，但使得 API 更加灵活。 如果编写一个将被广泛使用的类库，正确使用通配符类型应该被认为是强制性的。 记住基本规则： producer-extends, consumer-super（PECS）。 还要记住，所有 `Comparable `和 `Comparator` 都是消费者。

​    

PECS：**producer-extends，consumer-super**

```java
// src为生产者->extends
public void pushAll(Iterable<? extends E> src) {
    for (E e : src)
        push(e);
}

// dst为消费者->super
public void popAll(Collection<? super E> dst) {
    while (!isEmpty())
        dst.add(pop());
}
```

​    

注：

```java
public static <E> Set<E> union(Set<? extends E> s1,  Set<? extends E> s2) {...}

Set<Integer>  integers =  Set.of(1, 3, 5);
Set<Double>   doubles  =  Set.of(2.0, 4.0, 6.0);
Set<Number>   numbers  =  union(integers, doubles); // java 8之前的版本编译时会报错
```

在 **Java 8** 之前，类型推断规则不够聪明，无法处理先前的代码片段，要求编译器指定返回类型（或目标类型）来推断 `E` 的类型。

解决方法如下：

```java
Set<Number> numbers = Union.<Number>union(integers, doubles);
```

