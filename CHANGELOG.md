# Changelog

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