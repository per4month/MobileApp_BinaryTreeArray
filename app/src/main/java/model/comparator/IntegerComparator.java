package model.comparator;

import java.io.Serializable;

import model.usertype.type.IntegerClass;

public class IntegerComparator implements Comparator, Serializable {
    @Override
    public int compare(Object o1, Object o2) {
        return ((IntegerClass)o1).getValue() - ((IntegerClass)o2).getValue();
    }
}
