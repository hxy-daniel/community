# community 项目

## Mapper实现
- xml
- 接口注解

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

## 事务管理
- 编程式事务
- 声明式事务

## 评论
- 评论entityType 可以使帖子或评论（回复）
- 评论entityId
- 查询一页评论数据
- 查询评论的数量
- 评论：对帖子的操作
- 回复：对评论的操作
- 楼层`th:text="${page.offset + comment.count}`
- 详细显示逻辑

- 添加评论数据+修改帖子的评论数量（事务管理）

## 私信列表
- 会话id: 小id_大id
- 私信列表
- 私信详情

## 发送私信
- 异步发送
- 发送成功后刷新私信列表

## 设置已读
- 访问私信详情时

## 统一处理异常
- ControllerAdvice

## 统一记录日志
- AOP
- Target(目标对象):很多Target对象
  - Joinpoint(连接点):每个Target对象有很多Joinpoint
- Aspect
  - Pointcut(切点):哪些对象的哪些位置
  - Advice(通知):具体植入的系统逻辑 + 位置（前、后、返回、异常）

- AOP的实现
  - AspectJ
  - Spring AOP
    - CGLIB

- Aspect内获取Request/ip
  - RequestContextHolder

## Redis
- 场景
  - 缓存、排行榜（热门帖子）、计数器（帖子访问量）、社交网络（点赞、关注）、消息队列
  - 配置RedisTemplate
- 编程式事务
  - multi()
  - exec()
  - 事务中不要做查询（Redis事务指令存放在队列中，最后提交时才全执行，所以查询为空）
- RedisKeyUtil
- 点赞
  - 帖子、评论点赞
  - 点赞与取消点赞
  - 首页点赞数量
  - 详情页点赞数量
  - 显示点赞状态
  - like(this,entityType,entityId) 
    - 三种点赞 帖子1 两种评论2 
    - set中存放用户id集合
- 收到的赞
  - 以用户为key记录点赞数量
  - increment(key) decrement(key)
  - 开发个人主页 查询点赞量
  - 问题
    - th:onclick()中变量不能传字符串
    - discussPost中userId字段误设为String,应为int,数据库中是varchar
- 关注
  - 关注数、粉丝数
- 关键
  - Follower/Followee
    - 不能关注自己
    - 用户未登录判断
  - 目标可以是用户、帖子、题目等，抽象为实体

## 关注、粉丝列表
- 分页

## 优化登录模块
- Redis存储验证码
  - 登录前生成
  - 登录后验证
- Redis存储登录凭证（ticket）
  - 登录后存储
  - 退出后删除
  - 查询凭证处理
- Redis缓存用户信息

## 系统消息
- 阻塞队列(BlockingQueue)
  - put
  - take
- kafka
  - 一个分布式的流媒体平台
  - 应用
    - 消息系统、日志收集、用户行为跟踪、流式处理
  - 特点
    - 高吞吐量、消息持久化（硬盘）、高可靠性（分布式）、高扩展性
  - 术语
    - Broker(服务器)、Zookeeper(管理集群)
    - Topic(存放消息的位置/空间，点对点、发布订阅)、Partition(一个主题多个分区，高并发)、Offset(消息在分区内存放的索引)
    - Leader Replica(主副本，负责处理请求，每个分区多个副本)、Follower Replica(从副本，只负责备份)
  - 配置
    - zookeeper.properties中数据存放目录
    - server.properties中日志存放目录
- Spring整合Kafka
  - 引入依赖
    - spring-kafka
  - 配置Kafka
    - server、consumer
  - 访问Kafka
    - 生产者
      - kafkaTemplate.send(topic, data)
    - 消费者
      - @kafkaListener(topic={"test"})
      - public void handleMessage(ConsumerRecord record)
- 发送系统通知
  - 触发事件
    - 评论后 comment
    - 点赞后 like
    - 关注后 follow 
  - 处理事件
    - 封装事件对象
    - 开发事件的生产者
    - 开发事件的消费者