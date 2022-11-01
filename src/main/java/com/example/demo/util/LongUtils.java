package com.example.demo.util;
/*
 * @author Daniel
 */

import java.util.Objects;

public class LongUtils {
    public static long getOrDefault(Long number, long defaultValue){
        return Objects.isNull(number)? defaultValue : number;
    }

}
