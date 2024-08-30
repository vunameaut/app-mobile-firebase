package com.example.quyt;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class xemdonhang extends AppCompatActivity {

    private ImageButton back;
    private Button btnXemDonHang;

    private EditText edtMadonhang;
    private TextView txtChietKhau, txtGiaDonVi, txtMaChiTiet, txtMaSanPham, txtSoLuong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_xemdonhang);

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

        // Sự kiện nút Xem Thông Tin Đơn Hàng
        btnXemDonHang.setOnClickListener(view -> xemThongTinDonHang());
    }

    private void initUI() {
        back = findViewById(R.id.btn_back);
        btnXemDonHang = findViewById(R.id.btn_xemdonhang);

        edtMadonhang = findViewById(R.id.edt_madonhang);

        txtChietKhau = findViewById(R.id.txt_chietkhau);
        txtGiaDonVi = findViewById(R.id.txt_giadonvi);
        txtMaChiTiet = findViewById(R.id.txt_machitiet);
        txtMaSanPham = findViewById(R.id.txt_masp);
        txtSoLuong = findViewById(R.id.txt_soluong);
    }

    private void xemThongTinDonHang() {
        String maDonHang = edtMadonhang.getText().toString().trim();

        if (maDonHang.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("2/data");
        databaseReference.orderByChild("maDonHang").equalTo(maDonHang).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        OrderDetail orderDetail = snapshot.getValue(OrderDetail.class);

                        if (orderDetail != null) {
                            txtChietKhau.setText(String.valueOf(orderDetail.chietKhau));
                            txtGiaDonVi.setText(String.valueOf(orderDetail.giaDonVi));
                            txtMaChiTiet.setText(orderDetail.maChiTiet);
                            txtMaSanPham.setText(orderDetail.maSanPham);
                            txtSoLuong.setText(String.valueOf(orderDetail.soLuong));
                            return; // Thoát vòng lặp khi tìm thấy đơn hàng
                        }
                    }
                } else {
                    Toast.makeText(xemdonhang.this, "Không tìm thấy dữ liệu cho mã đơn hàng: " + maDonHang, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(xemdonhang.this, "Lỗi khi truy vấn dữ liệu: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Lỗi truy vấn dữ liệu: ", databaseError.toException());
            }
        });
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
