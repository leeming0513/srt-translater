srt-translater
==============

### 作用和原理
自动把英文字母转换成中英双字幕。

程序会自动调用 google translate api 进行翻译。

### 用法
下载脚本到你喜欢的位置。

到字幕所在文件夹，执行以下语句（支持批量）：

```
find . -name "*.srt" -print0 | xargs -0 groovy ./srt-translater.groovy
```