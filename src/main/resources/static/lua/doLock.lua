-- lua脚本中通过redis.call() 执行redis命令
-- redis.call() 第一个参数永远是你要操作的redis命令的名称
-- 后续的参数就按照书写redis命令的形式去写

-- 如何获取jedis执行evalsha传入的参数
-- 获取keyList 就要用  KEYS[从1开始的索引值]
-- 获取valueList 就要用 ARGV[从1开始的索引值]

if (redis.call("exists", KEYS[1]) == 1) then
    return 0
else
    redis.call("set", KEYS[1], ARGV[1])
    redis.call("expire", KEYS[1], 5)
    return 1
end