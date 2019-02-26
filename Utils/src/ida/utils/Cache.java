/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ida.utils;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class implementing a simple cache.
 * 
 * @param <R> type of the keys of the cached values
 * @param <S> type of the cached values
 * @author Ondra
 */
public class Cache<R,S> {
    
    private SoftReference<ConcurrentHashMap<R,S>> softRef;
    
    /**
     * Creates a new empty instance of class Cache
     */
    public Cache(){}
    
    /**
     * Stores a new element to the cache.
     * @param key key of the element
     * @param value the lement to be stored
     */
    public void put(R key, S value){
        ConcurrentHashMap<R,S> map = null;
        if (softRef == null || (map = softRef.get()) == null){
            map = new ConcurrentHashMap<R,S>();
            softRef = new SoftReference<ConcurrentHashMap<R,S>>(map);
        }
        map.put(key, value);
    }
    
    /**
     * Retrieves an element from this cache.
     * @param key key of the element
     * @return the element associated to the key or null
     */
    public S get(R key){
        ConcurrentHashMap<R,S> map = null;
        if (softRef == null || (map = softRef.get()) == null){
            return null;
        }
        return map.get(key);
    }
    
    /**
     * Clears this cache
     */
    public void clear(){
        if (this.softRef != null){
            this.softRef.clear();
        }
    }
}
