## 第3章 装饰者模式（Decorator Pattern）

​    

开闭原则：类对扩展开发，对修改关闭。

​    

比如一家咖啡店，咖啡为一个主体，咖啡的调料为“装饰者”，比如摩卡、奶泡等。

**优点**：可以避免对每一种咖啡都新建一个子类。或者避免在 cost() 方法里增加各种判断（如判断是否有摩卡、奶泡等调料）。

​    

咖啡类：

```java
public abstract class Coffee {
    String description = "Unknown Beverage";
  
	public String getDescription() {
		return description;
	}
    
	public abstract double cost();
}
```

具体咖啡子类：

```java
public class Espresso extends Coffee {
    
    public Espresso() {
		description = "Espresso";
	}
    
	public double cost() {
		return 19.99;
	}
}
```

调料（装饰者）类：

```java
public abstract class Decorator extends Coffee {
	public abstract String getDescription();
}
```

摩卡类（具体装饰者子类）：

```java
public class Mocha extends Decorator {
	Coffee coffee;
 
	public Mocha(Coffee coffee) {
		this.coffee = coffee;
	}
 
	public String getDescription() {
		return coffee.getDescription() + " with Mocha";
	}
 
	public double cost() {
		return .20 + coffee.cost();
	}
}
```

用户代码：

```java
public static void main(String args[]) {
    Coffee coffee = new Espresso();
    System.out.println(coffee.getDescription() + "：" + beverage.cost());

    Coffee coffee2 = new Espresso();
    // 用装饰者一层一层包装起来
    coffee2 = new Mocha(coffee2);
    coffee2 = new Whip(coffee2); // 另一个调料类
    System.out.println(coffee2.getDescription() + "：" + beverage2.cost());
}
```

**缺点**：类型问题。

​    

### Java 中的装饰者（Java I/O）

比如 InputStream -> FileInputStream -> BufferedInputStream -> LineNumberInputStream。