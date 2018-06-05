# 第9章 基于HTTP的功能追加协议

​    

## SPDY

​    

## 使用浏览器进行全双工通信的 WebSocket

一旦确立 WebSocket 通信连接，不论服务器还是客户端，任意一方 都可直接向对方发送报文。

建立 TCP 连接后，用 HTTP 进行握手（通知服务器通信协议改为 WebSocket）后会切换成 WebSocket 协议。

​    

## 期盼已久的 HTTP/2.0

多路复用

对 header 进行压缩

不是长连接。

​    

https://www.cnblogs.com/Catherine001/p/8359153.html

与 HTTP/1.1：

HTTP/1.1 串行化单线程处理，可以同时在同一个tcp链接上发送多个请求，但是只有响应是有顺序的，只有上一个请求完成后，下一个才能响应。一旦有任务处理超时等，后续任务只能被阻塞(线头阻塞) 

关于长连接：

HTTP/1.1 默认进行持久连接，但每个请求仍然要单独发 header。它的长连接其实是一种“伪链接”，有一个保持时间。

websocket 的长连接，是一个真的全双工。

​    

https://www.jianshu.com/p/b68d2b26f5f4