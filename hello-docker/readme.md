# kafka-connect-zeebe

- [kafka-connect-zeebe](#kafka-connect-zeebe)
  - [启动docker](#启动docker)
  - [启动源码](#启动源码)
  - [关闭](#关闭)
  - [connector运行成功](#connector运行成功)
  - [部署bpmn](#部署bpmn)
  - [完整部署流程](#完整部署流程)
    - [清理](#清理)
    - [启动docker](#启动docker-1)
      - [验证](#验证)
    - [启动kafka和connector](#启动kafka和connector)
      - [验证](#验证-1)
    - [modeler部署bpmn](#modeler部署bpmn)


## 启动docker
+ npm run start

![picture 0](../images/31139f875ba2c1817de69bc699739f7677ef361928bc540ccadd3ae58e241263.png)  



## 启动源码
+ https://github.com/robin2017/kafka-2.7.0/tree/dev-connector-zeebe


## 关闭
```
关掉kafka和connector
npm run clear
npm run restart
```

## connector运行成功

![picture 1](../images/e8573d90e12b2ff050cb55a467c6c917ab12da578c9224260f0c9a07d3fa022d.png)  

![picture 2](../images/32d7343b77b946c393ae359a6a43eba1bd27090940e622aec9981ea37495f56c.png)  


## 部署bpmn
+ 使用modeler部署，很方便
+ 但是在kafka-ui上看不到broker和topic！！！？？？

![picture 3](../images/b29ebc14df6cb76603cfe71f2d8b9660cab66d6beef9d0d93787e0250ad69052.png)  

![picture 4](../images/d0aa9a11c7c3900ffb74384234e532c517937da028a11430e6be4d6eacf40297.png)  
![picture 5](../images/cc1d8fff8717b95680c3b5ebb8111badfeb854429cf49f253cffb5abc1e2c1ab.png)  
![picture 6](../images/56ddeb47b105324069d6cb49944fcfd942ac0fd6d62e8891d4cb1905c26ed33a.png)  
![picture 7](../images/374ac506dca421fd6fa6b3f353728dc3a656f44cb392dc7c7094ecfb0ade09ba.png)  


## 完整部署流程
### 清理
+ docker的容器和volumn都清理干净
+ 清理kafka-logs：npm run clear
+ 查看ip，所有kafka-2.7.0整个项目的ip

### 启动docker
+ npm run start

#### 验证
+ 查看zeebe是否成功


![picture 8](../images/57740c616de36b6b8fac91f1e0936a7e75c4a5ad46b01b7f619eeb9da0cd81e0.png)  
![picture 12](../images/bbe62c5bbbcddf1e87c86583bba26949bd5e4c9ae3f3936b2a6e7375920487bb.png)  


### 启动kafka和connector
+ 按照顺序启动akfka和connector

#### 验证
+ 查看两个服务是否启动成功
+ 通过kafka-ui查看broker和topic信息


![picture 9](../images/9a33d3ae92c793c31ac95693fd29ed8694c32bd5a58c8b6ff1580e8f8ed5ba5a.png)  
![picture 10](../images/302619f767ec47648dd33e2c83fb264c050c9fdf28ee8c3f94ba920084c9b00d.png)  
![picture 11](../images/cb3fac9887a6bf6c1493401ae23685ddff8caadb9aa75b4cd2dbbc027e5ffb03.png)  
![picture 13](../images/f644bad3ddf6abfe2cb7127a7f806514b5f1cb80f946c9f4296f01f7205608d4.png)  


### modeler部署bpmn
+ bpmn文件在hello-data目录下

