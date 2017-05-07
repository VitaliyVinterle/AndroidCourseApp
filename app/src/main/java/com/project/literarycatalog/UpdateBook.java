package com.project.literarycatalog;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateBook extends AppCompatActivity {

    Toolbar toolbarForUpdate;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_update);

        long userId=0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerUpdate, PlaceholderFragment.newInstance(userId))
                    .commit();
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        toolbarForUpdate = (Toolbar) findViewById(R.id.toolbar_for_update);
        toolbarForUpdate.setTitleTextColor(Color.WHITE);
        toolbarForUpdate.setSubtitleTextColor(Color.WHITE);
        toolbarForUpdate.setTitle("Edit book");
        if(toolbarForUpdate != null)
        {
            setSupportActionBar(toolbarForUpdate);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbarForUpdate.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(UpdateBook.this, BookProfile.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(homeIntent);
            }
        });
    }

    public static class PlaceholderFragment extends Fragment {

        public static EditText titleField;
        public static EditText authorField;
        public static EditText yearOfCreationField;
        public static EditText cityOfCreationField;
        public static EditText publishingHouseField;
        public static EditText numberOfPagesField;

        Button btnUpdate;

        DatabaseHelper sqlHelper;
        SQLiteDatabase db;
        Cursor userCursor;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(long id) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args=new Bundle();
            args.putLong("id", id);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            sqlHelper = new DatabaseHelper(getActivity());
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.update_book, container, false);

            titleField = (EditText)rootView.findViewById(R.id.titleFieldUpdate);
            authorField = (EditText)rootView.findViewById(R.id.authorFieldUpdate);
            yearOfCreationField = (EditText)rootView.findViewById(R.id.yearOfCreationFieldUpdate);
            cityOfCreationField = (EditText)rootView.findViewById(R.id.cityOfCreationFieldUpdate);
            publishingHouseField = (EditText)rootView.findViewById(R.id.publishingHouseFieldUpdate);
            numberOfPagesField = (EditText)rootView.findViewById(R.id.numberOfPagesFieldUpdate);

            btnUpdate = (Button) rootView.findViewById(R.id.btnUpdate);

            final long id = getArguments() != null ? getArguments().getLong("id") : 0;

            db = sqlHelper.getWritableDatabase();

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseHelper.COLUMN_TITLE, titleField.getText().toString());
                    cv.put(DatabaseHelper.COLUMN_AUTHOR, authorField.getText().toString());
                    cv.put(DatabaseHelper.COLUMN_YEAR_OF_CREATION, Integer.parseInt(yearOfCreationField.getText().toString()));
                    cv.put(DatabaseHelper.COLUMN_CITY_OF_CREATION, cityOfCreationField.getText().toString());
                    cv.put(DatabaseHelper.COLUMN_PUBLISHING_HOUSE, publishingHouseField.getText().toString());
                    cv.put(DatabaseHelper.COLUMN_NUMBER_OF_PAGES, Integer.parseInt(numberOfPagesField.getText().toString()));

                    if (id > 0) {
                        db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(id), null);
                    } else {
                        db.insert(DatabaseHelper.TABLE, null, cv);
                    }
                    Toast.makeText(getContext(), "Book edited successfully!", Toast.LENGTH_SHORT).show();
                    goHome();
                }
            });

            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
            userCursor.moveToFirst();

            titleField.setText(userCursor.getString(1));
            authorField.setText(userCursor.getString(2));
            yearOfCreationField.setText(String.valueOf(userCursor.getInt(4)));
            cityOfCreationField.setText(userCursor.getString(5));
            publishingHouseField.setText(userCursor.getString(6));
            numberOfPagesField.setText(String.valueOf(userCursor.getInt(7)));
            userCursor.close();

            return rootView;
        }

        public void goHome(){
            db.close();
            Intent intent = new Intent(getActivity(), BookProfile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }
}