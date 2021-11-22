package com.example.diary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.diary.database.MyDB;
import com.example.diary.enity.Record;
import com.example.diary.util.DateFormatType;
import com.example.diary.util.MyFormat;
import com.example.diary.util.MyTimeGetter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import static com.example.diary.util.MyFormat.getTimeStr;
import static com.example.diary.util.MyFormat.myDateFormat;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditActivity extends BaseActivity implements View.OnClickListener{  //新建activity
    private final static String TAG = "EditActivity";

    MyDB myDB;    //定义数据库
    private Button btnSave;  //确认
    private Button btnBack;  //返回
    public static final int Take_Photo=1;
    private ImageView picture;//显示图片
    private Uri imageUri;
    private TextView editTime;//编辑时间
    private EditText editTitle;//编辑题目
    private EditText editBody;//内容
    private AlertDialog.Builder dialog;//提示框
    private SharedPreferences sp;//声明SharedPreferences对象
    private  String path="";

    private String createDate;//完整的创建时间，插入数据库
    private String dispCreateDate;//创建时间-显示变量可能会去除年份

    private Integer year;
    private Integer month;
    private Integer dayOfMonth;
    private Integer hour;
    private Integer minute;
    private boolean timeSetTag;
    public  String name;
    public static final int CHOOSE_PHOTO=2;
    private  int a=1;

    MyTimeGetter myTimeGetter;
    //获取时间对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_linear_layout);  //新建界面
        init();  //初始化
        Button takePhoto=(Button) findViewById(R.id.btn_edit_addpic);
        picture=(ImageView) findViewById(R.id.picture_show);
        Button chooseFromALbum=(Button) findViewById(R.id.btn_edit_selectpic);

        takePhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //创建File对象，用于存储拍照后的图片
                File outputImage=new File(getExternalCacheDir(),"output_image"+a+".jpg");
                //通过Context.getExternalCacheDir()方法可以获取到 SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
                a++;
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                        //如果已经存在，则删除
                    }
                    outputImage.createNewFile();
                    //创建一个新的
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    //版本号判断
                    imageUri= FileProvider.getUriForFile(EditActivity.this,"com.example.cameraalbumtest.fileprovider",outputImage);
                   // Log.w("EditActivity", imageUri.toString());
                }else{
                    imageUri=Uri.fromFile(outputImage);
                    //获取outputImage的Uri
                    //Log.w("EditActivity", imageUri.toString());
                    path=imageUri.toString();

                    //转换成String类型的path，方便存入数据库
                }
                //启动相机程序
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,Take_Photo);
                //启动
            }
        });

        chooseFromALbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                    //PackageManager.PERMISSION_GRANTED=0
                    ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
               //申请权限
                }
                else{
                    openAlbum();
                    //用户授权了申请
                }

            }
        });

    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        //startActivityForResult(intent,Take_Photo);回调
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Take_Photo:
                //Take_Photo=1 拍照
                if(resultCode==RESULT_OK){
                    //RESULT_OK=-1
                    try{
                        //将拍摄的照片显示出来
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                //选择系统照片
                if(resultCode==RESULT_OK){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                        //处理照片  22 4.4以上，选取相册中的图片不再返回图片真实的URi了，而是封装过的Uri
                    }
                    else{
                        handleImageBeforeKitKat(data);
                        //4.4以下
                    }
                }
                break;
            default:
                break;
        }
    }
    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
        //打开相册
    }
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //授权允许   如果被拒绝，grantResults为空
                    openAlbum();
                }
                else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                    //运行时权限处理，动态申请WRITE_EXTERNAL——STORAGE 没有权限
                }
                break;
            default:
        }
    }
    private void handleImageOnKitKat(Intent data){
        //解析封装过的Uri
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的uri，则通过document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                //如果Uri的authority是media格式
                String id=docId.split(":")[1];
                //解析处数字格式的id
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.provides.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的uri直接获取图片路径
            imagePath=uri.getPath();
        }
        path=imagePath.toString();
        displayImage(imagePath);
        //显示在界面上
    }
    private void handleImageBeforeKitKat(Intent data){
        //Uri没有封装过，不需要解析
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        //Uri传入getImagePath，获取真实路径
        path=imagePath.toString();
        displayImage(imagePath);
    }
    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection){
        String Path=null;
        //通过uri和selection来获取正式的图片路径
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                Path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return Path;
    }
    private void displayImage(String imagePath){
        //显示图片
        if (imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }
        else{
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();;
        }
    }

    /*
     * 初始化函数
     */
    void init(){
        myDB = new MyDB(this);
        btnBack = findViewById(R.id.button_back);
        btnSave = findViewById(R.id.button_save);
        editTitle = findViewById(R.id.edit_title);
        editBody = findViewById(R.id.edit_body);
        editTime = findViewById(R.id.edit_title_time);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        Date date = new Date(System.currentTimeMillis());
        //获取当前时间
        createDate = myDateFormat(date, DateFormatType.NORMAL_TIME);
        //调用MyFormat类的myDateFormat方法 格式化时间
        dispCreateDate = getTimeStr(date);
        //获取创建时间 如果是当前年份，去除年份
        hour = 0;
        minute = 0;
        year = 0;
        month = 0;
        dayOfMonth = 0;
        timeSetTag = false;
        //初始化为 未设置提醒
    }

    /*返回键监听，消除误操作BUG*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String title;//标题
            String body;//内容
            String createDate;//创建日期
            title = editTitle.getText().toString();
            body = editBody.getText().toString();
            createDate = editTime.getText().toString();
            //当返回按键被按下
            if (!isShowIng()){
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body,createDate);
                    clearDialog();
                    //没填信息时，清空弹窗
                } else {
                    intentStart();  //
                }
            }
        }
        return false;
    }

    /*按钮点击事件监听*/
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        String title;
        String body;
        //获取输入
        title = editTitle.getText().toString();
        body = editBody.getText().toString();

        Intent intent = this.getIntent();
        Record record1=new Record();
        record1.setAuthor(intent.getStringExtra(MyDB.RECORD_AUTHOR));
        name=record1.getAuthor();
        switch (v.getId()){
            case R.id.button_save:
                //确认
                if (saveFunction(title,body,createDate,path)){
                    //保存信息
                    intentStart();
                    //跳回主界面
                }
                break;
            case R.id.button_back:
                //返回
                if (!"".equals(title)||!"".equals(body)){
                    showDialog(title,body,createDate);
                    clearDialog();  //没填信息时，清空弹窗
                } else {
                    intentStart();  //有信息，保存
                }
                break;
            default:
                break;
        }
    }

    /*返回主界面*/
    void intentStart(){
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        //跳转主界面
        Record record = new Record();
        record.setAuthor(name);
        //传值到main界面 传递作者名字
        intent.putExtra(MyDB.RECORD_AUTHOR,record.getAuthor().trim());
        startActivity(intent);
        this.finish();
        //在一个Activity用完之后应该将之finish掉
    }

    /*备忘录保存函数*/
    boolean saveFunction(String title,String body,String createDate,String path){ //保存判定

        boolean flag = true;
        if ("".equals(title)){
            Toast.makeText(this,"标题不能为空",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (title.length()>10){
            Toast.makeText(this,"标题过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if (body.length()>200){
            Toast.makeText(this,"内容过长",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if ("".equals(createDate)){
            Toast.makeText(this,"时间格式错误",Toast.LENGTH_SHORT).show();
            flag = false;
        }
        if(flag){
            //true
            sp=getSharedPreferences("text.txt",MODE_PRIVATE);
            String authors = sp.getString("author","王婧琪");
            SQLiteDatabase db;
            ContentValues values;  //保存数据对象
            //  存储备忘录信息 只能基本类型
            db = myDB.getWritableDatabase();
            //以写方式打开数据库
            values = new ContentValues();
            values.put(MyDB.RECORD_TITLE,title);
            values.put(MyDB.RECORD_BODY,body);
            values.put(MyDB.RECORD_AUTHOR,authors);
            values.put(MyDB.RECORD_TIME,createDate);
            values.put(MyDB.RECORD_PATH,path);


            db.insert(MyDB.TABLE_NAME_RECORD,null,values);
            //存入数据库
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
            db.close();
        }
        return flag;
    }

    /*弹窗函数*/
    void showDialog(final String title, final String body, final String createDate){
        //点击返回时提示弹框
        dialog = new AlertDialog.Builder(EditActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("是否保存当前编辑内容");
        dialog.setPositiveButton("保存",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveFunction(title, body, createDate,path);  //保存参数
                intentStart();  //跳转
                    }
                });

        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentStart();//跳转
                    }
                });
        dialog.show();
    }

    /*
     *  清空弹窗
     */
    void clearDialog(){
        dialog = null;
    }

    /*
     *  判断是否弹窗是否显示
     */
    boolean isShowIng(){
        if (dialog!=null){
            return true;
        }else{
            return false;
        }
    }


}
