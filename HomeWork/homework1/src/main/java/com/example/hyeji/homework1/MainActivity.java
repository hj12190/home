package com.example.hyeji.homework1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.preference.DialogPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btnWrite;
    TextView textView1;
    EditText edtDiary;

    int cYear, cMonth, cDay;
    String fileName;
    String mpath;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("diary");

        btnWrite = (Button) findViewById(R.id.btn1);
        textView1 = (TextView) findViewById(R.id.text1);
        edtDiary = (EditText) findViewById(R.id.edt1);

        //현재 날짜를 가져오기 위한 선언
        Calendar c = Calendar.getInstance();
        cYear = c.get(Calendar.YEAR);
        cMonth = c.get(Calendar.MONTH);
        cDay = c.get(Calendar.DAY_OF_MONTH);

        //textview에 오늘 날짜 불러오기
        textView1.setText(" " + c.get(Calendar.YEAR) + "년 " +
                +(c.get(Calendar.MONTH) + 1) + "월 " +
                c.get(Calendar.DAY_OF_MONTH) + "일 ");

        //오늘 일기 있으면 불러오기
        WriteDiary(cYear, cMonth, cDay);

        //sd카드 경로 설정
        mpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //sd카드 폴더 생성 및 경로 지정
        sdFile();

        //textView 클릭시 datepicker 다이얼로그 생성
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MainActivity.this, dateSetListener, cYear, cMonth, cDay).show();
            }
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    cYear = year;
                    cMonth = month;
                    cDay = day;
                    textView1.setText(String.format(cYear + "년 " + (cMonth + 1) + "월 " + cDay + "일 "));

                    WriteDiary(cYear, cMonth, cDay);
                }
            };
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                sdFile();

                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    String str = edtDiary.getText().toString();
                    fos.write(str.getBytes());
                    fos.close();
                    Toast.makeText(getApplicationContext(), fileName + " 이 저장됨", Toast.LENGTH_SHORT).show();
                }catch (IOException e) {
                }
            }
        });
    }

    //일기가 있으면 반환하고, 없으면 "일기없음"이라 띄우기.
    String readDiary(String fName) {
        String diaryStr = null;
        try {
            FileInputStream inFs =new FileInputStream(mpath+"/mydiary/" + fileName);
            byte[] txt = new byte[inFs.available()];
            inFs.read(txt);
            inFs.close();
            diaryStr = (new String(txt));

        } catch (IOException e) {
            edtDiary.setHint("일기 없음");
        }
        return diaryStr;
    }


    //sd카드 경로 지정 및 폴더 생성
    void sdFile() {

        String ext = Environment.getExternalStorageState();

        //경로 탐색
        if(ext.equals(Environment.MEDIA_MOUNTED))
            mpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        else
            mpath = Environment.MEDIA_UNMOUNTED;

        //파일 경로 지정
        file = new File(mpath + "/mydiary");

        //폴더가 없으면 폴더 생성
        if(!file.exists())
            file.mkdir();

        file = new File(mpath + "/mydiary/"+ fileName);
    }

    //일기 작성
    void WriteDiary(int year, int month, int day) {
        fileName = Integer.toString(year) + "_" + Integer.toString(month + 1) + "_"
                + Integer.toString(day) + ".txt";
        String str = readDiary(fileName);
        edtDiary.setText(str);
        btnWrite.setEnabled(true);
    }

    //일기 삭제 다이얼로그
    void diaryDelete(int year, int month, int day) {
        new AlertDialog.Builder(this)
                .setMessage(""+ year + "년 " + (month+1) + "월 " + day + "일 일기를 삭제하시겠습니까?")
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //일기 삭제

                                file.delete();

                                //일기 삭제 시 토스트
                                Toast.makeText(getApplicationContext(),"일기가 삭제되었습니다.", Toast.LENGTH_SHORT ).show();
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // 취소 버튼 누르면 아무 작업도 하지 않음.
                            }
                        }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "다시 불러오기");
        menu.add(0, 2, 0, "일기 삭제");

        SubMenu sMenu = menu.addSubMenu("글씨 크기 >> ");
        sMenu.add(0,4,0,"크게");
        sMenu.add(0,5,0,"보통");
        sMenu.add(0,6,0,"작게");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            //다시불러오기
            case 1:
                readDiary(fileName);
                return true;
            //일기삭제
            case 2:
                diaryDelete(cYear,cMonth,cDay);
                return true;
            //글씨크기조절
            case 4:
                edtDiary.setTextSize(50);
                break;
            case 5:
                edtDiary.setTextSize(20);
                break;
            case 6:
                edtDiary.setTextSize(10);
                break;
        }
        return true;
    }
}