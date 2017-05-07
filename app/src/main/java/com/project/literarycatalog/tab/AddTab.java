package com.project.literarycatalog.tab;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;

import com.project.literarycatalog.DatabaseHelper;
import com.project.literarycatalog.MainActivity;
import com.project.literarycatalog.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;

public class AddTab  extends AbstractTabFragment implements View.OnClickListener{

    private static final int LAYOUT = R.layout.tab_add_book;

    EditText titleField;
    EditText authorField;
    EditText yearOfCreationField;
    EditText cityOfCreationField;
    EditText publishingHouseField;
    EditText numberOfPagesField;

    Animation sunRiseAnimation;

    Button saveButton;
    Button btnAddPicture;

    static final int CAMERA_CAPTURE = 1;
    final int PICK_IMAGE_REQUEST = 2;
    final int PIC_CROP = 3;
    final int REQUEST_CODE_GALLERY = 999;

    private Uri picUri;

    ImageView imageView;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    public static AddTab getInstance(Context context) {
        Bundle args = new Bundle();
        AddTab tab = new AddTab();
        tab.setArguments(args);
        tab.setContext(context);
        tab.setTitle(context.getString(R.string.tab_item_add));

        return tab;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.add(0, 1, 1, "Make a photo");
        menu.add(0, 2, 2, "Gallery image");
    }
    @Override
    public boolean onContextItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                try {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
                    File imageFile = new File(imageFilePath);
                    picUri = Uri.fromFile(imageFile);
                    takePictureIntent.putExtra( MediaStore.EXTRA_OUTPUT,  picUri );
                    startActivityForResult(takePictureIntent, CAMERA_CAPTURE);

                } catch(ActivityNotFoundException anfe){
                    String errorMessage = "ERROR - your device doesn't support capturing images";
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                break;
        }
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        view = inflater.inflate(LAYOUT,container,false);

        btnAddPicture = (Button) view.findViewById(R.id.btnAddPicture);


        btnAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddPicture.performLongClick();
            }
        });
        registerForContextMenu(btnAddPicture);

        setRetainInstance(true);
        sqlHelper = new DatabaseHelper(getActivity());


        titleField = (EditText) view.findViewById(R.id.titleField);

        ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        authorField = (EditText) view.findViewById(R.id.authorField);
        yearOfCreationField = (EditText) view.findViewById(R.id.yearOfCreationField);
        cityOfCreationField = (EditText) view.findViewById(R.id.cityOfCreationField);
        publishingHouseField = (EditText) view.findViewById(R.id.publishingHouseField);
        numberOfPagesField = (EditText) view.findViewById(R.id.numberOfPagesField);
        saveButton = (Button) view.findViewById(R.id.save);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        sunRiseAnimation = AnimationUtils.loadAnimation(context, R.anim.book_rize);
        imageView.startAnimation(sunRiseAnimation);


        final long id = getArguments() != null ? getArguments().getLong("id") : 0;

        db = sqlHelper.getWritableDatabase();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_TITLE, titleField.getText().toString());
                cv.put(DatabaseHelper.COLUMN_AUTHOR, authorField.getText().toString());
                if(!yearOfCreationField.getText().toString().equals(""))
                cv.put(DatabaseHelper.COLUMN_YEAR_OF_CREATION, Integer.parseInt(yearOfCreationField.getText().toString()));
                cv.put(DatabaseHelper.COLUMN_CITY_OF_CREATION, cityOfCreationField.getText().toString());
                cv.put(DatabaseHelper.COLUMN_PUBLISHING_HOUSE, publishingHouseField.getText().toString());
                if(!numberOfPagesField.getText().toString().equals(""))
                cv.put(DatabaseHelper.COLUMN_NUMBER_OF_PAGES, Integer.parseInt(numberOfPagesField.getText().toString()));
                cv.put(DatabaseHelper.COLUMN_IMAGE,imageViewToByte(imageView));
                if(titleField.length() != 0 && authorField.length() !=0 && yearOfCreationField.length() !=0 && cityOfCreationField.length() !=0 && publishingHouseField.length() !=0 && numberOfPagesField.length() !=0) {
                    if (id > 0) {
                        db.update(DatabaseHelper.TABLE, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(id), null);
                    } else {
                        db.insert(DatabaseHelper.TABLE, null, cv);
                    }
                    titleField.setText("");
                    authorField.setText("");
                    yearOfCreationField.setText("");
                    cityOfCreationField.setText("");
                    publishingHouseField.setText("");
                    numberOfPagesField.setText("");
                    Toast.makeText(context, "Book added successfully!", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.mipmap.bookshelf);
                    goHome();
                } else { Toast.makeText(context, "You should fill in all fields!", Toast.LENGTH_SHORT).show();}
            }
        });

        if (id > 0) {
            userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
            userCursor.moveToFirst();

            titleField.setText(userCursor.getString(1));
            authorField.setText(userCursor.getString(2));
            yearOfCreationField.setText(String.valueOf(userCursor.getInt(3)));
            cityOfCreationField.setText(userCursor.getString(4));
            publishingHouseField.setText(userCursor.getString(5));
            numberOfPagesField.setText(String.valueOf(userCursor.getInt(6)));

            userCursor.close();
        }

        return view;
    }

    public void goHome(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(context, "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if(requestCode == CAMERA_CAPTURE){
                Uri uri = picUri;
                performCrop();
            }
            else if(requestCode == PICK_IMAGE_REQUEST){
                picUri = data.getData();
                performCrop();
            }
            else if(requestCode == PIC_CROP){
                Bundle extras = data.getExtras();
                Bitmap thePic = (Bitmap) extras.get("data");
                imageView.setImageBitmap(thePic);
            }
        }
    }

    private void performCrop(){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            String errorMessage = "ERROR - your device doesn't support the crop action";
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View v) {
    }
}
