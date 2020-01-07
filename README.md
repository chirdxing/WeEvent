<<<<<<< HEAD
[中文](README.md) | [English](README-en.md)

![image](https://github.com/WeBankFinTech/WeEvent-docs/blob/master/docs/image/weevent-logo.png)

[![CodeFactor](https://www.codefactor.io/repository/github/webankfintech/weevent/badge)](https://www.codefactor.io/repository/github/webankfintech/weevent)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1d2141e952d84a47b0a615e51702bf6f)](https://www.codacy.com/app/WeEventAdmin/WeEvent?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=WeBankFinTech/WeEvent&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.com/WeBankFinTech/WeEvent.svg?branch=master)](https://travis-ci.com/WeBankFinTech/WeEvent)
[![Latest release](https://img.shields.io/github/release/WeBankFinTech/WeEvent.svg)](https://github.com/WeBankFinTech/WeEvent/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/com.webank.weevent/weevent-client.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.webank.weevent%22%20AND%20a:%weevent-client%22)
[![Documentation](https://img.shields.io/badge/api-reference-blue.svg)](https://weeventdoc.readthedocs.io/zh_CN/latest/protocal/index.html)
[![Documentation Status](https://readthedocs.org/projects/weeventdoc/badge/?version=latest)](https://weeventdoc.readthedocs.io/zh_CN/latest)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 什么是WeEvent？
WeEvent是一套分布式事件驱动架构，实现了可信、可靠、高效的跨机构、跨平台事件通知机制。

WeEvent由微众银行自主研发并完全开源，秉承分布式商业模式中对等合作、智能协同、价值共享的设计理念，致力于提升机构间合作效率，降低合作成本，同时打通应用程序、物联网、云服务和私有服务等不同平台，最终在不改变已有商业系统的开发语言、接入协议的情况下，做到跨机构、跨平台的事件通知与处理。  
[WeEvent官网](http://fintech.webank.com/weevent)。

## 快速入门
WeEvent支持三种安装方式：[Docker镜像](https://hub.docker.com/r/weevent/)、[Bash一键脚本](https://weeventdoc.readthedocs.io/zh_CN/latest/install/quickinstall.html)、[进阶安装](https://weeventdoc.readthedocs.io/zh_CN/latest/install/module/index.html)。
### Docker镜像安装
```shell
$ docker pull weevent/weevent:1.0.0; docker run -d -p 8080:8080 weevent/weevent:1.0.0 /root/run.sh
```

### Bash一键安装
[下载](https://weeventdoc.readthedocs.io/zh_CN/latest/install/download.html)并且解压安装包，如weevent-1.0.0.tar.gz。在解压目录中执行安装脚本。
```shell
$ ./install-all.sh -p /usr/local/weevent/
```

### 功能体验
通过浏览器即可体验事件发布等基本功能，如[发布事件](http://localhost:8080/weevent/rest/publish?topic=test&content=helloevent)。  
更多功能，请参见[WeEvent接入](https://weeventdoc.readthedocs.io/zh_CN/latest/protocal/restful.html)。

## 用户文档
[WeEvent在线文档](https://weeventdoc.readthedocs.io/zh_CN/latest)。

## 项目贡献
*   开发环境  
git，gradle 4.10，java 1.8，nodejs 10.16，推荐使用IntelliJ IDEA。
*   [项目计划](https://github.com/WeBankFinTech/WeEvent/wiki/Project-RoadMap)  
*   [代码提交](https://github.com/WeBankFinTech/WeEvent/wiki/Project-WorkFlow)  

WeEvent爱贡献者！请阅读[贡献文档](https://github.com/WeBankFinTech/WeEvent/blob/master/CONTRIBUTING.md)，了解如何贡献代码，并提交你的贡献。

希望在您的帮助下WeEvent继续前进。

## 社区
*   联系我们：weevent@webank.com  
=======
## WeEvent 介绍
`WeEvent`是一个基于区块链实现的事件中间件，为业务提供事件发布/订阅`Pub/Sub`功能。发布到`WeEvent`上的事件，永久存储和不可篡改，支持事后跟踪和审计。
支持`Restful`、`RPC`、`JsonRPC`、`STOMP`等多种接入方式，


## 特性
- Broker服务：提供主题`Topic`的`CRUD`管理、事件发布/订阅`Pub/Sub`等功能；
- 适配多协议：支持`Restful`、`JsonRPC`、`STMOP`、`MQTT `等多种接入协议；
- Java SDK：提供一个符合`JMS`规范的Jar包，封装了`Broker`服务提供的各种能力；
- 高可用性：完善的主备服务切换和负载均衡能力；
- 丰富样例：各种接入方式的使用代码样例。


## 快速入门
- 安装前置依赖

区块链是`WeEvent`的前置依赖，用户需要提前安装，具体操作见[FISCO-BCOS文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/docs/tools/index.html)。

- 搭建服务

快速搭建一套`WeEvent`的服务，请参考[文档](http://)。通过一键部署的`WeEvent`的服务，用户可以快速体验和开发。

- 体验订阅

用户可以下载[Client](http://)，体验创建主题`Topic`，发布/订阅事件`Event`。

## 贡献说明
WeEvent爱贡献者！请阅读我们的贡献[文档](http://)，了解如何贡献代码，并提交你的贡献。

希望在您的帮助下WeEvent继续前进。


## 社区
- 联系我们：weevent@webank.com
>>>>>>> init WeEvent
