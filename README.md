# kafka 本地调试

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
./kafka-topics.sh --zookeeper 10.36.8.171:2181 --create --topic topic_test --partitions 1 --replication-factor 1
```

![picture 4](images/55947cfdfe0b454a668896588d0ce2c8b0cea952eb46b0fe76b539d3feb32983.png)  
![picture 5](images/1fa789d59dc08573b4951db102d65e09b14ff38b44e1b6c480e9eb2c9faebca9.png)  
