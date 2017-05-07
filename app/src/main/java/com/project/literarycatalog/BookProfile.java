package com.project.literarycatalog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class BookProfile extends AppCompatActivity {

    Toolbar toolbarForProfile;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_profile);

        long userId=0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getLong("id");
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, PlaceholderFragment.newInstance(userId))
                    .commit();
        }

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        toolbarForProfile = (Toolbar) findViewById(R.id.toolbar_for_profile);
        toolbarForProfile.setTitleTextColor(Color.WHITE);
        toolbarForProfile.setSubtitleTextColor(Color.WHITE);
        toolbarForProfile.setTitle("Book");
        if(toolbarForProfile != null)
        {
            setSupportActionBar(toolbarForProfile);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        toolbarForProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookProfile.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    public static class PlaceholderFragment extends Fragment {
        TextView titleTextView;
        TextView authorTextView;
        ImageView imgImageView;
        TextView yearOfCreationTextView;
        TextView cityOfCreationTextView;
        TextView publishingHouseTextView;
        TextView numberOfPagesTextView;

        TextView titleTextViewTop;
        TextView authorTextViewTop;
        TextView yearOfCreationTextViewTop;
        TextView cityOfCreationTextViewTop;
        TextView publishingHouseTextViewTop;
        TextView numberOfPagesTextViewTop;


        private Animator mCurrentAnimatorEffect;
        private int mShortAnimationDurationEffect;


        Button btnUpdateBook;
        Button btnDeleteBook;
        Button btnSearch;

        public static long ID_OF_BOOK=0;

        Button saveButton;

        public static String INFO_FOR_SHARE ="";
        public static String INFO_FOR_SEARCH ="";

        DatabaseHelper sqlHelper;
        SQLiteDatabase db;
        Cursor userCursor;

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
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.book_profile_activity, container, false);
            titleTextView = (TextView)rootView.findViewById(R.id.titleTextView);
            authorTextView = (TextView)rootView.findViewById(R.id.authorTextView);
            imgImageView = (ImageView) rootView.findViewById(R.id.imgImageView);
            yearOfCreationTextView = (TextView)rootView.findViewById(R.id.yearOfCreationTextView);
            cityOfCreationTextView = (TextView)rootView.findViewById(R.id.cityOfCreationTextView);
            publishingHouseTextView = (TextView)rootView.findViewById(R.id.publishingHouseTextView);
            numberOfPagesTextView = (TextView)rootView.findViewById(R.id.numberOfPagesTextView);

            titleTextViewTop = (TextView)rootView.findViewById(R.id.titleTextViewTop);
            authorTextViewTop = (TextView)rootView.findViewById(R.id.authorTextViewTop);
            yearOfCreationTextViewTop = (TextView)rootView.findViewById(R.id.yearOfCreationTextViewTop);
            cityOfCreationTextViewTop = (TextView)rootView.findViewById(R.id.cityOfCreationTextViewTop);
            publishingHouseTextViewTop = (TextView)rootView.findViewById(R.id.publishingHouseTextViewTop);
            numberOfPagesTextViewTop = (TextView)rootView.findViewById(R.id.numberOfPagesTextViewTop);

            final ImageView thumbImageView = (ImageView) rootView.findViewById(R.id.imgImageView);
            thumbImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(thumbImageView, R.mipmap.ic_launcher,rootView);
                }
            });

            mShortAnimationDurationEffect = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);


            btnUpdateBook = (Button) rootView.findViewById(R.id.btnUpdateBook);
            btnDeleteBook = (Button) rootView.findViewById(R.id.btnDeleteBook);
            btnUpdateBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), UpdateBook.class);
                    intent.putExtra("id",ID_OF_BOOK);
                    startActivity(intent);
                }
            });


            btnDeleteBook = (Button) rootView.findViewById(R.id.btnDeleteBook);
            btnSearch = (Button) rootView.findViewById(R.id.btnSearch);

            saveButton = (Button) rootView.findViewById(R.id.save);

            final long id = getArguments() != null ? getArguments().getLong("id") : 0;
            ID_OF_BOOK = id;
            db = sqlHelper.getWritableDatabase();

            btnDeleteBook.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    db.delete(DatabaseHelper.TABLE, "_id = ?", new String[]{String.valueOf(id)});
                    Toast.makeText(getContext(), "Book deleted successfully!", Toast.LENGTH_SHORT).show();
                    goHome();
                }
            });

            if (id > 0) {

                userCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                        DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
                userCursor.moveToFirst();
                titleTextViewTop.setText("Title: ");
                titleTextView.setText(userCursor.getString(1));
                authorTextViewTop.setText("Author: ");
                authorTextView.setText(userCursor.getString(2));
                INFO_FOR_SEARCH = userCursor.getString(2);
                byte[] image = userCursor.getBlob(3);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                imgImageView.setImageBitmap(bitmap);

                yearOfCreationTextViewTop.setText("Year of creation: ");
                yearOfCreationTextView.setText(String.valueOf(userCursor.getInt(4)));
                cityOfCreationTextViewTop.setText("City of creation: " );
                cityOfCreationTextView.setText(userCursor.getString(5));
                publishingHouseTextViewTop.setText("Publishing house: ");
                publishingHouseTextView.setText(userCursor.getString(6));
                numberOfPagesTextViewTop.setText("Number of pages: ");
                numberOfPagesTextView.setText(String.valueOf(userCursor.getInt(7)));

                INFO_FOR_SHARE = "Recommend you to read very cool book:" + "\n"
                + titleTextViewTop.getText().toString() + titleTextView.getText().toString() + "\n"
                + authorTextViewTop.getText().toString() + authorTextView.getText().toString() +"\n"
                + yearOfCreationTextViewTop.getText().toString() + yearOfCreationTextView.getText().toString() + "\n"
                + cityOfCreationTextViewTop.getText().toString() + cityOfCreationTextView.getText().toString() + "\n"
                + publishingHouseTextViewTop.getText().toString() + publishingHouseTextView.getText().toString() + "\n"
                + numberOfPagesTextViewTop.getText().toString() + numberOfPagesTextView.getText().toString() + "\n";
                userCursor.close();
            }
            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com.ua/search?client=opera&q="+ INFO_FOR_SEARCH +"&sourceid=opera&ie=UTF-8&oe=UTF-8")));
                }
            });

            return rootView;
        }

        public void goHome(){
            db.close();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private void zoomImageFromThumb(final ImageView thumbView, int imageResId, View rootView) {
            if (mCurrentAnimatorEffect != null) {
                mCurrentAnimatorEffect.cancel();
            }

            final ImageView expandedImageView = (ImageView) rootView.findViewById(R.id.expanded_image);
            expandedImageView.setImageDrawable(thumbView.getDrawable());
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            thumbView.getGlobalVisibleRect(startBounds);
            rootView.findViewById(R.id.containerForExpand).getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            float startScale;
            if ((float) finalBounds.width() / finalBounds.height()
                    > (float) startBounds.width() / startBounds.height()) {
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

            thumbView.setAlpha(0f);
            expandedImageView.setVisibility(View.VISIBLE);

            expandedImageView.setPivotX(0f);
            expandedImageView.setPivotY(0f);

            AnimatorSet set = new AnimatorSet();
            set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                            startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                    View.SCALE_Y, startScale, 1f));
            set.setDuration(mShortAnimationDurationEffect);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentAnimatorEffect = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCurrentAnimatorEffect = null;
                }
            });
            set.start();
            mCurrentAnimatorEffect = set;

            final float startScaleFinal = startScale;
            expandedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCurrentAnimatorEffect != null) {
                        mCurrentAnimatorEffect.cancel();
                    }

                    AnimatorSet set = new AnimatorSet();
                    set.play(ObjectAnimator
                            .ofFloat(expandedImageView, View.X, startBounds.left))
                            .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,startBounds.top))
                            .with(ObjectAnimator.ofFloat(expandedImageView,View.SCALE_X, startScaleFinal))
                            .with(ObjectAnimator.ofFloat(expandedImageView,View.SCALE_Y, startScaleFinal));
                    set.setDuration(mShortAnimationDurationEffect);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            thumbView.setAlpha(1f);
                            expandedImageView.setVisibility(View.GONE);
                            mCurrentAnimatorEffect = null;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            thumbView.setAlpha(1f);
                            expandedImageView.setVisibility(View.GONE);
                            mCurrentAnimatorEffect = null;
                        }
                    });
                    set.start();
                    mCurrentAnimatorEffect = set;
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, PlaceholderFragment.INFO_FOR_SHARE);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.shareHeader)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
