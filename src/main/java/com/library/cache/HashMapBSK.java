package com.library.cache;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
@Slf4j
public class HashMapBSK<K,V> extends HashMap<K,V> {
    @Override
    public V get(Object key) {
        V value = super.get(key);
        if (value != null) {
            log.info("CACHE HIT: {}", key);
        }
        else {
            log.info("CACHE MISS: {}", key);
        }
        return value;
    }
}