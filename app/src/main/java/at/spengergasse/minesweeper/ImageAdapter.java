package at.spengergasse.minesweeper;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

    ImageView[] images;

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return images[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return images[position];
    }

    public ImageAdapter(ImageView[] images) {
        this.images = images;
    }

}
