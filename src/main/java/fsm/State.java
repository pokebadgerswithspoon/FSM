/*
 * State.java
 * Created on 23 Feb 2019 19:22:57
 * by lauri
 *
 * Copyright(c) 2019.  All Rights Reserved. ☣
 * This software is the proprietary information.
 */
package fsm;

import java.io.Serializable;

/**
 *
 * @author lauri
 */
public class State<T> implements Serializable {

    private T id;

    public State(T id) {
        this.id = id;
    }

    public T getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof State) {
            State other = (State) o;
            if (id == null) {
                return other.id == null;
            } else {
                return id.equals(other.id);
            }
        } else {
            return false;
        }
    }
}
