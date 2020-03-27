package com.zy.myblog.xx.utils;

import java.util.*;

/**
 * List工具类
 * @author zy 1716457206@qq.com
 */
public class ListUtils {
    /*取两个list的差集*/
    public static List<String> receiveDefectList(List<String> firstArrayList, List<String> secondArrayList) {
        List<String> resultList = new ArrayList<String>();
        LinkedList<String> result = new LinkedList<String>(firstArrayList);// 大集合用 linkedlist 删除值得
        HashSet<String> othHash = new HashSet<String>(secondArrayList);// 小集合用hashset 包含的
        Iterator<String> iter = result.iterator();// 采用Iterator迭代器进行数据的操作
        while(iter.hasNext()){
            if(othHash.contains(iter.next())){
                iter.remove();
            }
        }
        resultList = new ArrayList<String>(result);
        return resultList;
    }
}
