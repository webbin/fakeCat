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
import android.view.View;
import android.view.ViewGroup;
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
                    if (nextIndex != NEXT_IS_NULL)
                        activity.rotateImage(nextIndex);
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

        recyclerView.setLayoutManager(new GridLayoutManager(this, colCount));
        adapter = new ImageRotateAdapter();
        adapter.setDataList(generateDataList());
        recyclerView.setAdapter(adapter);

//        recyclerView.getAdapter().get
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
                    Thread.sleep(500);
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

    private class ImageRotateAdapter extends RecyclerView.Adapter {


        private RotateImageData[] dataList;
        private RotateImageView[] imageList;

        public ImageRotateAdapter() {
            dataList = new RotateImageData[0];
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {

            private RotateImageView imageView;

            public ImageViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.item_rotate_image);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FakeCatActivity.this).inflate(R.layout.item_rotate_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            RotateImageData data = dataList[position];
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            imageViewHolder.imageView.setIndex(data.getIndex());
            imageViewHolder.imageView.setImageResource(data.getResource());
            imageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RotateImageView imageView = (RotateImageView) v;
                    imageView.rotate(90);
                }
            });
            imageViewHolder.imageView.setRotatingListener(data.getListener());
            if (position < imageList.length && imageList[position] == null) {
                imageList[position] = imageViewHolder.imageView;
            }
        }

        @Override
        public int getItemCount() {
            return dataList.length;
        }

        public RotateImageData[] getDataList() {
            return dataList;
        }

        public void setDataList(RotateImageData[] dataList) {
            this.dataList = dataList;
            imageList = new RotateImageView[dataList.length];
        }

        public RotateImageView getRotateImage(int index) {
            return imageList[index];
        }


    }

}
