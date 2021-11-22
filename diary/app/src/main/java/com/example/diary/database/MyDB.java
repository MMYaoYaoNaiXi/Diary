package com.example.diary.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDB extends SQLiteOpenHelper {
    public final static String TABLE_NAME_RECORD = "record";
    //表名
    //定义各个属性
    public final static String RECORD_ID = "_id";
    public final static String RECORD_TITLE = "title_name";
    //日记标题
    public final static String RECORD_BODY = "text_body";
    //日记内容
    public final static String RECORD_AUTHOR = "text_author";
    //日记作者
    public final static String RECORD_TIME = "create_time";
    //日记创建时间
    public final static String RECORD_PATH="picture_path";
    private Context mContext;


    public MyDB(Context context) {
        //构造方法
        super(context, "record.db", null, 1);

        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表 表名TABLE_NAME_RECORD = record
        db.execSQL("CREATE TABLE "+TABLE_NAME_RECORD+" ("+RECORD_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                RECORD_TITLE+" VARCHAR(30)," +
                RECORD_BODY+" TEXT," +
                RECORD_AUTHOR+" TEXT,"+
                RECORD_TIME+" DATETIME NOT NULL," +
                RECORD_PATH+" VARCHAR(100))");
        Toast.makeText(mContext,"数据库创建成功",Toast.LENGTH_SHORT).show();
        //创建成功提示信息
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*db.execSQL("ALTER TABLE "+TABLE_NAME_RECORD+" ADD " +RECORD_PATH+" TEXT");
        onCreate(db);*/
    }
}