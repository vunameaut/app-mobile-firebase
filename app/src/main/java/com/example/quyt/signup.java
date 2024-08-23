package com.example.quyt;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class signup extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    private Button signUpButton;
    private TextView signInText;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        signUpButton = findViewById(R.id.signUpButton);
        signInText = findViewById(R.id.signInText);

        // Xử lý sự kiện khi người dùng nhấn nút Sign Up
        signUpButton.setOnClickListener(v -> handleSignUp());

        signInText.setOnClickListener(view -> finish());
    }

    public class User {
        private String Email;
        private String MaTaiKhoan;
        private String MatKhau;
        private String Ngaytao;
        private String TenDangNhap;
        private String TrangThai;
        private String VaiTro;

        public User(String Email, String MatKhau, String TenDangNhap) {
            this.Email = Email;
            this.MatKhau = MatKhau;
            this.TenDangNhap = TenDangNhap;
            this.MaTaiKhoan = FirebaseDatabase.getInstance().getReference().push().getKey(); // Tạo mã tài khoản tự động
            this.Ngaytao = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()); // Tạo ngày tạo tự động
            this.TrangThai = "active";
            this.VaiTro = "user";
        }
        public String getEmail() {
            return Email;
        }

        public String getMaTaiKhoan() {
            return MaTaiKhoan;
        }

        public String getMatKhau() {
            return MatKhau;
        }

        public String getNgaytao() {
            return Ngaytao;
        }

        public String getTenDangNhap() {
            return TenDangNhap;
        }

        public String getTrangThai() {
            return TrangThai;
        }

        public String getVaiTro() {
            return VaiTro;
        }
    }
    private void handleSignUp() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một đối tượng User để ghi vào Firebase
        User user = new User(email, password, username);

        FirebaseDatabase.getInstance().getReference("8/data")
                .child(user.getMaTaiKhoan())  // Sử dụng MaTaiKhoan làm khóa chính
                .setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        Log.d("DatabaseWrite", "Dữ liệu đã được ghi thành công.");

                        // Chuyển về màn hình đăng nhập
                        finish();
                    } else {
                        Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                        Log.e("DatabaseWrite", "Lỗi khi ghi dữ liệu: " + task.getException().getMessage());
                    }
                });
    }




}