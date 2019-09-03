package com.custom.gallery;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.custom.library.ImagePicker;
import com.custom.library.bean.ImageItem;
import com.custom.library.util.Utils;
import com.custom.library.view.GridSpacingItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;



public class MainJavaActivity extends AppCompatActivity implements ImagePicker.OnPickImageResultListener {

    private ImageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImagePicker.defaultConfig();
//        ImagePicker.limit(12);
        CheckBox cb_crop = (CheckBox) findViewById(R.id.cb_crop);
        cb_crop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ImagePicker.isCrop(isChecked);
            }
        });
        CheckBox cb_multi = (CheckBox) findViewById(R.id.cb_multi);
        cb_multi.setChecked(true);//默认是多选
        cb_multi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ImagePicker.multiMode(isChecked);
            }
        });
        Button btn_pick = (Button) findViewById(R.id.btn_pick);
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.pick(MainJavaActivity.this, MainJavaActivity.this);
            }
        });
        Button btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.camera(MainJavaActivity.this, MainJavaActivity.this);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(new ArrayList<ImageItem>());
        adapter.setListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ImagePicker.review(MainJavaActivity.this, position, MainJavaActivity.this);
            }
        });
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, Utils.dp2px(this, 2f), false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onImageResult(@NotNull ArrayList<ImageItem> imageItems) {
        adapter.updateData(imageItems);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImagePicker.clear();
    }
}
