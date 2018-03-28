# 54. 返回零长度的数组或者集合，而不是null

​    

```java
public List<Cheese> getCheeses() {
	return cheesesInStock.isEmpty() ? null
		: new ArrayList<>(cheesesInStock);
}
```

返回null，会让客户端额外判断是否为null：

```java
List<Cheese> cheeses = shop.getCheeses();
if (cheeses != null && cheeses.contains(Cheese.STILTON))
	System.out.println("Jolly good, just the thing.");
```

正确的做法：

```java
public List<Cheese> getCheeses() {
	return new ArrayList<>(cheesesInStock);
}
```

或者使用 Collections.emptyXXX 方法每次返回**同一个不可变**的空集合（List、Map等）：

```java
// Optimization - avoids allocating empty collections
public List<Cheese> getCheeses() {
	return cheesesInStock.isEmpty() ? Collections.emptyList()
		: new ArrayList<>(cheesesInStock);
}

// 数组时：
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];
public Cheese[] getCheeses() {
	return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```

​    

有些人会认为，返回零长度的数组或集合，需要额外开销。但实际上，这种程度的开销可以是忽略的，除非检查时开销大的原因确实是由这个而引起的。