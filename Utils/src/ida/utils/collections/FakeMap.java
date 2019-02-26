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

package ida.utils.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Implementation fo interface Map<R,S> which remains empty no-matter what we put into it.
 * @param <R> type of the key-elements
 * @param <S>  type of the value-elements
 * @author admin
 */
public class FakeMap<R,S> implements Map<R,S> {

    private Set<R> keySet = Collections.<R>emptySet();

    private Collection<S> emptyCollection = Collections.<S>emptyList();

    private Set<Map.Entry<R,S>> entrySet = Collections.<Map.Entry<R,S>>emptySet();

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    public boolean containsKey(Object key) {
        return false;
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public S get(Object key) {
        return null;
    }

    public S put(R key, S value) {
        return null;
    }

    public S remove(Object key) {
        return null;
    }

    public void putAll(Map<? extends R, ? extends S> m) {

    }

    public void clear() {

    }

    public Set<R> keySet() {
        return keySet;
    }

    public Collection<S> values() {
        return emptyCollection;
    }

    public Set<Entry<R, S>> entrySet() {
        return entrySet;
    }

}
