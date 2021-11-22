package com.example.diary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.example.diary.database.MyDB;
import com.example.diary.enity.Record;


@SuppressLint("Registered")
//登录
public class LoginActivity extends BaseActivity {
    private EditText author;        //定义作者
    private Button btn_login;      //定义登录按钮
    private SharedPreferences sp;  //声明SharedPreferences对象
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        author=findViewById(R.id.login_author);
        //获取作者
        btn_login=findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //登录按钮功能
                String set_author = author.getText().toString();  //得到作者
                sp=getSharedPreferences("text.txt", MODE_PRIVATE);
                //创建SharedPreferences对象，定义数据类型以及保存位文件 text不存在则创建新文件，默认操作模式
                //MODE_PRIVATE私有方式其他应用不能访问
                SharedPreferences.Editor editor=sp.edit();
                //创建SharedPreferences的编辑器 获取编辑对象
                editor.putString("author",set_author);
                //保存作者 键值对的方式存储数据
                String name=sp.getString("author",""); //获取作者名
               /* if(name==""){
                    editor.putString("author","张三");
                }*/
                if(author.getText().toString().equals("")){
                    set_author="张三";
                    editor.putString("author",set_author);
                }
                editor.commit();
                //调用commit方法将添加的数据提交  完成数据存储

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Record record = new Record();
                record.setAuthor(set_author);
                //传值到main界面 传递作者名字
                intent.putExtra(MyDB.RECORD_AUTHOR,record.getAuthor().trim());
                //跳转新建界面
                startActivity(intent);
                //可以将Intent对象传递给startActivity()方法或startActivityForResult()方法以启动一个Activity
                //该Intent对象包含了要启动的Activity的信息及其他必要的数据
                LoginActivity.this.finish();
                //在一个Activity用完之后应该将之finish掉
            }
        });

    }
}
