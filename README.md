[![](https://jitpack.io/v/AIDEProjects/AppDevFramework.svg)](https://jitpack.io/#AIDEProjects/AppDevFramework)

# AppDevFramework 0.6.8-alpha
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
	- - Log.setTagMode(tag, encodeMode)可设置标签可见性local/view
	- Logcat用于打印logcat以及检查程序Fatal异常日志
1. LogView与DebugView
	- LogView用于程序内显示日志列表信息
	- DebugView用于程序内显示调试项信息
1. 手势处理器：一个支持双指移动缩放视图的触摸处理器
1. 自由变换布局: 基于手势处理器的FrameLayout布局实现
1. Vector2/Vector2Int: 封装完善的2d向量操作方法，包括float与Int型，Vec2Int实现于Vec2是其孪生类
1. SampleListBuilder: 一个预设的SampleActivity列表初始化器，用于简单生成多Activity演示的ListView条目
1. DebugActivityLayoutBuilder: 预设的纵向DebugView+mainView+LogView视图布局构建器, 用于简单的创建带调试信息的主活动布局
1. PrefUtils: 一个简单的sharedReferences数据方法封装
1. FilesTool: 十分简单的文件操作封装，读写等
1. StringUtils: 字符串工具类，用于格式化时间戳, 获取格式化枚举名, 自然字符排序等
1. Angle类: 
	- angleDifference(angle1, angle2)获取两角度方向差值用于判断是否为目标方向
1. MathUtils: 
	- preciNum(num, digit)获取保留digit位有效精度的四舍五入数

# 待办: 
- 解决在新活动报错而报错dialog在旧活动看不见的问题: 
	- 方法一: 使用applicationContext替代activityContext

# 更新
## 0.6.9: 解决新活动dialog看不见的问题
	- 统一使用UtilActivity，这样就自动setCtx到新的活动了

## 0.6.8-alpha: 功能增强与优化
- **DebugActivityLayoutBuilder**：重构构造函数，优化布局初始化，新增调试视图和日志视图控制方法，改进视图对齐方式。
- **FreeTransformLayout**：新增坐标符号变量 `coordSign`，重命名变量为 `translation`，优化绘制和布局处理。
- **GestureHandler**：增强手势事件日志记录，支持双指触摸缩放和位移，优化死区处理和坐标转换，改进缩放和位移约束。
- **Log.java**：初始化 `RealtimeInfo` 标签。
- **Vector2.java**：增强数值比较，新增 `getDirectionString` 方法。

其他文件包括布局更新、文本显示优化等。

## 0.6.7: 手势处理器增加一个输出操作细节的log
	- 显示位移方向与距离与当前位置: 
	- - direction: ↖/↘...
	- - distance: 8/5...
	- - realStagePos(是否被约束): 100, 100...
	- 以及缩放操作(放大或缩小)与程度与当前因子: 
	- - mode: +/-
	- - sclDiff: 0.3/0.6...
	- - stageSclFactor(是否被约束): 2/1.8...

## 0.6.6.1: 简化Log设置筛选模式方法为setTagMode(tag, local, view)

## 0.6.6: 完善Log相关，增加一个简单的调试视图布局构建器
1. 再次简化DebugActivityLayoutBuilder，现在可以简单new DebugActivityLayoutBuilder(ctx, gameView)即可
1. 解决DebugView.setDebugInfo的线程问题以及整理调试log输出
1. 自由变换布局gestureManager变量更名handler
1. LogView添加TAG.LogAdd标签用于于Local输出操作日志视图信息
	- addLog方法放入MainLooper post执行以解决线程问题
1. Log类
	- 将banTags改为showTags
	- 并将显示变量改为LogMode相位掩码使用encodeMode(local, view)来切换本地和视图的显示隐藏
	- log格式化改为时间戳+完整tag+logMsg

## 0.6.5.1-beta: 修复子线程调用logView通知更新问题

## 0.6.5-alpha: 改名SampleListBuilder, 增加DebugActivityLayoutBuilder

## 0.6.4-alpha: 修复杂项bug与优化问题
1. Log更新
	- 取消Logf..统一为Log...
	- Log()方法将带\n多行log前缀加上tagName
	- 整理banTags列表，将内置Log由TAG分类并在初始化时设置show or ban
	- 增加几处LogT: 
	- - FreeTransformLayout的初始化日志
	- - GestureHandler的InCenter配置日志，与约束位移日志
1. LogView更新
	- 将log排列由逆序重新改回顺序排列并自动滑动置底部
1. 增加debug_activity_main.xml调试布局
1. 将LogView与DebugView字体改小以便显示更多信息
1. StringUtils增加获取enumFullName方法
1. 修复Vector2Int的bug：
	- 在V2Int被声明为V2时访问其xy会为0
	- 修复：在setXY处同步设置super.xy即可

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


