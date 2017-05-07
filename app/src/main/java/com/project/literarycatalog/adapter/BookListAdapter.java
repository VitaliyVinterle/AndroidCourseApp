package com.project.literarycatalog.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.project.literarycatalog.DatabaseHelper;
import com.project.literarycatalog.R;

public class BookListAdapter extends SimpleCursorAdapter {

    private Cursor c;
    private Context context;

    public BookListAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        this.c = c;
        this.context = context;
    }

    public View getView(int pos, View inView, ViewGroup parent) {
        View view = inView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.book_item, null);
        }

        this.c.moveToPosition(pos);
        String titleColumn = this.c.getString(this.c.getColumnIndex(DatabaseHelper.COLUMN_TITLE));
        byte[] image = this.c.getBlob(this.c.getColumnIndex(DatabaseHelper.COLUMN_IMAGE));
        ImageView imageView = (ImageView) view.findViewById(R.id.imgBook);

        if (image != null) {
            if(image.length > 3)
            {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            else
            {
                imageView.setImageResource(R.drawable.bookshelf);
            }
        }
        TextView title = (TextView) view.findViewById(R.id.txtTitle);
        title.setText(titleColumn);

        return(view);
    }
}