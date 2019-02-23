/*
 * Event.java
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
public interface Event {

    Type getType();

    public class Type<ID_CLASS> implements Serializable {

        private ID_CLASS id;

        public Type(ID_CLASS name) {
            this.id = name;
        }

        public ID_CLASS getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Type) {
                Type other = (Type) o;
                return id == null ? other.id == null : id.equals(other.id);
            } else {
                return false;
            }
        }

        @Override
        public String toString() {
            return new StringBuilder("Event.Type(").append(id).append(")").toString();
        }

        @Override
        public int hashCode() {
            return id == null ? 0 : id.hashCode();
        }
    }

}
