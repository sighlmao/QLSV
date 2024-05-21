package com.example.qlsv;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText edtmalop, edttenlop, edtsiso;
    Button btnthem, btnxoa, btnsua, btntk;
    ListView LV;
    ArrayList<String> myList;
    ArrayAdapter<String> myAdapter;
    SQLiteDatabase myDatabase;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtmalop = findViewById(R.id.edtmalop);
        edttenlop = findViewById(R.id.edttenlop);
        edtsiso = findViewById(R.id.edtsiso);
        btnthem = findViewById(R.id.btnthem);
        btnxoa = findViewById(R.id.btnxoa);
        btnsua = findViewById(R.id.btnsua);
        btntk = findViewById(R.id.btntimkiem);

        // Setup ListView
        LV = findViewById(R.id.iv);
        myList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList);
        LV.setAdapter(myAdapter);

        // Create and open SQLite database
        myDatabase = openOrCreateDatabase("qlsinhvien.db", MODE_PRIVATE, null);

        // Create table if it doesn't exist
        try {
            String sql = "CREATE TABLE tbllop(malop TEXT primary key, tenlop TEXT, siso INTEGER)";
            myDatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table đã tồn tại");
        }

        // Fetch and display data from the database
        fetchDataAndDisplay();

        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String malop = edtmalop.getText().toString();
                String tenlop = edttenlop.getText().toString();
                int siso = Integer.parseInt(edtsiso.getText().toString());
                ContentValues myValue = new ContentValues();
                myValue.put("malop", malop);
                myValue.put("tenlop", tenlop);
                myValue.put("siso", siso);
                String msg = "";
                if (myDatabase.insert("tbllop", null, myValue) == -1) {
                    msg = "Thêm bị lỗi! Hãy thử lại.";
                } else {
                    msg = "Thêm thành công";
                    fetchDataAndDisplay(); // Update the ListView after inserting new data
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        btnxoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String malop = edtmalop.getText().toString();
                int n = myDatabase.delete("tbllop", "malop = ?", new String[]{malop});
                String msg = "";
                if (n == 0){
                    msg = "Xóa không thành công";
                }else{
                    msg=n+" xóa thành công";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        btnsua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int siso = Integer.parseInt(edtsiso.getText().toString());
                String malop = edtmalop.getText().toString();
                ContentValues myValue = new ContentValues();
                myValue.put("siso", siso);
                int n = myDatabase.update("tbllop", myValue, "malop = ?", new String[]{malop});
                String msg="";
                if (n == 0){
                    msg = "Chỉnh sửa không thành công";
                }else{
                    msg = n +" chỉnh sửa thành công";
                }
                Toast.makeText(MainActivity.this, msg,Toast.LENGTH_SHORT).show();
            }
        });

        btntk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList.clear();
                Cursor c = myDatabase.query("tbllop", null, null,null,null, null,null);
                c.moveToNext();
                String data = "";
                while(c.isAfterLast() == false){
                    data = c.getString(0)+" - "+c.getString(1)+" - "+c.getString(2);
                    c.moveToNext();
                    myList.add(data);
                }
                c.close();
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchDataAndDisplay() {
        myList.clear();
        Cursor cursor = myDatabase.rawQuery("SELECT * FROM tbllop", null);
        if (cursor.moveToFirst()) {
            do {
                String malop = cursor.getString(0);
                String tenlop = cursor.getString(1);
                int siso = cursor.getInt(2);
                myList.add("Mã lớp: " + malop + ", Tên lớp: " + tenlop + ", Sĩ số: " + siso);
            } while (cursor.moveToNext());
        }
        cursor.close();
        myAdapter.notifyDataSetChanged();
    }
}