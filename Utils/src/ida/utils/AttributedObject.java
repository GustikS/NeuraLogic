package ida.utils;

import java.util.HashMap;

/**
 * Created by ondrejkuzelka on 24/09/16.
 */
public class AttributedObject<R,S> {

    private HashMap<R,S> attributes;

    public void setAttribute(R key, S value){
        if (this.attributes == null){
            this.attributes = new HashMap<R,S>();
        }
        this.attributes.put(key, value);
    }

    public S getAttribute(R key){
        if (this.attributes == null){
            return null;
        } else {
            return this.attributes.get(key);
        }
    }
}
