package com.example.quyt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText edt_tk, edt_mk;
    private Button btnDangNhap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_tk = findViewById(R.id.edt_tk);
        edt_mk = findViewById(R.id.edt_mk);
        btnDangNhap = findViewById(R.id.signInButton);
        TextView signUpText = findViewById(R.id.signUpText);

        btnDangNhap.setOnClickListener(view -> dang_nhap());
        signUpText.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, signup.class);
            startActivity(intent);
        });


    }


    public void dang_nhap() {
        String email = edt_tk.getText().toString().trim();
        String matKhau = edt_mk.getText().toString().trim();

        if (email.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = FirebaseDatabase.getInstance().getReference("8/data")
                .orderByChild("email")
                .equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean isLoggedIn = false;
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String matKhauFirebase = userSnapshot.child("matKhau").getValue(String.class);
                        String trangThai = userSnapshot.child("trangThai").getValue(String.class);

                        if (matKhauFirebase != null && matKhauFirebase.equals(matKhau)) {
                            if ("active".equals(trangThai)) {
                                Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                Log.d("LoginStatus", "Đăng nhập thành công");


                                Intent intent = new Intent(MainActivity.this, nhaplieu.class);
                                startActivity(intent);
                                isLoggedIn = true;
                                break;
                            } else {
                                Toast.makeText(MainActivity.this, "Tài khoản của bạn đã bị vô hiệu hóa.", Toast.LENGTH_SHORT).show();
                                Log.e("LoginStatus", "Tài khoản bị vô hiệu hóa.");
                                isLoggedIn = true;
                                break;
                            }
                        }
                    }
                    if (!isLoggedIn) {
                        Toast.makeText(MainActivity.this, "Mật khẩu không chính xác.", Toast.LENGTH_SHORT).show();
                        Log.e("LoginStatus", "Mật khẩu không chính xác.");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Tên đăng nhập không tồn tại.", Toast.LENGTH_SHORT).show();
                    Log.e("LoginStatus", "Tên đăng nhập không tồn tại.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginStatus", "Database error: " + databaseError.getMessage());
            }
        });
    }


    // Lớp User để đại diện cho dữ liệu người dùng
    public static class User {
        public String Email;
        public String MatKhau;
        public String TrangThai;

        // Constructor mặc định cần thiết cho Firebase
        public User() {
        }

        public User(String email, String matKhau, String trangThai) {
            this.Email = email;
            this.MatKhau = matKhau;
            this.TrangThai = trangThai;
        }
    }
}
