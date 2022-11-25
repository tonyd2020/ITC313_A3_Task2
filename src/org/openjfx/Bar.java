/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openjfx;

/**
 *
 * @author Tony RMIT
 */
public class Bar {
    private int value;
    private boolean isLast;

    public Bar(int value, boolean isLast) {
        this.value = value;
        this.isLast = isLast;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isIsLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }
    
    
}
