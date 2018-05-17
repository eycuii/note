## 第11章 代理模式（Proxy Pattern）

​    

https://blog.csdn.net/goskalrie/article/details/52458773

JDK 动态代理：

```java
//1. 抽象主题  
public interface Moveable {  
    void move()  throws Exception;  
}  
//2. 真实主题  
public class Car implements Moveable {  
    public void move() throws Exception {  
        Thread.sleep(new Random().nextInt(1000));  
        System.out.println("汽车行驶中…");  
    }  
}  
//3.事务处理器  
public class TimeHandler implements InvocationHandler {  
    private Object target;  
     
    public TimeHandler(Object target) {  
        super();  
        this.target = target;  
    }  
   
    /** 
     * 参数： 
     *proxy 被代理的对象 
     *method 被代理对象的方法 
     *args 方法的参数 
     * 返回： 
     *Object 方法返回值 
     */  
    public Object invoke(Object proxy, Method method, Object[] args)  
            throws Throwable {  
        long startTime = System.currentTimeMillis();  
        System.out.println("汽车开始行驶…");  
        method.invoke(target, args);  
        long stopTime = System.currentTimeMillis();  
        System.out.println("汽车结束行驶…汽车行驶时间：" + (stopTime - startTime) + "毫秒！");  
        return null;  
    }  
   
}  
//测试类  
public class Test {  
    public static void main(String[] args) throws Exception{  
        Car car = new Car();  
        InvocationHandler h = new TimeHandler(car);  
        Class<?> cls = car.getClass();  
        /** 
         *loader 类加载器 
         *interfaces 实现接口 
         *h InvocationHandler 
         */  
        Moveable m = (Moveable) Proxy.newProxyInstance(cls.getClassLoader(),cls.getInterfaces(), h);  
        m.move();  
    }  
}  
```

Java帝国之动态代理：https://mp.weixin.qq.com/s?__biz=MzAxOTc0NzExNg==&mid=2665513926&idx=1&sn=1c43c5557ba18fed34f3d68bfed6b8bd&chksm=80d67b85b7a1f2930ede2803d6b08925474090f4127eefbb267e647dff11793d380e09f222a8#rd

​    

cglib 动态代理

动态代理jdk和cglib的区别：https://www.cnblogs.com/3chi/p/6911889.html

​    

**区别**

jdk、cglib 都会生成代理类来实现动态代理，其中 jdk 是实现 target 的接口的方式，而 cglib 是继承 target 的方式。

所以 jdk 方式需要 target 实现某个接口，cglib 方式时 target 类不能是 final 的（否则不能继承）。