# 58. for-each循环优先于传统的for循环

​    

for-each隐藏了迭代器。可以遍历数组、集合，以及任何实现了Iterator接口的对象。

```java
public interface Iterable<E> {
	// Returns an iterator over the elements in this iterable
	Iterator<E> iterator();
}
```

优势：不需要重复计算长度；代码简洁，可以避免不必要的错误。

​    

http://mp.weixin.qq.com/s/JZU6MqcTUCJfju_niLoy8w

但注意：for-each中最好不要做**删除**元素等操作，因为根据java的fail-fast机制，for-each循环时会检查长度是否有变化，如果有，则会抛ConcurrentModificationException异常。

> 迭代器（Iterator）是工作在一个独立的线程中，并且拥有一个 mutex 锁。 迭代器被创建之后会建立一个指向原来对象的单链索引表，当原来的对象数量发生变化时，这个索引表的内容不会同步改变，所以当索引指针往后移动的时候就找不到要迭代的对象，所以按照 fail-fast 原则 迭代器会马上抛出`java.util.ConcurrentModificationException` 异常。

> fail-fast 机制是java集合(Collection)中的一种错误机制。当多个线程对同一个集合的内容进行操作时，就可能会产生fail-fast事件。
> 　　例如：当某一个线程A通过iterator去遍历某集合的过程中，若该集合的内容被其他线程所改变了；那么线程A访问集合时，就会抛出ConcurrentModificationException异常，产生fail-fast事件。
> 要了解fail-fast机制，我们首先要对ConcurrentModificationException 异常有所了解。当方法检测到对象的并发修改，但不允许这种修改时就抛出该异常。同时需要注意的是，该异常不会始终指出对象已经由不同线程并发修改，如果单线程违反了规则，同样也有可能会抛出改异常。