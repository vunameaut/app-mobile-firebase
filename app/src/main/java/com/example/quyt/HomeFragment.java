package com.example.quyt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private Button btnQuanLy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo nút và thiết lập OnClickListener
        btnQuanLy = view.findViewById(R.id.btn_quanly);
        btnQuanLy.setOnClickListener(v -> {
            // Chuyển đến nhaplieu Activity khi nút được nhấn
            Intent intent = new Intent(getActivity(), nhaplieu.class);
            startActivity(intent);
        });

        return view;
    }
}
