## 第9章 迭代器与组合模式（Composite Pattern）

​    

将对象组合成树形结构来表现 “整体/部分” 层次结构。让客户以一致的方式处理个别对象以及对象组合。

组合类也跟主体类一样实现接口。如：

```java
public class Flock implements Quackable {
	ArrayList<Quackable> quackers = new ArrayList<Quackable>();
 
	public void add(Quackable quacker) { // 比主体类Duck多了处理集合的方法
		quackers.add(quacker);
	}
 
    @Override
	public void quack() {
		Iterator<Quackable> iterator = quackers.iterator();
		while (iterator.hasNext()) {
			Quackable quacker = iterator.next();
			quacker.quack();
		}
	}
 
    @Override
	public String toString() {
		return "Flock of Quackers";
	}
}
```

