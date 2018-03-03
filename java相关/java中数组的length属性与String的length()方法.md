# java中数组的 length 属性与 String 的 length() 方法



因为数组长度不可变，所以长度可以是 final 变量，即 length。

但 String 其内部已经有了 final char value[]，获取字符串长度只需要返回 value.length 即可，不必再额外定义一个 final 属性。

所以，java 中数组有 length 属性，没有 length() 方法，而 String 类没有 length 属性，有 length() 方法。





------

深入分析Java中的length和length() https://mp.weixin.qq.com/s/_O9llQcodBqngbZxrJj9cA