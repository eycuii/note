# 第18章 Java I/O 系统

​    

InputStream，OuputStream：面向字节形式的 I/O

Reader，Writer：面向字符的 I/O；兼容 Unicode



#### 缓冲输入文件

```java
public static String read(String filename) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(filename));
    String s;
    StringBuilder sb = new StringBuilder();
    while((s = in.readLine()) != null)
      sb.append(s + "\n");
    in.close();
    return sb.toString();
}
```



### 对象序列化

把（有实现 Serializable 接口的）对象转换成一个字节数组（包括 private 数据）。

通过序列化可以实现对象的深度复制，即复制整个对象而不是只复制它的引用。

如果某对象拥有一个其他对象的引用，当把它序列化后再次反序列化，会发现它拥有的那个其他对象并不会是在原来那个内存地址上（即可能不再是同一个对象的引用）。

如果两个对象都有对第三个对象的引用，把这两个对象由同一个流进行序列化后再反序列化时，它俩拥有的引用所指向的对象是相同的（内存地址一样）。

#### transient 瞬时关键字

transient 数据不会被序列化。所以经过反序列化恢复对象后，会发现 transient 数据会变为空的。

#### 实现 readObject() 与 writeObject() 方法

ObjectOuputStream 调用 `writeObject()` 方法时，会先检查该对象是否有 `writeObject()` 方法（不是重写或覆盖），如果有则调用该对象的方法。`readObject()` 也一样。

```java
public class SerialCtl implements Serializable {
    private String a;
    private transient String b;
    public SerialCtl(String aa, String bb) {
        a = "Not Transient: " + aa;
        b = "Transient: " + bb;
    }
    public String toString() { return a + "\n" + b; }
    private void writeObject(ObjectOutputStream stream) throws Exception {
        stream.defaultWriteObject(); // 默认的 writeObject() 方法
        stream.writeObject(b);
    }
    private void readObject(ObjectInputStream stream) throws Exception {
        stream.defaultReadObject(); // 默认的 readObject() 方法
        b = (String)stream.readObject();
    }
    public static void main(String[] args) throws Exception {
        SerialCtl sc = new SerialCtl("Test1", "Test2");
        System.out.println("Before:\n" + sc);
        ByteArrayOutputStream buf= new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(buf);
        o.writeObject(sc);
        // Now get it back:
        ObjectInputStream in = new ObjectInputStream(
            new ByteArrayInputStream(buf.toByteArray()));
        SerialCtl sc2 = (SerialCtl)in.readObject();
        System.out.println("After:\n" + sc2);
    }
} /* Output:
Before:
Not Transient: Test1
Transient: Test2
After:
Not Transient: Test1
Transient: Test2
*/
```

