package com.example.quyt;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword, edtConfirmPassword;
    private Button signUpButton;
    private TextView signInText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Khởi tạo DatabaseReference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        signUpButton = findViewById(R.id.signUpButton);
        signInText = findViewById(R.id.signInText);

        // Xử lý sự kiện khi người dùng nhấn nút Sign Up
        signUpButton.setOnClickListener(v -> handleSignUp());

        // Điều hướng trở lại màn hình đăng nhập
        signInText.setOnClickListener(view -> finish());
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

        // Tạo tài khoản với email và mật khẩu bằng Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công, cập nhật thông tin người dùng
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)  // Cập nhật tên hiển thị
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Lưu thông tin người dùng vào Realtime Database
                                            saveUserInformation(user.getUid(), username, email);

                                            // Gửi email xác nhận
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(verifyTask -> {
                                                        if (verifyTask.isSuccessful()) {
                                                            Toast.makeText(signup.this, "Đăng ký thành công! Vui lòng xác nhận email của bạn.", Toast.LENGTH_SHORT).show();
                                                            Log.d("UserProfile", "Thông tin người dùng đã được cập nhật.");
                                                            finish();  // Quay lại màn hình đăng nhập
                                                        } else {
                                                            Toast.makeText(signup.this, "Gửi email xác nhận thất bại: " + verifyTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            Log.e("UserProfile", "Gửi email xác nhận thất bại.", verifyTask.getException());
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(signup.this, "Cập nhật tên người dùng thất bại: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e("UserProfile", "Cập nhật tên người dùng thất bại.", updateTask.getException());
                                        }
                                    });
                        }
                    } else {
                        // Đăng ký thất bại
                        Toast.makeText(signup.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("SignUp", "Đăng ký thất bại", task.getException());
                    }
                });
    }

    private void saveUserInformation(String uid, String email, String username) {
        // Tạo đối tượng để lưu thông tin người dùng
        Map<String, String> user = new HashMap<>();
        user.put("uid", uid);
        user.put("username", username);
        user.put("email", email);

        // Lưu thông tin người dùng vào node "users" dưới khóa "username"
        mDatabase.child("users").child(username).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Database", "Thông tin người dùng đã được lưu vào cơ sở dữ liệu.");
                    } else {
                        Log.e("Database", "Lưu thông tin người dùng thất bại.", task.getException());
                    }
                });
    }



}
