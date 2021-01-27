package com.cgq;

import java.io.*;
import java.util.ArrayList;

/**
 * @author cgq
 * @apiNote markdown文件内容的按行读取--注意：利用java的BufferedReade进行对文件读取时，
 *         首行的第一个字符无论怎样都是" "，所以需要单独解析
 */
public class MarkDownFileRead {
    /**
     * 按行读取markdown文件的内容，并存储在一个ArrayList中
     * @param filename 读取的文件的名字
     * @return 返回按行存储markdown文件内容的ArrayList
     */
    public static ArrayList<String> readMarkDownFileByRaw(String filename){
        //用来存储markdown文件内容的容器
        ArrayList<String> fileContent=new ArrayList<>();
        String str;
        int count=0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
            while((str=bufferedReader.readLine())!=null){
                if(count==0){
                    str=str.substring(1);
                    count++;
                }
                if(!str.equals("")){
                    fileContent.add(str);
                }else{
                    //为了保证分析的格式分析的正确进行，我们把所欲的空行即""都插入变为" "，从而让charAt(0)的判断正确进行
                    fileContent.add(" ");
                }

                //判断str到底属于什么类型的markdown格式的东西
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
}
