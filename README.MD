# LiteNote

一个简洁的 Android 取件码等相关应用,支持桌面小部件功能。

## 功能特点

- 支持基本的笔记创建、编辑和删除
- 提供产品管理功能
- 支持桌面小部件显示笔记内容
- 支持深色模式
- 支持 Android 12+ 系统

## 技术栈

- Kotlin
- Jetpack Compose
- Room Database
- Glance (用于小部件)
- Material Design 3

## 系统要求

- Android SDK 29+ (Android 10.0 及以上)
- 最高支持 Android SDK 35

## 构建与运行

1. Clone 项目到本地
2. 使用 Android Studio Hedgehog 或更高版本打开项目
3. 同步 Gradle 依赖
4. 运行项目到模拟器或实机

## 项目结构

主要目录说明:

- `assets/`: 存放静态资源文件,如协议文档等
- `base/`: 数据库基础类
- `dbutils/`: 数据库操作工具类
- `entity/`: 数据实体类定义
- `service/`: 后台服务相关代码
- `sub/`: 子页面活动
- `utils/`: 通用工具类
- `widget/`: 自定义UI组件
- `desktopwidget/`: 桌面小部件实现

核心文件:

- `MainActivity.kt`: 应用主入口
- `HomePages.kt`: 首页UI实现 
- `Settings.kt`: 设置页面实现
- `Express.kt`: 快递相关功能组件
- `DesktopCodeMWidget.kt`: 桌面小部件实现


## 许可证

本项目基于 Apache License 2.0 开源。

## 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目。在提交 PR 前请确保:

1. 代码风格符合项目规范
2. 新功能有完整的测试覆盖
3. 所有测试用例通过
4. 更新相关文档

## 版本历史

当前版本: 2.3.1 (3000031)

## 联系方式

如有问题请提交 Issue 或发送邮件到 [wangxudong@oac.ac.cn]
```

