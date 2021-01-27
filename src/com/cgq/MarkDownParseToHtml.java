package com.cgq;


import java.util.ArrayList;

/**
 * @author cgq
 * @apiNote 主要功能:实现了markdown解析为html的逻辑函数调用部分
 */
public class MarkDownParseToHtml {
    //用来存储markdown具体文件内容的容器--按行存储
    private static ArrayList<String> mdContent=new ArrayList<>();
    //用来存储markdown每一行对应的markdown标签是什么类型
    private static ArrayList<String> mdType=new ArrayList<>();
    public static void main(String []args){
        //得到文件的具体内容
        mdContent=MarkDownFileRead.readMarkDownFileByRaw("web\\readme.md");

        //开始第一次文件内容的扫描，也即是对代码块区域位置的确定，对标题、空行、分割线类型的确定
        MarkDownFileScan.CodeAreaParse(mdContent,mdType);
        //开始第二次文档内容的扫描，也即是对引用块、有序列表块、无序列表块的判断
        MarkDownFileScan.QuoteListTitleParse(mdContent,mdType);
        //开始第三次的文件扫描，将文件内容转换为html标签
        MarkDownFileScan.InLineElemParseAndConvertToHtml(mdContent,mdType);

        //最后一步：将转换后的代码生成为html文件,将文件输出到web目录下
        String filename="index.html";
        MarkDownFileScan.PrintToHtml(mdContent,filename);

    }
}
