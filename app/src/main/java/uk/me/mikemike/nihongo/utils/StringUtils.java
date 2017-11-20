package uk.me.mikemike.nihongo.utils;

/**
 * Created by mike on 11/20/17.
 */

public final class StringUtils {

    public static boolean isEmptyOrNull(String value){
        if(value == null)return true;
        return value.isEmpty();
    }
}
