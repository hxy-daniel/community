# community 项目

## 敏感词过滤
前缀树
- Trie、字典树、查找树
- 查找效率高，消耗内存大
- 应用：字符串检索、词频统计、字符串排序等

敏感词过滤器
- 定义前缀树
- 根据敏感词，初始化前缀树
- 编写过滤敏感词的方法

## 发布帖子
- fastjson
- ajax
- 敏感词过滤
- 转译HTML标签内容为普通文本（HtmlUtils.htmlEscape）

## 帖子详情页
- utext格式显示
- 日期格式化
-`（th:text="${#dates.format(discussPost.createTime,'yyyy-MM-dd HH:mm:ss')}）`
- 首页帖子列表点击链接
- `th:href="|@{/discussPost/detail/${item.post.id}|"`