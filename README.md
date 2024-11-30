# AppDevFramework 0.3.1
简易AndroidApp开发库框架

[![](https://jitpack.io/v/AIDEProjects/AppDevFramework.svg)](https://jitpack.io/#AIDEProjects/AppDevFramework)

# 待办: 
- 手势管理器
- - 更新区分缩放与位移方式由指间距变为角度差
- - 增加屏幕大小设定以决定位移量
- - 增加Align属性区分LEFTDOWN与CENTER对齐
- - 缩放为以视图中心为原点

# 更新
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


