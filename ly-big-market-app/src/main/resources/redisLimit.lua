local key = KEYS[1];
if tonumber(redis.call('get', key) or '0') + 1 > 1000 then return 0;
else redis.call('incr', key);redis.call('EXPIRE', key, 60);
return tonumber(redis.call('get', key)); end