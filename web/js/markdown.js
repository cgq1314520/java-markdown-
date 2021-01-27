 $(function(){
             //函数一：功能：为自定义的pre生成行号，使得其成为一个代码块，原理：通过添加一个div来模拟行号
             //获取网页中所有pre元素内容并用\n划分,最终再利用给左侧浮动的div中添加html形成行号，从而形成类似于代码行的作用
             var arr = $("pre").html().split("\n");
             var text = "";
             for (var i = 0; i < arr.length; i++) {
              if(i==arr.length-1){
                  text+=(i+1);
              }
              else{
                text+=(i+1)+"<br/>";
              }
            }
             
            $(".left-raw").html(text);
            //以上代码为给代码表上行数
        });