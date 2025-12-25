package com.example.demo.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis 设置缓存失败，key: {}", key, e);
        }
    }

    /**
     * 设置缓存并指定过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis 设置缓存失败，key: {}, timeout: {}", key, timeout, e);
        }
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis 获取缓存失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存（指定类型）
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null && clazz.isInstance(value)) {
                return (T) value;
            }
            return null;
        } catch (Exception e) {
            log.error("Redis 获取缓存失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 删除缓存
     */
    public Boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis 删除缓存失败，key: {}", key, e);
            return false;
        }
    }

    /**
     * 批量删除缓存
     */
    public Long delete(Collection<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Redis 批量删除缓存失败，keys: {}", keys, e);
            return 0L;
        }
    }

    /**
     * 判断key是否存在
     */
    public Boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Redis 判断key是否存在失败，key: {}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            log.error("Redis 设置过期时间失败，key: {}, timeout: {}", key, timeout, e);
            return false;
        }
    }

    /**
     * 获取过期时间
     */
    public Long getExpire(String key, TimeUnit unit) {
        try {
            return redisTemplate.getExpire(key, unit);
        } catch (Exception e) {
            log.error("Redis 获取过期时间失败，key: {}", key, e);
            return -1L;
        }
    }

    /**
     * 设置 Hash 缓存
     */
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("Redis 设置Hash缓存失败，key: {}, hashKey: {}", key, hashKey, e);
        }
    }

    /**
     * 批量设置 Hash 缓存
     */
    public void hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("Redis 批量设置Hash缓存失败，key: {}", key, e);
        }
    }

    /**
     * 获取 Hash 缓存
     */
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("Redis 获取Hash缓存失败，key: {}, hashKey: {}", key, hashKey, e);
            return null;
        }
    }

    /**
     * 获取 Hash 所有键值对
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis 获取Hash所有键值对失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 删除 Hash 缓存
     */
    public Long hDelete(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            log.error("Redis 删除Hash缓存失败，key: {}, hashKeys: {}", key, hashKeys, e);
            return 0L;
        }
    }

    /**
     * 设置 Set 缓存
     */
    public Long sAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Redis 设置Set缓存失败，key: {}", key, e);
            return 0L;
        }
    }

    /**
     * 获取 Set 所有成员
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis 获取Set所有成员失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 判断是否是 Set 成员
     */
    public Boolean sIsMember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("Redis 判断Set成员失败，key: {}, value: {}", key, value, e);
            return false;
        }
    }

    /**
     * 设置 List 缓存
     */
    public Long lPush(String key, Object value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            log.error("Redis 设置List缓存失败，key: {}", key, e);
            return 0L;
        }
    }

    /**
     * 获取 List 缓存
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("Redis 获取List缓存失败，key: {}, start: {}, end: {}", key, start, end, e);
            return null;
        }
    }

    /**
     * 自增
     */
    public Long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis 自增失败，key: {}, delta: {}", key, delta, e);
            return -1L;
        }
    }

    /**
     * 获取匹配的keys
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("Redis 获取匹配keys失败，pattern: {}", pattern, e);
            return null;
        }
    }

    /**
     * 清空所有缓存（谨慎使用）
     */
    public void flushAll() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.error("Redis 清空所有缓存失败", e);
        }
    }

    /**
     * 获取 Redis 信息
     */
    public String getRedisInfo() {
        try {
            return redisTemplate.getConnectionFactory().getConnection().info().toString();
        } catch (Exception e) {
            log.error("Redis 获取信息失败", e);
            return null;
        }
    }
}