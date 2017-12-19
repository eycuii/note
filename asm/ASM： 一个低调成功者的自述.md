ASM： 一个低调成功者的自述：https://mp.weixin.qq.com/s/ax288tkY1YIClmEl5Dg1Hg

​    

# ASM： 一个低调成功者的自述

我就是那个不太知名的ASM， 你可能听说过我，也可能完全不知道我。

但是你要是认为我无足轻重，那就大错特错了。

没有我， 你经常使用的Spring , hibernate 将会失去最核心的功能。

没有我， Jython , JRuby 根本就移植不到Java 虚拟机中来。

没有我， Clojure, Groovy这些时髦的语言也没法在Java 虚拟机中运行。

一句话来概括我的能力： 我可以动态的修改已经编译过的class , 还可以动态的生成新的java class,  注意我说的**动态**这个词， 那可以是完全在运行时， 在内存中完成的， 这是一件非常厉害的本事。

既然我是为了动态的修改class 文件而生， 为什么叫做ASM呢？

其实我的创造者在创造我的时候， 为了起名也是大费周章，后来他突然想到C语言中的__asm__ 这个关键字， 可以允许你们在C语言中写点汇编，  他就把ASM这个关键字挪用了。

考虑到起名字确实是一件非常折磨人的事情， 我也就忍了， 就叫这个ASM吧， 至少能体现出我位于系统的最底层， 不， 应该说是最基础的层次。

我听到下面有人问了， 不就是动态生成类吗？ 我完全可以像jsp那样， 使用JavaComplier接口在运行时动态的编译一个java 源代码， 这不也是动态生成class吗？

其实不一样， 你那是生成新的类， 能对现有的class 进行修改吗？

又有人发话了， 你为什么要动态的在运行时来修改类啊？   我为什么不能在编译以前就把类的功能都写好啊？

当然，你要是能把所有的功能都写好，那自然不错，但是人生之不如意，十之八九啊。

举一个最最极端的例子，你从别人那里获得了一个没有源代码的jar 文件， 你想对其中的一个class 进行增强，肿么办？   你可能说： 我可以反编译啊..  算了吧， 反编译的代码能看吗？

还有你们经常挂在嘴边的AOP， 在配置文件中声明一些功能例如事务支持， 然后要这些功能动态的织入到业务代码中，肿么办？    有人说我可以用Java 动态代理啊，  是， 你可以用，但是人家要是没有接口不还是得瞪眼干着急？

顺便吐个槽，你们AOP的那些术语实在是太烂， 什么PointCut, 什么Advice, 除了把人搞晕，还有什么用处？

对了， 还有那个Hibernate , 难道你不知道那个所谓的实体类是被我给增强过的？ 你使用的并不是你看到的， 懂了么？

那到底是怎么实现的动态修改类的？  其实很简单， 去下载一个Java 虚拟机规范， 花上半年时间， 把每个字都搞懂了， 最后像黑客帝国中的Neo那样， 看到的整个世界都是二进制流， 你自然明白我是怎么做的了。

![img](http://mmbiz.qpic.cn/mmbiz_jpg/KyXfCrME6UKgpzwebH1fdQmuOEdbEWRH64R8bxm8FnzfF4wM6Csgqa4bdicpYz4uiajJHjzMXkqpYYxOticVX7gjw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1)

我给你简单的说一下： 我的核心呢主要是三个类， ClassReader, ClassWriter , ClassVisitor, 你用这三个家伙就可以去解析一个class 字节码， 获得字段了，方法了等信息， 当然最重要的可以对这些信息进行修改， 最终形成一个代表新class 的字节码数组，  剩下的事估计你就知道怎么做了， 其实也不属于我ASM了。

还不知道？  很简单嘛， 就是用个ClassLoader 把这个字节码数组Load到虚拟机中， 然后通过反射一调用，不就完了？

对了， 我的创造者使用了Visitor模式来设计这个API, 说实在的，设计的还真不错。 细节太多 ，我在这里就不罗嗦了， 感兴趣的同学可以看看   (http://download.forge.objectweb.org/asm/asm4-guide.pdf  ) ， 绝对物超所值， 当然你的英文要好。

说了这么多，还是上一点代码吧， 让你对我有个直观的认识：

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UKgpzwebH1fdQmuOEdbEWRHrY3jFy8qFg0hq6A7xkz5iaIM0wPicdhBh0micsKvbM6Vt6pzNRmMqIPwA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

我估计你也看不明白， 其实就是hello world了：

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UKgpzwebH1fdQmuOEdbEWRHs5F5wxmOgTqgvhf86FA5R14l9ZwibOegxGVOwVRK16ibgIvdEYhiclB9w/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

你可能心里在想， word哥， 想使用你ASM需要非常透彻的理解Java 虚拟机指令和Java虚拟机内部结构才能使用啊。

没错， 我刚刚说过， 需要读懂Java 虚拟机规范 ，  我负责处理的是最基础的东西 ， 很多码农并不会直接使用我来编程。

有个叫CGLib的家伙在我的基础之上做了不错的封装， 他做了一些高级的API抽象， 让普通的程序员也能够比较简单的对一个现成的类的行为进行修改。  由于更容易使用， CGLib的用户反而更多， 像Spring , Hibernate 。。。  我的风头完全被盖过了，   我想这就是我为什么这么低调、默默无闻的原因。

但是我初心不改 ，坚持对最底层的字节码进行操作， 我持续优化， 让自己变的又小又快， 为别的软件提供最有力的支持。

一开始我就说了Clojure对吧， 这是一个函数式编程的语言， 是解释执行的，没有编译过程， 那他凭什么能运行在Java 虚拟机上？ 不就是利用我ASM来动态的生成字节码 吗？  Jython, JRuby, Groovy 也是大同小异。

最近Java 帝国给我颁发了一个特殊贡献勋章， 奖励我对繁荣Java 虚拟机市场做出的重大贡献， 不谦虚的说， 这绝对是名副其实， 原来JVM中只有一门语言，那就是Java ,   你看看现在语言多的都选不多来了。

好， 今天就说到这里吧， 下次再会。 