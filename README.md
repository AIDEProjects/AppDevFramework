[![](https://jitpack.io/v/AIDEProjects/AppDevFramework.svg)](https://jitpack.io/#AIDEProjects/AppDevFramework)

# AppDevFramework 0.6.3
简易AndroidApp开发库框架

# 当前内容
1. UtilActivity: 主活动基类
	- 自动初始化日志选项
	- 自带读写权限请求配置, 无标题栏配置
1. Project与Res:
	- Project: 用于项目初始配置，包括ProjName, LogPath等
	- Res: 一些路径静态常量
1. AppLog与Log与Logcat
	- AppLog用于程序内日志toast与dialog, dialogE
	- Log用于存储本地Log
	- Logcat用于打印logcat以及检查程序Fatal异常日志
1. LogView与DebugView
	- LogView用于程序内显示日志列表信息
	- DebugView用于程序内显示调试项信息
1. 手势处理器：一个支持双指移动缩放视图的触摸处理器
1. 自由变换布局: 基于手势处理器的FrameLayout布局实现
1. Vector2/Vector2Int: 封装完善的2d向量操作方法，包括float与Int型，Vec2Int实现于Vec2是其孪生类
1. SampleListManager: 一个预设的SampleActivity列表初始化器，用于简单生成多Activity演示的ListView条目
1. PrefUtils: 一个简单的sharedReferences数据方法封装
1. FilesTool: 十分简单的文件操作封装，读写等
1. StringUtils: 字符串工具类，用于自然字符排序等

# 待办: 

# 更新
## 0.6.3: 重设计手势处理器接口

## 0.6.2: 自由变换布局增加xml-attrs控制手势参数
1. app:enableViewportGesture="true"
2. app:enableViewportGestureConstrain="false"

## 0.6.1: 修复api问题var

## 0.6.0: 解决限制边不完善的问题
1. 创建了decomposeRealStagePos()来反推分离出stagePos与stageSclOffset以解决constrain没有实际限制stagePos的问题

## 0.5.2: 重新增加手势处理器与其Frame布局实现
1. 创建多Activity结构
	- ActivityList增加手势处理器演示
1. 开发手势处理器
	- FreeTransformLayout自由变换布局
	- GestureHandler手势滑动缩放处理，限制边界

## 0.5.1
1. 修复无Save权限ctx不能用的问题
	- 将多个ctx合并到一个AppUtils.setCtx中


## 0.5.0
- 修复空logs报错
- log在无权限时自动禁用
- 补上子线程的异常捕获

## 0.4.0
- 增加StringUtils用于字符串自然排序
- 修复了NewLogPath的获取bug：现在会删除旧的log而不是最新的
- Vector2增加四则(两常量参数)

## 0.3.0
- 添加手势管理器GestureManager来处理布局的自由滑动并限制区域与缩放
- 重命名apputils.Utils为AppUtils并增加获取窗口宽高方法
- AppUtils增加dp2px方法
- logs存储最大限制maxLogCount(10)个文件

## 0.2.0
- 改写Vector2实现方式，增加孪生类型Vector2Int
- 添加一个配置文件来手动设置hasCrash: true/false, 以禁用程序运行以便查看错误日志.


