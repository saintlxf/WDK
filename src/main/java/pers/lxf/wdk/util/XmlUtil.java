package pers.lxf.wdk.util;

/**
 * xml 工具类，简单处理xml
 */
public class XmlUtil {
    // 根据字符串，判断开始为<,并得到其后的字符串，作为节点名返回
    public static String getXmlElementName(String str){
        String strTmp = str.trim();
        int start = strTmp.indexOf("<");
        if(start==0){
            return str.trim().substring(str.indexOf("<")+1,strTmp.indexOf(" "));
        }else{
            return null; // 返回null，表明该字符串不是节点，即不以“<”开头
        }
    }

    /**
     *
     * @param str 需要填充的字符串
     * @param fillContent 填充的内容
     * @return 填充后返回拼接好的字符串
     */
    public static String fillPlaceHolder(String str,String fillContent){
        String ret = null;
        int index = str.indexOf("?");
        if(index==-1){
            return str; // 没有找到? 返回原字符串
        }
        ret = str.substring(0,index)+fillContent+str.substring(index+1);
        return ret;
    }
}
