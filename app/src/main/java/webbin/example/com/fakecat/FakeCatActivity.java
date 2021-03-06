package webbin.example.com.fakecat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by webbin on 2017/12/1.
 */

public class FakeCatActivity extends AppCompatActivity {

    private static int NEXT_IS_NULL = -1;

    private static final int MESSAGE_ROTATE_IMAGE = 11201;


    private RecyclerView recyclerView;
    private ImageRotateAdapter adapter;

    private Button refreshBtn;

    private int normalAngle = 90;

    private int rowCount = 3;
    private int colCount = 3;


    private static class RotateHandler extends Handler {
        private WeakReference<FakeCatActivity> mActivity;

        private RotateHandler(WeakReference<FakeCatActivity> mActivity) {
            this.mActivity = mActivity;
        }
        @Override
        public void handleMessage(Message msg) {
            FakeCatActivity activity=mActivity.get();
            switch (msg.what){
                case MESSAGE_ROTATE_IMAGE:
                    int nextIndex = msg.arg1;
                    if (nextIndex != NEXT_IS_NULL) {
                        activity.rotateImage(nextIndex);
                    } else {
                        activity.adapter.ennableTouch();
                    }
                    break;

            }
        }
    }

    private RotateHandler rotateHandler;
    private ExecutorService executorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_cat);

        initView();
        rotateHandler = new RotateHandler(new WeakReference<>(this));
        executorService = Executors.newSingleThreadExecutor();
    }

    private void initView() {
        recyclerView = findViewById(R.id.image_rotate_recycler_view);
        refreshBtn = findViewById(R.id.image_refresh_btn);

        recyclerView.setLayoutManager(new GridLayoutManager(this, colCount));
        adapter = new ImageRotateAdapter(this);
        adapter.setDataList(generateDataList());
        recyclerView.setAdapter(adapter);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

    }


    private RotateImageData[] generateDataList() {
        int size = rowCount * colCount;
        RotateImageData[] datas = new RotateImageData[size];
        for (int i = 0; i < size; i++) {
            RotateImageData data = new RotateImageData(i, R.mipmap.ic_launcher);
            data.setListener(new RotatingListener() {
                @Override
                public void onEndRotate(int index, String endPointer) {
                    int nextIndex = getNextIndex(index,endPointer);
                    postRotateMessage(nextIndex);
                }
            });
            int initAngle;
            double random = Math.random();
            if (random > 0 && random <= 0.25) {
                initAngle = 90;
            } else if (random > 0.25 && random <= 0.5) {
                initAngle = 180;
            } else if (random > 0.5 && random <= 0.75) {
                initAngle = 270;
            } else {
                initAngle = 0;
            }
            data.setInitAngle(initAngle);
            datas[i] = data;
        }
        return datas;
    }

    private void rotateImage(int index) {
        adapter.getRotateImage(index).rotate(normalAngle);
    }

    private void postRotateMessage(final int nextIndex) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Constant.RATATE_DURATION);
                    Message message = Message.obtain();
                    message.arg1 = nextIndex;
                    message.what = MESSAGE_ROTATE_IMAGE;
                    rotateHandler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rotate_image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.grid_3_3:
                rowCount = 3;
                colCount = 3;
                recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                refreshData();
                break;
            case R.id.grid_4_4:
                rowCount = 4;
                colCount = 4;
                recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
                refreshData();
                break;
            case R.id.grid_5_5:
                rowCount = 5;
                colCount = 5;
                recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
                refreshData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 计算传递的下一个位置
     * @param index 当前位置
     * @param endPointer　旋转结束时指向的位置
     * @return 下一个位置的下标
     */
    private int getNextIndex(int index, String endPointer) {
        int nextIndex = NEXT_IS_NULL;
        switch (endPointer) {
            case ViewRotateManager.LEFT_POINTER:
                if (index % colCount != 0) {
                    nextIndex = index - 1;
                }
                break;
            case ViewRotateManager.RIGHT_POINTER:
                if ((index + 1) % colCount != 0) {
                    nextIndex = index + 1;
                }
                break;
            case ViewRotateManager.TOP_POINTER:
                if (index > colCount - 1) {
                    nextIndex = index - colCount;
                }
                break;
            case ViewRotateManager.BOTTOM_POINTER:
                if (index < (rowCount - 1) * colCount - 1) {
                    nextIndex = index + colCount;
                }
                break;
        }
        return nextIndex;
    }

    private void refreshData() {
        adapter.setDataList(generateDataList());
    }


}
