package com.example.diary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.diary.enity.Record;

import java.io.FileNotFoundException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import com.example.diary.database.MyDB;


public class AmendActivity extends BaseActivity implements View.OnClickListener{
    //修改界面
    private final static String TAG = "AmendActivity";

    MyDB myDB;

    private Button btnSave;        //确认
    private Button btnBack;       //返回
    private TextView amendTime;   //修改时间
    private TextView amendTitle;  //修改题目
    private EditText amendBody;  //修改内容
    private Record record;
    private AlertDialog.Builder dialog;
    private ImageView picture;
    private Uri imageUri;
    //弹出一个消息框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amend_linear_layout);
        init(); //初始化

    }

    @Override
    public void onClick(View v) {
        String body;
        body = amendBody.getText().toString();
        //修改内容
        switch (v.getId()){
            case R.id.button_save:  //保存
                if (updateFunction(body)){
                    intentStart();  //返回主界面
                }
                break;
            case R.id.button_back:
                showDialog(body);
                clearDialog();
                //清空
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //当返回按键被按下
            if (!isShowIng()){
                showDialog(amendBody.getText().toString());
                clearDialog();
                //清空
            }
        }
        return false;
    }

    /*
     * 初始化函数
     */
    @SuppressLint("SetTextI18n")
    void init(){

        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        amendTitle = findViewById(R.id.amend_title);
        amendBody = findViewById(R.id.amend_body);
        amendTime = findViewById(R.id.amend_title_time);
        picture=findViewById(R.id.amend_picture_show);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);


        Intent intent = this.getIntent();
        myDB = (MyDB) new com.example.diary.database.MyDB(this);
        if (intent!=null){
            record = new Record();
            record.setId(Integer.valueOf(intent.getStringExtra(MyDB.RECORD_ID)));  //接收主界面传递的参数
            record.setTitleName(intent.getStringExtra(MyDB.RECORD_TITLE));
            record.setAuthor(intent.getStringExtra(MyDB.RECORD_AUTHOR));
            record.setTextBody(intent.getStringExtra(MyDB.RECORD_BODY));
            record.setCreateTime(intent.getStringExtra(MyDB.RECORD_TIME));
            record.setPath(intent.getStringExtra(MyDB.RECORD_PATH));
            amendTitle.setText(record.getTitleName());
            String str="";
            amendTime.setText(record.getCreateTime()+str);
            amendBody.setText(record.getTextBody());//复显示内容
            imageUri=Uri.parse(record.getPath());
            //string转换成uri类型
            int flag=0;
            if(!record.getPath().equals("")){
                //图片路径不空
                try {
                    //拍照图片显示
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    picture.setImageBitmap(bitmap);
                }  catch (FileNotFoundException e) {
                    flag=1;
                    e.printStackTrace();
                }
                if(flag==1){
                    try{
                        //相册图片显示
                        Bitmap bitmap=BitmapFactory.decodeFile(record.getPath());
                        picture.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /* 返回主界面*/
    void intentStart(){
        Intent intent = new Intent(AmendActivity.this,MainActivity.class);
        intent.putExtra(MyDB.RECORD_AUTHOR,record.getAuthor().trim());
        //页面跳转
        startActivity(intent);
        this.finish();
        //关闭在一个Activity用完之后应该将之finish掉
    }

    /*保存函数*/
    boolean updateFunction(String body){
        //修改只有内容时间
        SQLiteDatabase db; //数据库
        ContentValues values; //参数对象

        boolean flag = true;
        if (body.length()>200){
            Toast.makeText(this,"内容过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(flag){
            // flag=true update
            db = myDB.getWritableDatabase();
            values = new ContentValues();
            values.put(MyDB.RECORD_BODY,body);
            values.put(MyDB.RECORD_TIME,getNowTime());
            db.update(MyDB.TABLE_NAME_RECORD,values,MyDB.RECORD_ID +"=?",
                    new String[]{record.getId().toString()});
            //更新数据库
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }

    /*弹窗函数
     * @param title
     * @param body
     * @param createDate
     */
    void showDialog(final String body){ //点击返回弹窗
        dialog = new AlertDialog.Builder(AmendActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateFunction(body);  //更新
                intentStart();  //跳转
                    }
                });

        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentStart();
                    }
                });
        dialog.show();
    }

    void clearDialog(){
        dialog = null;
    }

    boolean isShowIng(){
        if (dialog!=null){
            return true;
        }else{
            return false;
        }
    }

    /*
     * 得到当前时间
     * @return
     */
    String getNowTime(){
        @SuppressLint("SimpleDateFormat")  //当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

}
