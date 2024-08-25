package com.example.quyt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class nhaplieu extends AppCompatActivity {

    private ImageButton back;
    private Button btnGhiNhan, btnXemDonHang;

    private EditText edtMadonhang, edtMasp, edtSoluong, edtGiaDonVi, edtChietKhau;
    private EditText edtMachitiet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nhaplieu);

        // Áp dụng Insets cho layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nhaplieu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo các thành phần UI
        initUI();

        // Sự kiện nút Back
        back.setOnClickListener(view -> finish());

        // Sự kiện nút Ghi Nhận
        btnGhiNhan.setOnClickListener(view -> ghiNhanDuLieu());

        // Sự kiện nút Xem Thông Tin Đơn Hàng
        btnXemDonHang.setOnClickListener(view -> {
            Intent intent = new Intent(nhaplieu.this, xemdonhang.class);
            startActivity(intent);
        });
    }

    private void initUI() {
        back = findViewById(R.id.btn_back);
        btnGhiNhan = findViewById(R.id.ghinhan);
        btnXemDonHang = findViewById(R.id.btn_xemdonhang);

        edtMadonhang = findViewById(R.id.txt_madonhang);
        edtMasp = findViewById(R.id.txt_masp);
        edtSoluong = findViewById(R.id.txt_soluong);
        edtGiaDonVi = findViewById(R.id.txt_giadonvi);
        edtChietKhau = findViewById(R.id.txt_chietkhau);
        edtMachitiet = findViewById(R.id.txt_machitiet);
    }

    private boolean isValidNumber(String value, String type) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        try {
            if (type.equals("int")) {
                Integer.parseInt(value);
            } else if (type.equals("double")) {
                Double.parseDouble(value);
            } else {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private void ghiNhanDuLieu() {
        String maChiTiet = edtMachitiet.getText().toString().trim();
        String maDonHang = edtMadonhang.getText().toString().trim();
        String maSanPham = edtMasp.getText().toString().trim();
        String soLuongStr = edtSoluong.getText().toString().trim();
        String giaDonViStr = edtGiaDonVi.getText().toString().trim();
        String chietKhauStr = edtChietKhau.getText().toString().trim();

        // Kiểm tra các trường không được để trống
        if (maChiTiet.isEmpty() || maDonHang.isEmpty() || maSanPham.isEmpty()
                || soLuongStr.isEmpty() || giaDonViStr.isEmpty() || chietKhauStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra định dạng số
        if (!isValidNumber(soLuongStr, "int")) {
            Toast.makeText(this, "Số Lượng phải là số nguyên hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidNumber(giaDonViStr, "double")) {
            Toast.makeText(this, "Giá Đơn Vị phải là số thực hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidNumber(chietKhauStr, "double")) {
            Toast.makeText(this, "Chiết Khấu phải là số thực hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển đổi dữ liệu đã kiểm tra
        int soLuong = Integer.parseInt(soLuongStr);
        double giaDonVi = Double.parseDouble(giaDonViStr);
        double chietKhau = Double.parseDouble(chietKhauStr);

        // Kiểm tra sự tồn tại của mã đơn hàng
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("2/data");
        databaseReference.child(maDonHang).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // ID đã tồn tại
                    Toast.makeText(nhaplieu.this, "Mã đơn hàng đã tồn tại. Vui lòng chọn mã khác.", Toast.LENGTH_SHORT).show();
                } else {
                    // Tạo đối tượng OrderDetail
                    OrderDetail orderDetail = new OrderDetail(maChiTiet, maDonHang, maSanPham, soLuong, giaDonVi, chietKhau);

                    // Ghi dữ liệu vào Firebase với maDonHang làm tên nút dữ liệu
                    databaseReference.child(maDonHang).setValue(orderDetail)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(nhaplieu.this, "Dữ liệu đã được ghi nhận thành công!", Toast.LENGTH_SHORT).show();
                                clearInputFields();
                                Log.d("Firebase", "Ghi dữ liệu thành công cho mã đơn hàng: " + maDonHang);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(nhaplieu.this, "Ghi dữ liệu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("Firebase", "Ghi dữ liệu thất bại: ", e);
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(nhaplieu.this, "Lỗi khi kiểm tra dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Lỗi kiểm tra dữ liệu: ", databaseError.toException());
            }
        });
    }



    private void clearInputFields() {
        edtMachitiet.setText("");
        edtMadonhang.setText("");
        edtMasp.setText("");
        edtSoluong.setText("");
        edtGiaDonVi.setText("");
        edtChietKhau.setText("");
    }

    // Lớp mô hình cho chi tiết đơn hàng
    public static class OrderDetail {
        public String maChiTiet;
        public String maDonHang;
        public String maSanPham;
        public int soLuong;
        public double giaDonVi;
        public double chietKhau;

        public OrderDetail() {

        }

        public OrderDetail(String maChiTiet, String maDonHang, String maSanPham, int soLuong, double giaDonVi, double chietKhau) {
            this.maChiTiet = maChiTiet;
            this.maDonHang = maDonHang;
            this.maSanPham = maSanPham;
            this.soLuong = soLuong;
            this.giaDonVi = giaDonVi;
            this.chietKhau = chietKhau;
        }
    }
}
