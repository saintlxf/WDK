package pers.lxf.wdk.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class StringUtil {
    public static String dropLastComma(String str){
        return dropLastCharIfMatch(str,",");
    }

    public static String dropLastDot(String str){
        return dropLastCharIfMatch(str,".");
    }

    public static String dropLastCharIfMatch(String str,String lastChar){
        if(str==null){
            return null;
        }
        str = str.trim();
        return str.substring(str.length()-1).equals(lastChar)?str.substring(0,str.length()-1):str;
    }

    public static String dropLastChar(String str){
        if(str==null){
            return null;
        }
        str = str.trim();
        return str.substring(0,str.length()-1);
    }

    public static String firstLittle2UpCase(String str){
        char[] chars = str.toCharArray();

        //首字母小写方法，小写写会变成大写
        if(chars[0]>='a'&&chars[0]<='z')
            chars[0] -=32;
        return String.valueOf(chars);
    }

    public static String firstLittle2LowCase(String str){
        char[] chars = str.toCharArray();
        //首字母小写方法，大写会变成小写
        if(chars[0]>='A'&&chars[0]<='Z')
            chars[0] +=32;
        return String.valueOf(chars);
    }

    /**
     * 替换所有的下划线，并将单词首字母大写
     * @param str 需要处理的字符串
     * @return 新的结果字符串
     */
    public static String replaceAllDownLineAndUpCaseFirstLittle(String str){
        if(str.contains("_")){
            return replaceAllDownLineAndUpCaseFirstLittle(str.substring(0,str.indexOf("_"))+firstLittle2UpCase(str.substring(str.indexOf("_")+1)));
        }else{
            return str;
        }
    }

    public static String generateSameChar(int i,String str){
        String ret = "";
        for (int j = 0; j < i; j++) {
            ret +=str;
        }
        return ret;
    }

    public static boolean isEmpty(String str){
        if(str==null||str.trim().length()==0){
            return true;
        }
        return false;
    }

    public static boolean notEmpty(String str){
        if(str==null||str.trim().length()==0){
            return false;
        }
        return true;
    }

    public static boolean getKeyValueBoolean(String str,String key,boolean defaultValue){
        boolean ret = defaultValue;
        if(str != null&&str.startsWith(key)){
            ret = Boolean.valueOf(str.substring(key.length()+1));
        }
        return ret;
    }


    public static void main(String[] args) {
        //URL xmlpath = StringUtil.class.getClassLoader().getResource("/");
        //System.out.println(Boolean.valueOf("True"));
        String a = "abc:true";
        System.out.println(getKeyValueBoolean(a,"abc",false));

    }
}
