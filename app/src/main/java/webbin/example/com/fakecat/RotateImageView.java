package webbin.example.com.fakecat;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RotateImageView extends AppCompatImageView {

    private int currentAngle = 0;
    private int duration = 500;
    private int index = 0;
    private RotatingListener rotatingListener;


    public RotateImageView(Context context) {
        super(context);
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RotateImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void rotate(int angle) {
        int endAngle = angle + currentAngle;
        if (endAngle >= 360) endAngle = 360;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this,
                "rotation",
                currentAngle,
                currentAngle + angle);
        animator.setDuration(duration);
        animator.start();
        currentAngle = endAngle == 360 ? 0 : endAngle;
        if (rotatingListener != null) {
            rotatingListener.onEndRotate(index, getPointer(currentAngle));
        }
    }


    private String getPointer(int angle) {
        String pointer = ViewRotateManager.LEFT_POINTER;
        switch (angle) {
            case 0:
                pointer = ViewRotateManager.TOP_POINTER;
                break;
            case 90:
                pointer = ViewRotateManager.RIGHT_POINTER;
                break;
            case 180:
                pointer = ViewRotateManager.BOTTOM_POINTER;
                break;
            case 360:
                pointer = ViewRotateManager.LEFT_POINTER;
                break;
        }
        return pointer;
    }


    public int getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(int currentAngle) {
        this.currentAngle = currentAngle;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public RotatingListener getRotatingListener() {
        return rotatingListener;
    }

    public void setRotatingListener(RotatingListener rotatingListener) {
        this.rotatingListener = rotatingListener;
    }
}
