package com.project.literarycatalog.tab;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.project.literarycatalog.BookProfile;
import com.project.literarycatalog.DatabaseHelper;
import com.project.literarycatalog.R;

import java.sql.SQLException;

public class SearchByTitleTab extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.tab_search_by_title;

    ListView userList;
    EditText userFilter;
    DatabaseHelper sqlHelper;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    String[] headers;
    String key;

    public SearchByTitleTab() {
    }

    public static SearchByTitleTab getInstance(Context context) {
        Bundle args = new Bundle();
        SearchByTitleTab tab = new SearchByTitleTab();
        tab.setArguments(args);
        tab.setContext(context);
        tab.setTitle(context.getString(R.string.tab_item_find_by_title));

        return tab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState != null) {
            String[] newHeaders = savedInstanceState.getStringArray(key);
            headers = newHeaders;
        }
        view = inflater.inflate(LAYOUT,container,false);

        userList = (ListView) view.findViewById(R.id.userListTitle);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchByTitleTab.this.getActivity(), BookProfile.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        userFilter = (EditText)view.findViewById(R.id.userFilter);
        sqlHelper = new DatabaseHelper(context);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            sqlHelper.open();
            userCursor = sqlHelper.database.rawQuery("select * from " + DatabaseHelper.TABLE, null);
            headers = new String[] {DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_AUTHOR, DatabaseHelper.COLUMN_YEAR_OF_CREATION, DatabaseHelper.COLUMN_CITY_OF_CREATION, DatabaseHelper.COLUMN_PUBLISHING_HOUSE, DatabaseHelper.COLUMN_NUMBER_OF_PAGES};
            userAdapter = new SimpleCursorAdapter(context, android.R.layout.two_line_list_item,
                    userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);

            if(!userFilter.getText().toString().isEmpty())
                userAdapter.getFilter().filter(userFilter.getText().toString());

            userFilter.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    userAdapter.getFilter().filter(s.toString());
                }
            });
            userAdapter.setStringConversionColumn(1);
            userAdapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {
                    if (constraint == null || constraint.length() == 0) {

                        return sqlHelper.database.rawQuery("select * from " + DatabaseHelper.TABLE, null);
                    }
                    else {
                        return sqlHelper.database.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                                DatabaseHelper.COLUMN_TITLE + " like ?", new String[]{"%" + constraint.toString() + "%"});
                    }
                }
            });
            userList.setAdapter(userAdapter);
        }
        catch (SQLException ex){}

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArray(key,headers);
        super.onSaveInstanceState(savedInstanceState);

    }
    public void setContext(Context context) {
        this.context = context;
    }
}
