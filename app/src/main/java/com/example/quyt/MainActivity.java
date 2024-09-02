package com.example.quyt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText edt_tk, edt_mk;
    private Button btnDangNhap;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Nếu đã đăng nhập, điều hướng đến màn hình home
            Intent intent = new Intent(MainActivity.this, home.class);
            startActivity(intent);
            finish();
        }

        edt_tk = findViewById(R.id.edt_tk);
        edt_mk = findViewById(R.id.edt_mk);
        btnDangNhap = findViewById(R.id.signInButton);
        TextView signUpText = findViewById(R.id.signUpText);

        // Đăng nhập khi bấm nút
        btnDangNhap.setOnClickListener(view -> dang_nhap());

        // Điều hướng đến trang đăng ký
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

        // Đăng nhập bằng Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Log.d("LoginStatus", "Đăng nhập thành công");

                            // Lưu trạng thái đăng nhập
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("userEmail", email);
                            editor.apply();

                            // Điều hướng đến màn hình home
                            Intent intent = new Intent(MainActivity.this, home.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Email chưa được xác minh. Vui lòng xác minh email của bạn.", Toast.LENGTH_SHORT).show();
                            Log.e("LoginStatus", "Email chưa xác minh.");
                        }
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(MainActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LoginStatus", "Đăng nhập thất bại", task.getException());
                    }
                });
    }
}
