package webbin.example.com.fakecat;

public class RotateImageData {

    private int index;
    private int resource;
    private RotatingListener listener;
    private int initAngle = 0;

    public RotateImageData(int index, int resource) {
        this.index = index;
        this.resource = resource;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public RotatingListener getListener() {
        return listener;
    }

    public void setListener(RotatingListener listener) {
        this.listener = listener;
    }

    public int getInitAngle() {
        return initAngle;
    }

    public void setInitAngle(int initAngle) {
        this.initAngle = initAngle;
    }
}
