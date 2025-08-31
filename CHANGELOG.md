# Changelog

## 0.6.0-pre.1
 - 修复开字母功能已知bug
 - 添加判断开字母中的歌曲是否已经被猜出/失败的方法

## 0.6.0-pre
 - 开字母功能测试

## v0.5.4
 - 修复真代牌子完成表歌曲重复的问题

## v0.5.3
 - 修正完成表中一个等级歌曲数目刚好为10的倍数时出现的空行问题

## v0.5.2
 - 修正`RecordUtils`中`parsePlateRecord`方法返回的记录没有同步状态的问题

## v0.5.1
 - 为`MaiVersion`类的`matchingNames`添加getter方法

## v0.5.0
 - 为游玩记录提供了模型类
 - 新增查询牌子完成表的功能

## v0.4.2
 - 此版本及以后版本的依赖包中将会附带`-sources.jar`
 - 没有任何在代码层面的修改

## v0.4.1-hotfix
 - 修复`SongPlayDataRenderer`有`Re:Master`难度的歌曲渲染底部Credits时的颜色问题

## v0.4.0
 - 添加查询单曲游玩信息的功能 （需要在配置文件的`diving_fish.dev_token`一项中填写开发者token）

## v0.3.0-alpha
 - api的`generate()`方法现在会返回输出文件的路径
 - api内的逻辑被拆分（网络请求、图片绘制与输出现在由单独的类负责）
 - api类在遇到`NotInitializedException`时会继续抛出供外部方法捕获并处理
 - `SongManager`的初始化逻辑实际上改为“更新”（即刷新内存中的数据）
 - 修复`SongManager`获取拟合难度时可能出现的`NullPointerException`

## v0.2.4-alpha
 - 使SongManager线程安全，并添加初始化检查
 - SongManager的初始化将异步进行

## v0.2.3-alpha
 - 在SongManager中添加一键查询的方法

## v0.2.2-alpha
 - 谱面信息在本地有缓存时将不再重新生成，而是直接调用本地缓存

## v0.2.1-alpha
 - 更改通过标题获取歌曲的方式，适配标题冲突的情况

## v0.2.0-alpha
 - 增加谱面信息图片生成
 - 增加单曲Rating计算

## v0.1.3-alpha
 - 修正dx分星数的判定

## v0.1.2-alpha
 - 将jar内的字体文件移除，改为外部获取

## v0.1.1-alpha
 - 修复b50内有dx分0星成绩时无法查询的bug

## v0.1.0-alpha
 - 添加b50查询