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
    String selectedMalop = null;

    @SuppressLint("MissingInflatedId")
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


        // Setup ListView
        LV = findViewById(R.id.lv);
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

        LV.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = myList.get(position);
            String[] parts = selectedItem.split(", ");
            selectedMalop = parts[0].split(": ")[1];
            String tenlop = parts[1].split(": ")[1];
            int siso = Integer.parseInt(parts[2].split(": ")[1]);

            edtmalop.setText(selectedMalop);
            edttenlop.setText(tenlop);
            edtsiso.setText(String.valueOf(siso));
        });

        btnthem.setOnClickListener(view -> {
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
        });

        btnxoa.setOnClickListener(view -> {
            if (selectedMalop != null) {
                int n = myDatabase.delete("tbllop", "malop = ?", new String[]{selectedMalop});
                String msg = "";
                if (n == 0) {
                    msg = "Xóa không thành công";
                } else {
                    msg = n + " xóa thành công";
                    fetchDataAndDisplay(); // Update the ListView after deleting data
                    clearFields();
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng chọn lớp để xóa", Toast.LENGTH_SHORT).show();
            }
        });

        btnsua.setOnClickListener(view -> {
            if (selectedMalop != null) {
                String tenlop = edttenlop.getText().toString();
                int siso = Integer.parseInt(edtsiso.getText().toString());
                ContentValues myValue = new ContentValues();
                myValue.put("tenlop", tenlop);
                myValue.put("siso", siso);
                int n = myDatabase.update("tbllop", myValue, "malop = ?", new String[]{selectedMalop});
                String msg = "";
                if (n == 0) {
                    msg = "Chỉnh sửa không thành công";
                } else {
                    msg = n + " chỉnh sửa thành công";
                    fetchDataAndDisplay(); // Update the ListView after updating data
                    clearFields();
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng chọn lớp để chỉnh sửa", Toast.LENGTH_SHORT).show();
            }
        });

        btntk.setOnClickListener(view -> fetchDataAndDisplay());
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

    private void clearFields() {
        edtmalop.setText("");
        edttenlop.setText("");
        edtsiso.setText("");
        selectedMalop = null;
    }
}