# MySQL 学习笔记



## 版本

MySQL ：5.7.19

MySQL 官网目前已经有了 8.0.3 ，但还是 RC ，没有 GA 版本，所以这里使用 5.7.19 的。

​    

## 下载与安装

### Windows

1. 下载

   https://dev.mysql.com/downloads/mysql/

2. 解压文件

3. 进入根目录，创建 my.ini 文件，内容如下：

   ```ini
   [client]
   port=3306
   default-character-set=utf8 
   [mysqld] 
   basedir=D:\mysql-5.7.19-winx64
   datadir=D:\mysql-5.7.19-winx64\data
   port=3306
   character-set-server=utf8
   character-set-filesystem = utf8
   sql_mode=NO_ENGINE_SUBSTITUTION,NO_AUTO_CREATE_USER
   explicit_defaults_for_timestamp=true
   skip-grant-tables
   ```

4. 创建一个空文件夹，文件夹名为 data ：

   ![mysql-安装](../img/mysql-安装.png)

5. 以管理员身份打开 cmd ，进入 bin 目录

6. 初始化

   ```shell
   mysqld --initialize-insecure
   ```

   可以选择用 --initialize-insecure 或者 --initialize 来初始化，--initialize-insecure 初始化 root 密码为空，如果用 --initialize 来初始化，会生成一个随机密码，该随机密码可以在 data 目录下的 err 文件中查看。

7. 安装 MySQL 服务

   ```shell
   mysqld -install
   ```

8. 启动 MySQL 服务

   ```shell
   net start mysql
   ```

9. 登录 MySQL

   ```shell
   mysql -u root
   ```

10. 退出

  ```shell
  exit
  ```





### Linux（Ubuntu）

1. 下载

   ```shell
   wget http://dev.mysql.com/get/Downloads/MySQL-5.7/mysql-5.7.19-linux-glibc2.12-x86_64.tar.gz
   ```

2. 解压

   ```shell
   tar zxvf mysql-5.7.19-linux-glibc2.12-x86_64.tar.gz
   sudo mv mysql-5.7.19-linux-glibc2.12-x86_64 /usr/local
   sudo ln -s /usr/local/mysql-5.7.19-linux-glibc2.12-x86_64/ /usr/local/mysql
   ```

3. 安装

   ```shell
   #MySQL运行需要libaio1库
   sudo apt-get install libaio1
   #添加用户组
   sudo groupadd mysql
   #添加用户，这个用户是不能登录的
   sudo useradd -r -g mysql -s /bin/false mysql
   #进入文件目录，mysql是链接
   cd /usr/local/mysql
   #新建文件夹
   sudo mkdir mysql-files
   #修改文件夹的权限
   sudo chmod 750 mysql-files
   sudo chown -R mysql .
   sudo chgrp -R mysql .
   #安装初始化，注意：此部最后一行会有一个初始化密码，用于root账号的首次登录
   sudo bin/mysqld --initialize --user=mysql 
   #生成证书
   sudo bin/mysql_ssl_rsa_setup        
   #把权限修改回来      
   sudo chown -R root .
   sudo chown -R mysql data mysql-files

   sudo /usr/local/mysql/bin/mysqld --no-defaults \
   --initialize \
   --basedir=/usr/local/mysql \
   --datadir=/usr/local/mysql3308/data \
   --user=mysql \
   --explicit_defaults_for_timestamp

   sudo /usr/local/mysql/bin/mysqld_safe --defaults-file=/usr/local/mysql3307/data/my.cnf >/dev/null 2>&1 &
   /usr/local/mysql/bin/mysql -uroot -p –socket=/opt/mysqldata3308/mysql.sock

   o/!SqyIao8z&
   )gkzlCIH2HE9

   /usr/local/mysql/bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data1 --explicit_defaults_for_timestamp
   +7AvlXwZ/4Wu
   jTHX>yDfd8fI
   7UcqF*o>.9Tq
   v*izyUkfI6+/

   sudo apt-get install sysv-rc-conf
   sudo sysv-rc-conf --list mysqld_multi
   export PATH=/usr/local/mysql/bin:$PATH
   ufw disable #关闭Ubuntu防火墙
   sudo chmod 777 -R /data/mysql_data{1..4}
   /usr/local/mysql/bin/mysql -S /tmp/mysql.sock1  -uroot -p'+7AvlXwZ/4Wu'

   SET PASSWORD = PASSWORD('123123');
   grant replication slave, replication client on *.* to repl@'192.168.42.%' identified by 'repl';
   f!I3sWr>iaU/
   (U/g4<c+&7ni
   J1ObfhG:Fat*
   ?wLsd7aYYnkf
   change master to master_host='192.168.42.128', MASTER_PORT=3306, master_user='repl', master_password='repl', master_log_file='mysql-bin.000001', master_log_pos=0;
   ```

4. 启动

   ```shell
   sudo bin/mysqld_safe --user=mysql &
   ```

5. 登录 MySQL

   ```
   /usr/local/mysql/bin/mysql -uroot -p
   ```

   需要输入密码，该密码在初始化时输出的最后一行中有显示，如下，可以看到密码是 rwqYv8bdgG)>

   ```shell
   [Note] A temporary password is generated for root@localhost: rwqYv8bdgG)>
   ```



### 一台 Linux 部署多个 MySQL （Ubuntu）

与上面安装单个 MySQL 时类似，但需要手动配置各端口等参数。

1. 关闭防火墙

   ```shell
   ufw disable 
   ```

2. 下载、解压、创建用户组

3. 在 MySQL 二进制包目录中创建 mysql-files 目录（MySQL 数据导入/导出数据专放目录）

   ```shell
   mkdir -v /usr/local/mysql/mysql-files
   ```

4. 创建多实例数据目录

   ```shell
   mkdir -vp /data/mysql_data{1..4}
   ```

5. 修改 MySQL 二进制包目录的所属用户与所属组

   ```shell
   chown root.mysql -R /usr/local/mysql-5.7.19-linux-glibc2.12-x86_64
   ```

6. 修改 MySQL 多实例数据目录与 数据导入/导出专放目录的所属用户与所属组

   ```shell
   chown mysql.mysql -R /usr/local/mysql/mysql-files /data/mysql_data{1..4}
   ```

7.  配置 MySQL 配置文件 /etc/my.cnf

   5.7.18 以后不会有默认的配置文件，所以需要自己去创建一个 my.cnf 并放到 /etc 文件夹里。

   my.cnf 内容：

   ```ini
   [mysqld_multi] 
   mysqld = /usr/local/mysql/bin/mysqld  
   mysqladmin = /usr/local/mysql/bin/mysqladmin
   log = /tmp/mysql_multi.log 
     
   [mysqld1] 
   # 设置数据目录　[多实例中一定要不同] 
   datadir = /data/mysql_data1
   # 设置sock存放文件名　[多实例中一定要不同] 
   socket = /tmp/mysql.sock1 
   # 设置监听开放端口　[多实例中一定要不同] 
   port = 3306 
   # 设置运行用户 
   user = mysql 
   # 关闭监控 
   performance_schema = off 
   # 设置innodb 缓存大小 
   innodb_buffer_pool_size = 32M 
   # 设置监听IP地址 
   bind_address = 0.0.0.0 
   # 关闭DNS 反向解析 
   skip-name-resolve = 0 
     
   [mysqld2] 
   datadir = /data/mysql_data2
   socket = /tmp/mysql.sock2 
   port = 3307 
   user = mysql 
   performance_schema = off 
   innodb_buffer_pool_size = 32M 
   bind_address = 0.0.0.0 
   skip-name-resolve = 0 
     
   [mysqld3] 
   datadir = /data/mysql_data3
   socket = /tmp/mysql.sock3 
   port = 3308 
   user = mysql 
   performance_schema = off 
   innodb_buffer_pool_size = 32M 
   bind_address = 0.0.0.0 
   skip-name-resolve = 0 
     
   [mysqld4] 
   datadir = /data/mysql_data4
   socket = /tmp/mysql.sock4 
   port = 3309 
   user = mysql 
   performance_schema = off 
   innodb_buffer_pool_size = 32M 
   bind_address = 0.0.0.0 
   skip-name-resolve = 0
   ```

   MySQL 读取 my.cnf 配置文件的顺序：

   ```shell
   # /usr/local/mysql/bin/mysql --help 中可以看到如下信息：
   Default options are read from the following files in the given order:
   /etc/my.cnf /etc/mysql/my.cnf /usr/local/mysql/etc/my.cnf ~/.my.cnf
   ```

   如果不使用 mysqld_multi 需要在每个实例根目录下创建 my.cnf 。

8. 初始化各个实例

   初始化完成后会自带随机密码在输出日志中。

   ```shell
   /usr/local/mysql/bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data1
   /usr/local/mysql/bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data2
   /usr/local/mysql/bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data3
   /usr/local/mysql/bin/mysqld --initialize --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data4
   ```

   不推荐使用 --initialize-insecure （不使用密码）来初始化，有可能在登录 MySQL 时还会要求输入其密码（直接 enter 掉也不行）。

9. 各实例开启 SSL 连接

   ```shell
   /usr/local/mysql/bin/mysql_ssl_rsa_setup --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data1
   /usr/local/mysql/bin/mysql_ssl_rsa_setup --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data2
   /usr/local/mysql/bin/mysql_ssl_rsa_setup --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data3
   /usr/local/mysql/bin/mysql_ssl_rsa_setup --user=mysql --basedir=/usr/local/mysql --datadir=/data/mysql_data4
   ```

10. 复制多实例脚本到服务管理目录下

    ```shell
    cp /usr/local/mysql/support-files/mysqld_multi.server /etc/init.d/mysqld_multi
    ```

11. 添加脚本执行权限

    ```shell
    chmod +x /etc/init.d/mysqld_multi
    ```

12. 添加进 service 服务管理

    ```shell
    chkconfig --add mysqld_multi
    ```

    上面是网上找的，如果是 Ubuntu ，需要使用 sysv-rc-conf ：

    ```shell
    sudo apt-get install sysv-rc-conf
    sudo sysv-rc-conf --list mysqld_multi
    ```

13. ​

    ```shell
    export PATH=/usr/local/mysql/bin:$PATH
    ```

14. 启动各实例

    ```shell
    /etc/init.d/mysqld_multi start
    ```

15. 查看状态

    ```shell
    /etc/init.d/mysqld_multi report
    ```

    如果发现没有启动，可以查看 /tmp/mysql_multi.log 其原因。如果报 data 文件夹 xxx 的权限不够（具体不记得），可以给 data 文件夹赋权限：

    ```shell
    sudo chmod 777 -R /data/mysql_data{1..4}
    ```

16. 登录一个 MySQL

    ```shell
    /usr/local/mysql/bin/mysql -S /tmp/mysql.sock1 -uroot -p'+7AvlXwZ/4Wu'
    ```







### 存储引擎

MySQL 的索引是在存储引擎中实现的，不同的存储引擎支持的索引类型以及具体实现方式是有差别的。

存储引擎有 MYISAM，Innodb ， Memory 等。其特点如下：

#### InnoDB

行级锁，并发能力相对强，占用空间是 MYISAM 的 2.5 倍，支持事务，5.6 开始支持全文索引。5.5 以后默认存储引擎为 InnoDB 。InnoDB的 AUTOCOMMIT 默认是打开的，即每条SQL语句会默认被封装成一个事务，自动提交，这样会影响速度，所以最好是把多条 SQL 语句显示放在 begin 和 commit 之间，组成一个事务去提交。

#### MyISAM

全表锁，拥有较高的执行速度，一个写请求请阻塞另外相同表格的所有读写请求，并发性能差，占用空间相对较小，不支持事务，5.5 以上支持全文索引。

#### Memory

全表锁，存储在内存当中，速度快，但会占用和数据量成正比的内存空间且数据在 mysql 重启时会丢失。有个缺陷是对于变长字段的存储是定长的，从而影响数据库的内存开销和性能。（听说即将抛弃使用 Memory ）

​    

### 分表

#### 纵向分表

常见方式有根据活跃度分表、根据重要性分表等。

主要解决：

- 表与表之间资源争用问题；
- 锁争用机率小；
- 实现核心与非核心的分级存储，如UDB登陆库拆分成一级二级三级库；
- 数据库同步压力问题。

#### 横向分表

根据某些特定的规则来划分大数据量表，如根据时间分表。

主要解决：

- 单表过大造成的性能问题；
- 单表过大造成的单服务器空间问题。

​    

### 索引

不同的存储引擎，对索引实现的细节有所不同。

MySQL 索引类型：

- 主键索引（PRIMARY）
- 唯一索引（UNIQUE）
- 普通索引（INDEX）
- 全文索引（FULLTEXT ，MYISAM 及 5.6 以上的 Innodb）

主键索引、唯一索引、普通索引都是基于 B-Tree 索引算法实现的。主键索引的字段不允许为 null ，且不能重复。唯一索引允许为 null ，但能重复。普通索引允许 null ，重复。

全文索引适用于海量数据的关键字模糊搜索，但功能还是比专业的搜索引擎少。





EXPLAIN

EXPLAIN 语句可以告诉查询语句有没有使用索引，但部分统计是估算值，分析出来的结果只是一个参考。

EXPLAIN SELECT * FROM T_USER



尽量使用数据类型相同的数据列进行比较。如 INT 不同于 BIGINT 。





集群



​    

## 优化

不要使用 select * （涉及到数据字典解析）

排序尽量使用升序

order by / group by 字段包括在索引当中减少排序，效率会更高

删除表所有记录 truncate 比 delete 快。

在 Innodb上用 select count(*) ，因为 Innodb 会存储统计信息



慎用 oder by rand()

275 5.3

​    

慢查询日志

​    

加锁，死锁

​    

缓存

​    

### 使用索引

1. 索引会加快查询速度，但不是越多越好，因为会增加数据库存储空间，并且插入修改删除数据时需要花额外时间去维护索引（5.5 以下的只能用到一个索引）。
2. 由于索引是根据排序来快速查找的，如果使用索引的字段有很多重复值，会降低其查询的效率，甚至可能不会使用索引来查询。


3. 聚簇索引是构建innodb的基础，如果主键非自增长，随机主键会影响插入速度。

4. 联合索引

   联合索引的效率往往比单列索引的效率高。

   联合索引能够满足最左侧查询需求，例如(a, b, c)三列的联合索引，能够加速a | (a, b) | (a, b, c) 三组查询需求。

   比如，select * from table1 where A=1 order by B asc；和 select * from table1 where A=? and B=? order by C asc；这两种查询只需要建一个联合btree索引（A, B, C）。

   但如果查询 select * from table1 where A=? order by id desc；实际并没有使用A索引，而是使用id索引，如果要优化这类查询，可以通过建A和id的联合索引，删除A单列索引。

   如果表中包含A和B，A和B建立了联合索引。条件A=? and B=?和B=? and A=?都会使用到索引，但是B=? and A=?会经过查询分析器的优化。最好写where条件的顺序和联合索引顺序一致，避免查询分析器优化损耗性能。

5. union all，in，or 会使用索引。

   union all 速度最快，其次 in 。

   不建议频繁用 or ，不是所有的 or 都命中索引。

   in(a,b) 如果满足条件的数据占比非常大，也是有可能不用索引而是全表扫描的。


6. != 负向查询不会命中索引（除非量很小）

7. like %XX% 左边有 % 不能走索引，可以使用 instr() 。

8. 尽量避免在where字句中对字段进行null值的判断。否则将会导致引擎放弃使用索引而进行全表扫描。

9. 尽量使带索引的字段单独出现：

   ```mysql
   SELECT `sname` FROM `stu` WHERE concat(`sname`,'abc') ='Jaskeyabc'; -- 不会使用索引,因为使用了函数运算,原理与上面相同
   SELECT `sname` FROM `stu` WHERE `sname` =concat('Jaskey','abc'); -- 会使用索引
   ```

   在 5.7 中，MySQL 提供了函数索引 Generated Column，可以更好的解决这类问题：

   在MySQL 5.7中，支持两种Generated Column，即Virtual Generated Column 虚拟列和Stored Generated Column，前者只将Generated Column保存在数据字典中（表的元数据），并不会将这一列数据持久化到磁盘上；后者会将Generated Column持久化到磁盘上，而不是每次读取的时候计算所得。很明显，后者存放了可以通过已有数据计算而得的数据，需要更多的磁盘空间，与Virtual Column相比并没有优势，因此，MySQL 5.7中，不指定Generated Column的类型，默认是Virtual Column。但虚拟列不能作为主键、外键。

   ```mysql
   -- 添加虚拟列 CREATE_DATE，并给给它加索引
   ALTER TABLE t ADD COLUMN CREATE_DATE  DATE AS (DATE(CREATE_TIME)), ADD KEY idx_CREATE_DATE (CREATE_DATE);
   -- 使用虚拟列索引
   SELECT * FROM t1 WHERE CREATE_DATE = '2017-10-08';
   ```

   注：创建generated column(包括virtual generated column 和stored generated column)时不能使用非确定性的（不可重复的）函数，如curtime()。

10. 使用索引进行条件查询时比较类型不一样时不会用到索引。如字符型字段为数字时在where条件里没有添加引号。

11. not in ,not exist 不会用到索引。

12. 如果查询的数据超过大表的20%，可能不会使用索引。

13. 到底是否会使用索引没有绝对性的，具体还要根据实际业务进行分析。



### 对 InnoDB 表使用 count()

InnoDB 表没有计数器，因为并发事务会在相同时间看到不同的行数。因此，SELECT COUNT(*) 语句只计算当前事务可见的行数。

在 5.7.2 ，InnoDB 引擎每次都通过聚集索引来处理 count() ，性能有提高，但是在某些情况下反而会比之前没有使用聚集索引时更差。在 5.7.18 中修复了此 bug ，改为如果存在一个更小的二级索引，会使用该索引来进行计数，而且更快（因为只需要读取一个字段，I/O减少了，性能就提高了）。

count(*) 函数是先从内存中读取表中的数据到内存缓冲区，然后扫描全表获得行记录数的，如果表的数据量很大而超过缓冲池大小时，性能会变低。这时可以考虑自己创建一个计数器表，但这种方法需要关注并发问题。如果不要求精确的计数，则可以使用 SHOW TABLE STATUS 语句。

count(*) 与 count(1) 效果一样，没区别。



### 分页 limit

越往后的数据，分页查询的效率越差。即使使用倒序查出后面的数据也会慢。

如果有加 where 条件或者 order by 等语句，使用索引会变快。尤其覆盖索引类型，因为它只包含了那个索引字段，不需要再去找该记录的其他数据，速度会更快。

因此，可以先对某字段（比如主键）进行分页查询，然后用返回的主键作为子查询的结果，来检索该表其它字段的值。或者类似地也可以用 inner join 。对大分页的情况，推荐使用 inner join ，速度更快。

```mysql
-- 子查询方法：
SELECT * FROM (SELECT * FROM t WHERE id > ( SELECT id FROM t ORDER BY id DESC LIMIT 935510, 1) LIMIT 10) t ORDER BY id DESC;
-- inner join 方法：
SELECT * FROM t INNER JOIN ( SELECT id FROM t ORDER BY id DESC LIMIT 935500,10) t2 USING (id);
```

如果是不带任何条件的分页，采用对主键或唯一索引采用范围检索的方法更合适，如：

```mysql
select * from t  where id <= max_id and id >= min_id limit 10;
```

