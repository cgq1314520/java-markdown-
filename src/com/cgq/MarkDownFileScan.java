package com.cgq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author cgq
 * @apiNote 对于markdown文件内容的扫描程序，其主要分为三个部分，第一个部分即第一次扫描，即扫描代码块的区域
 */
public class MarkDownFileScan {
    /*--------定义下面的标记用来标记每一行内容的具体类型--------------*/
    /*---------------第一部分：代码块对应的类型标志------------------*/
    //用来标记代码区开始的类型，对应着```
    protected static final String CODE_ENTER="code-enter";
    //用来标记代码区结束的类型，对应着```
    protected static final String CODE_EXIT="code-exit";
    //用来标记代码区的代码行的类型，对应着``` ```之间的内容
    protected static final String CODE_RAW="code-raw";

    /*---------------第二部分：引用块对应的类型标志------------------*/
    //用来标记引用区开始的类型，对应着>（前一行的首部为非>,下一行的首部为>，则代表着是引用区的开始）
    protected static final String QUOTE_ENTER="quote-enter";
    //用来标记引用区退出的类型，对应着>(前一行的首部为>,下一行的首部非>，则代表着引用区的结束)
    protected static final String QUOTE_EXIT="quote-exit";
    //用来标记引用区的引用行，对应着>
    protected static final String QUOTE_RAW="quote-raw";
    //用来标记单行引用的区
    protected static final String QUOTE_SINGLE="quote-single";

    /*---------------第三部分：无序列表区对应的类型标志------------------*/
    //用来标记无序列表区开始的类型，对应着-
    protected static final String UNORDERED_LIST_ENTER="unordered-list-enter";
    //用来标记序列表区退出的类型，对应着-(前一行的首部为-,下一行的首部非-，则代表着无序列表区的结束)
    protected static final String UNORDERED_LIST_EXIT="unordered-list-exit";
    //用来标记引用区的引用行，对应着-
    protected static final String UNORDERED_LIST_RAW="unordered-list-raw";
    //单行无序列表的标志
    protected static final String UNORDERED_LIST_SINGLE="unordered-list-single";

    /*---------------第四部分：有序列表区对应的类型标志------------------*/
    //用来标记无序列表区开始的类型，对应着1-9
    protected static final String ORDERED_LIST_ENTER="ordered-list-enter";
    //用来标记序列表区退出的类型，对应着1-9(前一行的首部为1-9,下一行的首部非1-9，则代表着有序列表区的结束)
    protected static final String ORDERED_LIST_EXIT="ordered-list-exit";
    //用来标记引用区的引用行，对应着1-9
    protected static final String ORDERED_LIST_RAW="ordered-list-raw";
    //单行有序列表的标志
    protected static final String ORDERED_LIST_SINGLE="ordered-list-single";

    /*--------------------第五部分：标题对应的类型标志-------------------*/
    //用来标记此行是否是标题，是占一行的元素
    protected static final String TITLE="title";

    /*--------------------第六部分：空行对应的类型标志-------------------*/
    //用来标记此行是否是空行，是占一行的元素
    protected static final String BLANK_RAW="blank-raw";

    /*----------------第七部分：水平分割线对应的类型标志------------------*/
    //用来标记此行是否是分割线，是占一行的元素
    protected static final String HR="hr";

    /*----------------第八部分：不符合以上类型的其他类型标志------------------*/
    protected static final String NONE="none";

    /*对于哪些行内的元素，我们将在最后一步直接解析判断生成其对应的html，在此不设置类型标志*/

    /**
     * 功能：markdown文件解析的第一部分：实现对于markdown文档中代码区域的定位，即判断代码区的开始和结束位置，
     *       然后利用mdType来记录每一行文件的类型(用上面的标记变量来记录)，从而得到对应mdContent所在行的类型
     *       同时在此过程中判断空行以及水平分割线这两个独占一行且较好判断的类型
     * @param mdContent 存储着每一行markdown文件内容的容器
     * @param mdType  用来存储mdContent每一行代码对应markdown标签类型的容器
     */
    public static void CodeAreaParse(ArrayList<String> mdContent,ArrayList<String> mdType){
        //定义两个变量用来标志代码区是否已经进入或退出
        boolean code_enter=false;
        boolean code_exit=false;
        //开始逐行遍历，从每一行的头部找```，并用一个标记变量来记录是否已经进入了代码区,只要不是代码区的都设置为OTHER类型
        for (int i=0;i<mdContent.size();i++) {
            String s=mdContent.get(i);
            //当找到了一个符合首部为```的一行内容，则说明开始进入了代码区或者退出代码区
            if(s.length()>2&&s.startsWith("```")){
                //也即还没有进入代码区也没有处于代码区中
                if(!code_enter&&!code_exit){
                    //将文件内容变为" "
                    mdContent.set(i," ");
                    //设置对应该行的类型为刚刚进入代码区
                    mdType.add(CODE_ENTER);
                    //设置code_enter标志变量为true，代表下面的是代码区
                    code_enter=true;
                }
                //也即当前正处于代码退出的位置，所以将该处的文件内容也改为" ",该行的类型变为CODE_EXIT
                else if(code_enter&&!code_exit){
                    mdContent.set(i," ");
                    mdType.add(CODE_EXIT);
                    //让退出的标记变量变为false，代表代码块已经退出
                    //让二者都变为false，为遇到下一个代码块做准备
                    code_exit=false;
                    code_enter=false;
                }
                else{  //这儿都不会进来
                    mdType.add(CODE_RAW);
                }
            }
            //也即如果此时是进入了代码区，还没有退出代码区，那么这些都是代码区域，所以设置类型为CODE_RAW
            else if(code_enter&&!code_exit){
                mdType.add(CODE_RAW);
            }
            else{ //在此过程中判断空行的类型,空行在读入时都变为了" "，其他非代码区的都设置为OTHER类型
                if(s.equals(" ")){   //如果是空行，则将类型添加
                    mdType.add(BLANK_RAW);
                }
                else if(s.equals("------")){  //判断是否是水平分割线，如果是添加其类型
                    mdType.add(HR);
                }
                else if(s.startsWith("#")){  //判断是否是标题，如果是添加其类型,具体是几号标题在具体生成代码时进行判断
                    mdType.add(TITLE);
                }
                else{
                    mdType.add(NONE);
                }
            }
        }
    }

    /**
     * 功能：针对已经被上一个函数解析过的内容，进行对于引用快、无序列表、有序列表部分的解析
     * @param mdContent 待解析的markdown文件内容（按行存储）
     * @param mdType 解析后每一行类型的存储容器
     */
    public static void QuoteListTitleParse(ArrayList<String> mdContent,ArrayList<String> mdType){
        //第一步：解析引用块部分，分为单行和多行,其实就是判断首位是不是>,且代码行中出现的>不用解析
        //       所以，我们应该设置一个标志变量，用来标志当前的位置是否是代码区
        boolean isCodeArea =false;
        //开始逐行判断是否是引用区，由于涉及到前后两行才能进行判断区域，所以以下循环
        for (int i = 1; i < mdContent.size()-1; i++) {
            //得到当前及相邻两行，从而更好的判断区域
            String now=mdContent.get(i);
            String last=mdContent.get(i-1);
            String next=mdContent.get(i+1);
            //先判断是否是代码区，如果是代码区，则直接跳过
            if(mdType.get(i).equals(CODE_ENTER)){
                isCodeArea=true;
                continue;
            }
            if(mdType.get(i).equals(CODE_EXIT)){
                isCodeArea=false;
                continue;
            }
            //只有当不是代码区时，才判断当前的行是不是引用区或者其他区
            if(!isCodeArea){
                //判断是否是多行引用区的入口
                if(now.length()>0&&now.startsWith(">")&&!last.startsWith(">")&&next.startsWith(">")){
                    //说明该行是多行引用区的入口，所以改变其类型
                    mdType.set(i,QUOTE_ENTER);
                }
                //判断是否离开了多行引用区
                else if(now.length()>0&&now.startsWith(">")&&last.startsWith(">")&&!next.startsWith(">")){
                    //说明该行是多行引用区的终点，所以改变其类型
                    mdType.set(i,QUOTE_EXIT);
                }
                //当前行是引用行
                else if(now.length()>0&&now.startsWith(">")&&last.startsWith(">")&&next.startsWith(">")){
                    mdType.set(i,QUOTE_RAW);
                }
                //判断当前是否是单行引用区
                else if(now.length()>0&&now.startsWith(">")&&!last.startsWith(">")&&!next.startsWith(">")){
                    mdType.set(i,QUOTE_SINGLE);
                }
                //判断是否是多行无序列表区的入口
                else if(now.startsWith("-")&&!now.equals("------")&&!last.startsWith("-")&&next.startsWith("-")){
                    mdType.set(i,UNORDERED_LIST_ENTER);
                }
                //判断是否是多行无序列表区的出口
                else if(now.startsWith("-")&&!now.equals("------")&&last.startsWith("-")&&!next.startsWith("-")){
                    mdType.set(i,UNORDERED_LIST_EXIT);
                }
                //判断是否是多行无序列表的内容
                else if(now.startsWith("-")&&!now.equals("------")&&last.startsWith("-")&&next.startsWith("-")){
                    mdType.set(i,UNORDERED_LIST_RAW);
                }
                //判断是否是单行无序列表的内容
                else if(now.startsWith("-")&&!now.equals("------")&&!last.startsWith("-")&&!next.startsWith("-")){
                    mdType.set(i,UNORDERED_LIST_SINGLE);
                }
                //判断是否是多行有序列表的入口,最多9行,从1.到9.
                else if((now.length()>1&&now.charAt(0)>='1'&&now.charAt(0)<='9'&&now.charAt(1)=='.')&&
                        !(last.length()>1&&last.charAt(0)>='1'&&last.charAt(0)<='9'&&last.charAt(1)=='.')&&
                        (next.length()>1&&next.charAt(0)>='1'&&next.charAt(0)<='9'&&next.charAt(1)=='.')){
                        mdType.set(i,ORDERED_LIST_ENTER);
                }
                //判断是否是多行有序列表的出口
                else if((now.length()>1&&now.charAt(0)>='1'&&now.charAt(0)<='9'&&now.charAt(1)=='.')&&
                        (last.length()>1&&last.charAt(0)>='1'&&last.charAt(0)<='9'&&last.charAt(1)=='.')&&
                        !(next.length()>1&&next.charAt(0)>='1'&&next.charAt(0)<='9'&&next.charAt(1)=='.')){
                    mdType.set(i,ORDERED_LIST_EXIT);
                }
                //判断是否是多行有序列表的行
                else if((now.length()>1&&now.charAt(0)>='1'&&now.charAt(0)<='9'&&now.charAt(1)=='.')&&
                        (last.length()>1&&last.charAt(0)>='1'&&last.charAt(0)<='9'&&last.charAt(1)=='.')&&
                        (next.length()>1&&next.charAt(0)>='1'&&next.charAt(0)<='9'&&next.charAt(1)=='.')){
                    mdType.set(i,ORDERED_LIST_RAW);
                }
                //判断是否是单行有序列表
                else if((now.length()>1&&now.charAt(0)>='1'&&now.charAt(0)<='9'&&now.charAt(1)=='.')&&
                        !(last.length()>1&&last.charAt(0)>='1'&&last.charAt(0)<='9'&&last.charAt(1)=='.')&&
                        !(next.length()>1&&next.charAt(0)>='1'&&next.charAt(0)<='9'&&next.charAt(1)=='.')){
                    mdType.set(i,ORDERED_LIST_ENTER);
                }
            }
        }
    }

    /**
     * 功能：在以上两个解析后的内容，针对于行内元素的解析，并生成对应的html
     * @param mdContent 待解析的markdown文件内容
     * @param mdType 当前每一行的类型
     */
    public static void InLineElemParseAndConvertToHtml(ArrayList<String> mdContent, ArrayList<String> mdType){
        for (int i = 0; i < mdContent.size(); i++) {
            String now=mdContent.get(i);
            String nowType=mdType.get(i);
            //如果该行是标题，则进行标题html的生成，同时对其行内元素进行解析
            if(nowType.equals(TITLE)){
                //声明一个变量用来记录标题是<hi>
                int count=0;
                for (int j = 0; j < now.length(); j++) {
                    if(now.charAt(j)=='#'){
                        count++;
                    }
                    else break;
                }
                //生成该标题对应的html,并解析该行的行内元素
                mdContent.set(i,"<h"+count+">"+InLineElenParse(now.substring(count).trim())+"</h"+count+">");
            }
            //如果该行类型为空行，则将该行的内容设置为<br>,但由于br本身高度太高，所以在此利用div设置好高度来替换换行
            if(nowType.equals(BLANK_RAW)){
                mdContent.set(i,"<div style=\"height: 5px;\"></div>");
            }
            //如果该行是代码区入口，则按照下面进行解决,写入class的目的是为了让显示更加好看,<xml></xmp>标签一包围，html就不会解析这个内容了
            if(nowType.equals(CODE_ENTER)){
                mdContent.set(i,"<div style=\"height: 5px;\"></div>");
                mdContent.set(i+1,"<pre><div class=\"left-raw\" ></div><div class=\"code-content\"><xmp style=\"margin:0 0;\">"+mdContent.get(i+1));
            }
            //如果该行是代码行，则直接写入即可,即不用管
            if(nowType.equals(CODE_RAW)){}
            //如果该行是代码行出口，则写入固定格式的代码
            if(nowType.equals(CODE_EXIT)){
                mdContent.set(i-1,mdContent.get(i-1)+"</xmp></div></pre>");
                mdContent.set(i,"<div style=\"height: 5px;\"></div>");
            }
            //如果该行是多行无序列表行，则进行如下解析
            if(nowType.equals(UNORDERED_LIST_ENTER)){
                mdContent.set(i,"<ul><li>"+InLineElenParse(now.substring(1).trim())+"</li>");
            }
            if(nowType.equals(UNORDERED_LIST_EXIT)){
                mdContent.set(i,"<li>"+InLineElenParse(now.substring(1).trim())+"</li></ul>");
            }
            if(nowType.equals(UNORDERED_LIST_RAW)){
                mdContent.set(i,"<li>"+InLineElenParse(now.substring(1).trim())+"</li>");
            }
            if(nowType.equals(UNORDERED_LIST_SINGLE)){
                mdContent.set(i,"<ul><li>"+InLineElenParse(now.substring(1).trim())+"</li></ul>");
            }
            //有序列表区的判断
            if(nowType.equals(ORDERED_LIST_ENTER)){
                mdContent.set(i,"<ol><li>"+InLineElenParse(now.substring(2).trim())+"</li>");
            }
            if(nowType.equals(ORDERED_LIST_EXIT)){
                mdContent.set(i,"<li>"+InLineElenParse(now.substring(2).trim())+"</li></ol>");
            }
            if(nowType.equals(ORDERED_LIST_RAW)){
                mdContent.set(i,"<li>"+InLineElenParse(now.substring(2).trim())+"</li>");
            }
            if(nowType.equals(ORDERED_LIST_SINGLE)){
                mdContent.set(i,"<ol><li>"+InLineElenParse(now.substring(2).trim())+"</li></ol>");
            }
            //多行的引用行区的处理,对应代码中的左侧边框为绿色的部分html代码
            if(nowType.equals(QUOTE_ENTER)){
                mdContent.set(i,"<p class=\"left-green-title\">"+InLineElenParse(now.substring(1).trim()));
            }
            if(nowType.equals(QUOTE_EXIT)){
                mdContent.set(i,InLineElenParse(now.substring(1).trim())+"</p>");
            }
            if(nowType.equals(QUOTE_RAW)){
                mdContent.set(i,InLineElenParse(now.substring(1).trim()));
            }
            if(nowType.equals(QUOTE_SINGLE)){
                mdContent.set(i,"<p class=\"left-green-title\">"+InLineElenParse(now.substring(1).trim()+"</p>"));
            }
            if(nowType.equals(HR)){
                mdContent.set(i,"<hr/>");
            }
            if(nowType.equals(NONE)){
                mdContent.set(i,"<p>" + InLineElenParse(now.trim()) + "</p>");
            }

        }
    }

    /**
     * 功能：解析给定的行,判断其中是否含有行内元素，如果有则挑出来并解析，也即处理行内的
     *      图片、粗体、斜体、高亮、行内引用、图片、链接、删除线这几种标签
     * @param content 待解析的行内内容
     * @return 解析后的行内元素，此时这些行内元素已经被转换为html了
     */
    public static String InLineElenParse(String content){
        for (int i = 0; i < content.length(); i++) {
            //第一步：解析看行内是否有图片，即是否存在![]()这种格式,长度为5，所以i到line.length()-4就行了
            if(i<content.length()-4&&content.charAt(i)=='!'&&content.charAt(i+1)=='['){
                //开始找在i+1位置之后第一次出现]的位置
                int index=content.indexOf(']',i+1);
                //如果找到了，则index位置之后的下一个位置应该为(，才符合markdown中图片的格式,同时在(之后应该有)才行
                if(index!=-1&&content.charAt(index+1)=='('&&content.indexOf(')',index+2)!=-1){
                    //到此，说明存在图片的行内元素，开始解析为html
                    int index1=content.indexOf(')',index+2);
                    String imgAlt=content.substring(i+2,index);
                    String imgSrc=content.substring(index+2,index1);
                    //开始替换其中的markdown格式的图片标签为html格式的图片标签
                    content=content.replace(content.substring(i,index1+1),"<img src=\""+imgSrc+"\" alt=\""+imgAlt+"\" class=\"img-content\" />");
                }
            }
            //解析超链接，markdown中超链接格式为[]()，解决思路和上面一样
            if(i<content.length()-3&&(content.charAt(0)=='[')||(i>0&&content.charAt(i)=='['&&content.charAt(i-1)!='!')){
                //开始找在i+1位置之后第一次出现]的位置
                int index=content.indexOf(']',i+1);
                //如果找到了，则index位置之后的下一个位置应该为(，才符合markdown中图片的格式,同时在(之后应该有)才行
                if(index!=-1&&content.charAt(index+1)=='('&&content.indexOf(')',index+2)!=-1){
                    //到此，说明存在超链接这一个行内元素，开始解析为html
                    int index1=content.indexOf(')',index+2);
                    String linkCont=content.substring(i+1,index);
                    String linkHref=content.substring(index+2,index1);
                    //开始替换其中的markdown格式的图片标签为html格式的图片标签
                    content=content.replace(content.substring(i,index1+1),"<a href=\""+linkHref+"\">"+linkCont+"</a>");
                }
            }
            // 解析粗体** **，判断行内是否有标注为粗体的部分
            if(i < content.length() - 3 && content.charAt(i) == '*' && content.charAt(i + 1) == '*') {
                int index = content.indexOf("**", i + 1);
                if(index != -1) {
                    String cont = content.substring(i + 2, index );
                    content = content.replace(content.substring(i, index + 2), "<b>" + cont + "</b>");
                }
            }
            // 解析斜体* *，判断行内是否有标注为斜体的部分
            if(i < content.length() - 2 && content.charAt(i) == '*' && content.charAt(i + 1) != '*') {
                int index = content.indexOf('*', i + 1);
                if(index != -1 && content.charAt(index + 1) != '*') {
                    String cont = content.substring(i + 1, index);
                    content = content.replace(content.substring(i, index + 1), "<i>" + cont + "</i>");
                }
            }
            //解析单行引用` `
            if(i < content.length() - 2 && content.charAt(i) == '`' && content.charAt(i + 1) != '`') {
                int index = content.indexOf('`', i + 1);
                if(index != -1 && content.charAt(index + 1) != '`') {
                    String cont = content.substring(i + 1, index);
                    content = content.replace(content.substring(i, index + 1), "<span class=\"back-green-radius-content\">" + cont + "</span>");
                }
            }
            //解析删除线~~ ~~
            if(i < content.length() - 3 && content.charAt(i) == '~' && content.charAt(i + 1) == '~') {
                int index = content.indexOf("~~", i + 1);
                if(index != -1) {
                    String cont = content.substring(i + 2, index );
                    content = content.replace(content.substring(i, index + 2), "<s>" + cont + "</s>");
                }
            }
            //解析高亮模块== ==，可以跨越多行
            if(i < content.length() - 3 && content.charAt(i) == '=' && content.charAt(i + 1) == '=') {
                int index = content.indexOf("==", i + 1);
                if(index != -1) {
                    String cont = content.substring(i + 2, index );
                    content = content.replace(content.substring(i, index + 2), "<span class=\"back-yellow-content\">" + cont + "</span>");
                }
            }
        }
        return content;
    }

    /**
     * 在web目录下生成一个转换后的html文档
     * @param mdContent 存储转换后html的容器
     * @param filename 生成的文件名，注意传入的只是文件名，最终我们将在web目录下生成该文件
     */
    public static void PrintToHtml(ArrayList<String> mdContent, String filename) {
        File file=new File("web//"+filename);
        if(!file.exists()){
            try {
                file.createNewFile();
                //然后开始逐行写入这些内容，先写前面固定的
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write("<!DOCTYPE html>\r\n".getBytes());
                fileOutputStream.write("<html lang=\"zh-CN\">\r\n".getBytes());
                fileOutputStream.write("<head>\r\n".getBytes());
                fileOutputStream.write("<script src=\"js/jquery.min.js\"></script>\r\n".getBytes());
                fileOutputStream.write("<script src=\"js/markdown.js\"></script>\r\n".getBytes());
                fileOutputStream.write("<link rel=\"shortcut icon\" href=\"image/favicon.ico\" type=\"image/x-icon\">\r\n".getBytes());
                fileOutputStream.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/markdown.css\" />\r\n".getBytes());
                fileOutputStream.write("<title>Parse</title>\r\n".getBytes());
                fileOutputStream.write("</head>\r\n".getBytes());
                fileOutputStream.write("<body>\r\n".getBytes());
                fileOutputStream.write("<div class=\"main-content\">\r\n".getBytes());
                //开始循环写解析后的内容
                for (int i = 0; i < mdContent.size(); i++) {
                    fileOutputStream.write((mdContent.get(i)+"\r\n").getBytes());
                }
                //开始写末尾
                fileOutputStream.write("</div>\r\n".getBytes());
                fileOutputStream.write("</body>\r\n".getBytes());
                fileOutputStream.write("</html>\r\n".getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
