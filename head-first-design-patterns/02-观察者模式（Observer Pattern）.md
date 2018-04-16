## 第2章 观察者模式（Observer Pattern）

​    

如出版者（报社）、订阅者中，出版者管理的数据有发生变化时，订阅者可以检测到。在观察者模式里，这种出版者称主题（Subject），订阅者称观察者（Observer）。

可通过订阅/注册操作，成为主题对象的观察者；也可通过取消订阅而不再观察主题对象。

​    

主题 Subject 接口：

```java
public interface Subject { // 任何Subject的实现类都可以成为被监听的对象
	public void registerObserver(Observer o); // 订阅
	public void removeObserver(Observer o); // 取消订阅
	public void notifyObservers(); // Subject实现类里需要维护观察者list
}
```

观察者 Observer 接口：

```java
public interface Observer {
    // 定义一个方法，使主题对象调用该方法来通知各观察者
	public void update(float temp, float humidity, float pressure); 
}
```

Observer 实现类也可以拥有 Subject 对象，这样在取消订阅时就会方便。（构造方法里传主题对象）

​    

### Java 内置的观察者模式

java.util 包下的 Observer 接口和 Observable 类。