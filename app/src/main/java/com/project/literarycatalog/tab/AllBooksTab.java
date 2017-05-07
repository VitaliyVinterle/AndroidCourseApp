package com.project.literarycatalog.tab;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.project.literarycatalog.BookProfile;
import com.project.literarycatalog.DatabaseHelper;
import com.project.literarycatalog.R;
import com.project.literarycatalog.adapter.BookListAdapter;



public class AllBooksTab extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.tab_all_books;

    private TextView header;
    private DatabaseHelper sqlHelper;

    GridView gridView;

    public AllBooksTab() {
    }

    public static AllBooksTab getInstance(Context context) {
        Bundle args = new Bundle();
        AllBooksTab tab = new AllBooksTab();
        tab.setArguments(args);
        tab.setContext(context);
        tab.setTitle(context.getString(R.string.tab_item_all_books));

        return tab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        view = inflater.inflate(LAYOUT,container,false);

        header = (TextView)view.findViewById(R.id.header);
        gridView = (GridView) view.findViewById(R.id.gridView);
        sqlHelper = new DatabaseHelper(context);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(AllBooksTab.this.getActivity(), BookProfile.class);
                myIntent.putExtra("id",id);
                startActivity(myIntent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SQLiteDatabase db = sqlHelper.getReadableDatabase();
        Cursor userCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE, null);
        BaseAdapter userAdapter = new BookListAdapter(context, R.layout.book_item, userCursor,
                new String [] {DatabaseHelper.COLUMN_IMAGE, DatabaseHelper.COLUMN_TITLE},
                new int[] { R.id.imgBook, R.id.txtTitle });

        header.setText("Total number of books: " + String.valueOf(userCursor.getCount()));
        gridView.setAdapter(userAdapter);
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
