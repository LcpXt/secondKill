local lockValue = redis.call("get", KEYS[1])

if (lockValue == ARGV[1]) then
    redis.call("del", KEYS[1])
end