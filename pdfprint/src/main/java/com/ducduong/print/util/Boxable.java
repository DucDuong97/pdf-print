package com.ducduong.print.util;

import lombok.Data;

@Data
public abstract class Boxable<T> {

    protected float mt = 0;
    protected float mr = 0;
    protected float mb = 0;
    protected float ml = 0;

    protected abstract float calculateElemHeight (T t, float maxWidth);
    protected abstract float calculateElemWidth  (T t);
    protected float calculateElemWidth  (T t, Float maxWidth, Float maxHeight) {
        return calculateElemWidth(t);
    }

    public float calculateWidth (T t) {
        return calculateElemWidth(t) + ml + mr;
    }

    public float calculateWidth (T t, Float maxWidth, Float maxHeight) {
        return calculateElemWidth(t, maxWidth, maxHeight) + ml + mr;
    }

    public float calculateHeight (T t, float maxWidth) {
        return calculateElemHeight(t, maxWidth) + mt + mb;
    }
}
