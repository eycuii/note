# MySQL 切换目录

之前使用 yum 安装了 mysql，其安装默认路径是在 `/var/lib/mysql`。发现 `/var` 空间不够，需要把 `datadir` 改成 `/home` 下。

参考：https://www.jb51.net/article/97179.htm

​    

## 版本

MySQL ：5.7.22

CentOS：6.5

​    

## 步骤

1. 关闭 mysql

   ```shell
   service mysqld stop
   ```

2. 转移数据。把原来的 `/var/lib/mysql` 复制到 `/home/mysql_data/` 。

   ```shell
   
   ```

   > 注意 -a 这个参数一定要带着，否则复制过去的权限就不对了。如果数据库比较大的话，时间会比较长，可能会超时。

3. 修改配置文件 my.cnf 的 `datadir` 和 `socket` 。

   ```shell
   vi /etc/my.cnf
   ```

   打开之后修改datadir的目录为 `/home/mysql_data/mysql`，把 socket 改成 `/home/mysql_data/mysql/mysql.sock` 。

4. 修改 `/etc/rc.d/init.d/mysqld` 的 datadir。

   ```shell
   vi /etc/rc.d/init.d/mysqld
   ```

   修改 datadir 的配置。

5. 修改 `/usr/bin/mysqld_safe` 的 datadir。

   ```shell
   vi /usr/bin/mysqld_safe
   ```

   修改 datadir 的配置。

6. 建立一个 `mysql.sock` 的链接。

   ```shell
   ln -s /home/mysql_data/mysql/mysql.sock /var/lib/mysql/mysql.sock
   ```

7. ```shell
   
   ```

   如果报 /etc/init.d/mysqld 没有权限：

   ```shell
   chmod a+wrx /etc/init.d/mysqld
   ```

​    

**启动失败，没反应的问题**

我在改 `/etc/rc.d/init.d/mysqld` 和 `/usr/bin/mysqld_safe` 时，是直接在本地改文件后上传到 Linux 上的，发现 `service mysqld start` 启动时没反应，查看 mysql 状态也没启动起来。后来尝试用 `mysqld_safe &` 启动 mysql 时，看到报 /usr/bin/mysqld_safe 权限不够后，给该文件加了命令，再启动，就成功了。执行的命令：

```shell
chmod a+wrx /usr/bin/mysqld_safe
```

