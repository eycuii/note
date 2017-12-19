机房夜话：https://mp.weixin.qq.com/s/7bvUXxYtGqC0YQj8JvcCNQ

​    

登录验证由一个认证中心系统处理。

可以直接看第三夜部分的图。

​    

# 机房夜话

这家集团公司财大气粗，竟然自己建了一个数据中心， 放了数百台机器， 部署了几十个企业内部系统。

在无尘、恒温、恒湿的环境里，这些信息系统的日子过得非常惬意。

他们只需要在白天应对人类的HTTP 请求，及时做出响应， 只要人类一下班，  系统的负载就陡然下降，CPU内存全部都空闲下来。  大家闲来无事， 热热闹闹的机房夜话就开始了。

​    

## 1 第一夜

休假系统是用世界上最好的语言PHP做的，他向来消息灵通，今天带来了一个特别新闻：“ 号外号外， 听说了吗， 人类要搞SSO了。”

Python 写的报销系统，  C#写的车辆管理系统早就看不惯PHP这种中英混杂的风格了： “ 别拽了，说中文！”

PHP休假系统很不屑： “就是单点登录嘛，难道你们没听说过？”

C# 说： “不就是登录嘛！ 人类不是天天登录系统？  你看他们想调度车俩的时候，就得登录我的系统， 输入用户名和密码， 我做验证， 验证通过就建立session,  然后把session id 通过cookie发送给人类的浏览器， 下次人类再访问我的URL的时候， cookie就会发过来， 我就知道他已经登录过了。 “

C#很得意，向大家炫耀着登录的原理。

“对了，告诉你们一个小秘密， 人类这些密码太简单了，不是123456, 就是abcd。 ”

Python附和道： “ 是啊，人类太懒了， 密码超级简单，听说上一次有个家伙用领导账号成功地登录了系统， 于是全集团人员的工资都暴露了！ ”

“那对于同一个人， 你这里的用户名/密码和小C#那里会一样吗？”  PHP说

“这个。。。 很有可能不一样。”

PHP说： “对啊，这就是问题了， 这么多不同的用户名，有的是邮箱地址，有的是手机号，有的是用户名， 我们这里几十个系统，搁谁都记不全啊!  这就是他们为什么要搞单点登录： 在一个地方登录一次， 就可以访问我们这里所有的系统了”

C# 叫道: " 只需要登录一次？ 听起来很美好啊 ！  让我想想怎么实现， 对了， 登录就是cookie，那我们把cookie共享起来不就可以了？  人类在报销系统那里登录后，再访问我这个车辆管理系统， 把cookie发过来不就行了？"

众人纷纷表示赞同。

PHP 心里再次鄙视了一下C# , 说： “NO , NO ,  cookie是不能跨域的，  a.com 产生的cookie , 浏览器是不会发到b.com去的。 ”

有人在悄悄地google ,  PHP果然说得不错。

大家赶紧检查了下自己的域名，有的叫 xxx.vaction.com,  有的叫xxx.hr.com ,  看来共享cookie方案不管用。

PHP补充到： “也许人类能把我们统一到一级或二级域名下， 比如 xxx.company.com,   这样cookie可以共享了！ 但是我们后端没有session 也不行啊， 你cookie发过来，我内存中没数据，根本不知道你是否登录过， 怎么验证？”

C#说： “session 也可以共享哦 ， 你看我这个系统有两个服务器，共享的是redis中的session， 将来我们这几十个系统都共享同一个redis，想想都让人激动啊！”

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2Ec0dFunnkBusVbYddxXECP044BO8uSUnTfjJevrBWo8TrJicHw4GJmNZQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

Python 说： “ 这么多系统， 架构不同，语言也不同， 共享session太麻烦了吧？ ”

C# 发愁地说： “那怎么办？ ”

这时候旁边传来了一声大吼： “你们在那里吵吵什么，老子在生成报表，都没法专心干活了！”

这是脾气暴躁的COBOL在抱怨了， 千万不要惹这个老家伙，于是大家纷纷噤声， 老老实实地睡觉去了。

​    

## 2 第二夜

第二天晚上，COBOL程序终于歇着了， 大家继续讨论。

Python提了一个新点子：“要我说，我们别共享session了， 我们就用cookie,  用户在我这个报销系统这里登录了， 我就在cookie中写个token ,  用户访问别的系统，就可以把token 带过去， 那个系统验证一下token ，如果没问题，就认为它已经登录了。”

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2Ec8G8Cx7A7OGMADl9hvViaeleXcuRUzXDmax5vBb04JibAqVB5N49pgo6w/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

“那token 得加密吧， 要不然谁都可以伪造”   C#安全意识挺强

“那是自然， 听说过json web token 没有？ 我们每个系统在生成token 的时候，都要对数据做个签名，防止别人篡改， 下面就是我生产的token ,  其中有header 信息和userID，  你看我用Hash算法和密钥生产了一个签名。 这个签名啊也是token数据的一部分， 到时候也会发到你的系统去”  Python 说

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2Ec4GJ0ibRllv2BG9XPAoLthbwwPPsj4CZamJsBAqLJnssykpjmmRVnEiag/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

（计算签名的过程）

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2Ecf7PBSQuCEE4p5OicFEoI6HjhbzCXryIich1eiblCq5yxBmCHlI3WacBBw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

（放置到cookie中的token）

C#说： “明白了， 我收到了token ，就用同样的算法再计算签名，然后和你计算的相比， 如果相等，证明他登录过，我可以直接取出userID使用了，  如果不相等， 说明有人篡改， 我就关门放狗， 把他暴揍一顿。”

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2EcyonicCB7w4Tbso6Pq3MCgs1MW7F2n1660Vsg4oIxQaLWYftrUWOQ2cQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

Ruby  插嘴说： “这个办法不错，轻量级，我喜欢！  只是这个算法和密钥大家都得一致才行。 密钥的分发也是个问题。”

PHP听了半天， 发现了一个漏洞：“你这个token中放了一个userID, 可是我们每个系统的userID都不一样啊， 你的userID 我拿过来没有任何用处， 怎么办？”

这的确是个致命的问题！每个系统中都有一套自己独特的user id ,互不共享，这样以来之前讨论的什么共享session, 共享cookie ,都很难实现了 !

一阵沉默， 看来没救了。

夜已深，大家讨论得有点累了，纷纷睡去。

​    

## 3 第三夜

第三天，机房夜话继续， 但还是没有解决方案。气氛有点小尴尬。

老成持重的Java咳嗽了几声，示意要发言了。

“你们知道吗， 我们是一个企业内部系统，人类搞SSO就是想消除这多个账号的问题，将来每个系统都不需要维护自己的用户系统， 他们会建立一个统一的认证中心，所有的用户注册和认证都在那里做。”

“这么做行得通吗，认证中心怎么通知我们说用户已经认证了？”  C#问道

“这个过程稍微有点复杂” ， Java 对C# 说， “举个例子来解释下， 比如用户通过浏览器先访问你这个系统www.a.com/pageA ， 这个pageA是个需要登录才能访问的页面，你发现用户没有登录， 这时候你需要做一件额外的操作，就是重定向到认证中心，www.sso.com/login?redirect=www.a.com/pageA”

C#说： “为什么后面要跟一个redirect的url呢？ 奥，明白了， 将来认证通过后，还要重定向到我这里来。”

“没错， 浏览器会用这个www.sso.com/login?redirect=www.a.com/pageA 去访问认证中心， 认证中心一看， 没登录过， 认证中心就让用户去登录， 登录成功以后， 认证中心要做几件重要的事情 ：

1. 建立一个session。
2. 创建一个ticket （可以认为是个随机字符串）
3. 然后再重定向到你那里， url 中带着ticket : www.a.com/pageA?ticket=T123   与此同时cookie也会发给浏览器，比如：Set cookie : ssoid=1234, sso.com ”

“可是这个cookie对我一点用处都没有啊，跨域不能访问啊。”

“人家网站sso.com的cookie对你肯定没用了， 浏览器会保存下来。 但是注意那个ticket ”  Java 提醒道， “这个东西是个重要的标识，你拿到以后需要再次向认证中心做验证。”

“明白，是为了防止不坏好意的人伪造”

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2EcxS5LHqbANxqI54uj7YRWXmjgPCg1A3jTBdJjP8aHZjMelKcPDNSghg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

（点击看大图哦）

“你拿着token ,去问下认证中心， 这是您发的token 吗， 认证中心说没错，是我发的，那你就可以认为用户在认证中心登录过了”

“那我该干什么事情呢？ ”

“浏览器向你发出的请求不是www.a.com/pageA?ticket=T123 吗， 这时候你既然认为用户已经登录过了，那就给他建立session, 返回pageA这个资源啊”

“嗯， 我还需要给浏览器发一个cookie, 对吧， 这是属于我的cookie : Set cookie : sessionid=xxxx, a.com ”  C# 说道

"孺子可教， 注意，这时候浏览器实际上有两个cookie,一个是你发的，另外一个是认证中心发的。"

“如果用户再次访问我另外一个受保护的页面，www.a.com/pageA1， 该怎么办？ 难道还要去认证中心登录 ”  C#继续问

“那当然不用了，你给浏览器发过你自己的cookie , 到时候浏览器自然会带过来，你就知道它登录过了。 ”

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2EcJqlgLvkhuN6CAahUDaFFlVGajd06YbGX0nrx4O9HtMA2jc54hWzTuA/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

（点击看大图哦）

“原来如此， 好麻烦啊”  C#感慨道。

Python 插了一句： “如果用户访问C#的系统(www.a.com/pageA)时已经通过认证中心登录了， 然后再访问我www.b.com/pageB, 会发生什么状况呢？”

Java 说：“很简单， 和访问www.a.com/pageA非常类似，唯一的不同就是不需要用户登录了，因为浏览器已经有了认证中心的cookie， 直接发给**www.sso.com就可以了**”

说着， Java 又画了两张图。

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2Ec1eBgZwfUh5yiaRcoSopJFh8WxUqQHyHt9QyQvSpQTmOjf4lyQCls4EQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

（点击看大图哦）

同样，认证中心会返回token , www.b.com 需要做验证

![img](http://mmbiz.qpic.cn/mmbiz_png/KyXfCrME6UL2VCwCTxnxI6Gf59OrQ2EcribtTuT9X8f4rPzCOhJeUmMibd4jat94ujtI1kuRsnaHnGibtSX19L5gg/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1)

（点击看大图哦）

PHP 一直在努力的听，他说： “其实本质上就一个认证中心的cookie ,加上多个子系统的cookie 而已！”

Java 撇了一眼PHP ： “总结的很精辟！”

C#发现了一点新东西： “在认证中心，为什么要去做一个系统注册的操作呢？， 我看到注册了系统A,还有系统B”

"SSO 是单点登录，是不是还要有**单点退出**啊， 用户在一个系统退出了，认证中心需要把自己的会话和cookie干掉，然后还要去通知各个系统， 让他们把自己的会话统统干掉，这样才能在所有的系统都实现真正地退出啊。" Java回答。

大家琢磨了一会儿，很快就喧嚣起来：

“太麻烦了”

“我们的代码还得改动不少呢”

“重定向太多了， 把我都搞晕了”

“我觉得人类不会这么搞！”

......

Java 说“别小看它， 这个点子是耶鲁大学提出的，叫做CAS(Central Authentication Server ) ,  是一个很著名的SSO解决方案， 弄不好人类就会采用。  你们呐，还是好好学学吧。”