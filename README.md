[![](https://jitpack.io/v/AIDEProjects/AppDevFramework.svg)](https://jitpack.io/#AIDEProjects/AppDevFramework)

# AppDevFramework 0.5.3
简易AndroidApp开发库框架

# 设计
1. 手势处理器：一个支持双指移动缩放视图的触摸处理器
- - 获取当前位移Translate()
- - 获取当前缩放Scale()
- - 设置位移限制Margin()
- - 设置缩放限制ScaleLimit()

# 待办: 

# 更新
## 0.5.3: 解决限制边不完善的问题
1. 创建了decomposeRealStagePos()来反推分离出stagePos与stageSclOffset以解决constrain没有实际限制stagePos的问题

## 0.5.2: 重新增加手势处理器与其Frame布局实现
1. 创建多Activity结构
- - ActivityList增加手势处理器演示
1. 开发手势处理器
- - FreeTransformLayout自由变换布局
- - GestureHandler手势滑动缩放处理，限制边界

## 0.5.1
1. 修复无Save权限ctx不能用的问题
- - 将多个ctx合并到一个AppUtils.setCtx中


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


