package webbin.example.com.fakecat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ImageRotateAdapter extends RecyclerView.Adapter {

    private Context context;
    private RotateImageData[] dataList;
    private RotateImageView[] imageList;
    private int defaultRotateAngle = 90;
    private boolean isImageEnable = true;


    public ImageRotateAdapter(Context context) {
        dataList = new RotateImageData[0];
        this.context = context;
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_rotate_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        RotateImageData data = dataList[position];
        ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
        imageViewHolder.imageView.setIndex(data.getIndex());
        imageViewHolder.imageView.setImageResource(data.getResource());
        imageViewHolder.imageView.setNewAngle(data.getInitAngle());
        imageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("image rotate adapter", "on click, is image enable = "+isImageEnable);
                if (!isImageEnable) return;
                RotateImageView imageView = (RotateImageView) v;
                imageView.rotate(defaultRotateAngle);
                disableTouch();
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
        notifyDataSetChanged();
    }

    public RotateImageView getRotateImage(int index) {
        return imageList[index];
    }

    public void disableTouch() {
        isImageEnable = false;
    }

    public void ennableTouch() {
        isImageEnable = true;
    }

    public int getDefaultRotateAngle() {
        return defaultRotateAngle;
    }

    public void setDefaultRotateAngle(int defaultRotateAngle) {
        this.defaultRotateAngle = defaultRotateAngle;
    }


}
