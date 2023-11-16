# kafka 本地调试

- [kafka 本地调试](#kafka-本地调试)
  - [基础环境](#基础环境)
  - [代码构建](#代码构建)
  - [运行](#运行)
    - [常见错误](#常见错误)
    - [成功](#成功)
    - [验证](#验证)
  - [connector环境](#connector环境)
    - [关闭](#关闭)
    - [重启](#重启)
    - [加断点](#加断点)
      - [将file包四个文件复制到runtime包中，可以调试](#将file包四个文件复制到runtime包中可以调试)
    - [日志](#日志)
  - [文件连接器调试](#文件连接器调试)


[聊聊 Kafka：编译 Kafka 源码并搭建源码环境](https://xie.infoq.cn/article/6fd06714623437ebed2f43a47)

## 基础环境
+ 本地如果装了多个jdk，通过jenv切到jdk8
+ 并去idea配置中，确保都是jdk8
+ 启动hello-docker环境

![picture 0](images/ffb8fcba11e4488221b41ce5baaec0f3601d6c95352572cb296d0bb698d92680.png)  

## 代码构建
+ 耗时5min左右

```
./gradlew clean build -x test
```

![picture 1](images/69f844e56a92b0291763d88080d65c241bb35a80fc92846d9dabd8e90951e47f.png)  


## 运行



### 常见错误
+ 指定JAVA_HOME为jdk8路径
+ 查看zk是否启动了,并且9092端口是否被占用了
+ 查看配置中ip地址是否正确
+ kafka-logsw文件夹删掉


![picture 2](images/35bce7acc6d0494cbe47735310134a01a8b5a48cdd83cc7978ab166d24191610.png)  

### 成功
![picture 3](images/b7c8ed5bd3058263897cbd11457a38eb5654573ad030bef69a81ab34de0eaacd.png)  

### 验证
```
cd bin
./kafka-topics.sh --zookeeper 192.168.12.159:2181 --create --topic topic_test --partitions 1 --replication-factor 1
```

![picture 4](images/55947cfdfe0b454a668896588d0ce2c8b0cea952eb46b0fe76b539d3feb32983.png)  
![picture 5](images/1fa789d59dc08573b4951db102d65e09b14ff38b44e1b6c480e9eb2c9faebca9.png)  


## connector环境
+ 创建文件夹~/kafka-source-logs

```
# 写入数据
npm run source-input
```

![picture 6](images/36902057ad168ae00098d1412424a3dc1c9a388deb7ba8aa7a690c01034a18e9.png)  
![picture 7](images/33b7d982d032c669a65aa344cc9b0218dd0b74a454509f7ee9c5369d49ef3dbd.png)  
![picture 8](images/59fbc01b003e8c590079a30af75e40bc96a192639bd05488329898ce88679bbb.png)  

### 关闭
+ idea的kafka关掉
+ connector-start的命令行关闭
+ 清理npm run clear
+ 重启npm run restart，因为topic注册到zk上去了

### 重启
+ 直接运行idea的kafka调试按钮


### 加断点
+ 因为kafka和connector是两个程序，之前启动connector不是idea起的！！！
![picture 9](images/6f9ae8d0c9b00961ca2874744c4d843e4332a159e36d3157550af0bee40793b3.png)  

![picture 10](images/cb96584461828bdb8ef638dd5d7b29bf0449f79b04cf790152c1e7c07124f3f2.png)  
![picture 11](images/238cf695c4311f034e1b7088bc7798fa74a3d6b0fa65446357482c7a98470e31.png)  

#### 将file包四个文件复制到runtime包中，可以调试

![picture 12](images/e3b6c9b4d75364397fd09c07dda0a255a7614a9f38eb332bc552d0f41c1e00d2.png)  

### 日志

![picture 13](images/77d4a44e710eb50fec83ff245c2f87d5251a5a70e58dd2144c1c9b67647abbfe.png)  


## 文件连接器调试
[参见代码仓库](https://gitee.com/wangy202012/spring-source-learning/blob/master/doc/kafka-connector.md)