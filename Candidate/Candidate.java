package com.liheng.Update;

import java.util.List;

public class Candidate{
    //标记此项集在几个事务中出现过
    int count;
    //在该项集创建之前读取的事务数
    int startNum;
    //此条项集创建时遍历数据库的次数
    int dbRun;
    //阶数
    int itemsetNum;
    //用字符串类型来标记项集中的每一项 如'1 2' '1 44 67'
    List<String> itemset;
    boolean circle;
    boolean solid;

    public Candidate(int count, List<String> itemset, int dbRun, int startNum, int itemsetNum)
    {
        //assign values to the Candidate member variables
        this.count = count;
        this.itemset = itemset;
        //标记为圆虚线
        this.circle = true;
        this.solid = false;
        this.startNum = startNum;
        this.dbRun = dbRun;
        this.itemsetNum = itemsetNum;
    }
    
    public List<String> getItemSet(){
		return itemset;
    }
}
