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


public class SearchByYearTab extends AbstractTabFragment {

    private static final int LAYOUT = R.layout.tab_search_by_year;

    ListView userList;
    EditText userFilter;
    DatabaseHelper sqlHelper;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    public SearchByYearTab() {
    }

    public static SearchByYearTab getInstance(Context context) {
        Bundle args = new Bundle();
        SearchByYearTab tab = new SearchByYearTab();
        tab.setArguments(args);
        tab.setContext(context);
        tab.setTitle(context.getString(R.string.tab_item_find_by_year));

        return tab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        view = inflater.inflate(LAYOUT,container,false);

        userList = (ListView)view.findViewById(R.id.userListYear);
        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(SearchByYearTab.this.getActivity(), BookProfile.class);
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
            String[] headers = new String[] {DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_YEAR_OF_CREATION, DatabaseHelper.COLUMN_AUTHOR, DatabaseHelper.COLUMN_CITY_OF_CREATION, DatabaseHelper.COLUMN_PUBLISHING_HOUSE, DatabaseHelper.COLUMN_NUMBER_OF_PAGES};
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
                                DatabaseHelper.COLUMN_YEAR_OF_CREATION + " like ?", new String[]{"%" + constraint.toString() + "%"});
                    }
                }
            });

            userList.setAdapter(userAdapter);
        }
        catch (java.sql.SQLException ex){}
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
