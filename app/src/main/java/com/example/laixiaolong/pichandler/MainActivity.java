package com.example.laixiaolong.pichandler;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{


    @BindView(R.id.ivBottom)
    ImageView ivBottom;
    @BindView(R.id.offsetBlue)
    AppCompatSeekBar offsetBlue;
    @BindView(R.id.offsetGreen)
    AppCompatSeekBar offsetGreen;
    @BindView(R.id.offsetRed)
    AppCompatSeekBar offsetRed;
    @BindView(R.id.offsetAlpha)
    AppCompatSeekBar offsetAlpha;
    @BindView(R.id.rbGroup)
    RadioGroup rbGroup;
    @BindView(R.id.rbRgb)
    AppCompatRadioButton rbRgb;
    @BindView(R.id.rbYuv)
    AppCompatRadioButton rbYuv;
    @BindView(R.id.rbDefault)
    AppCompatRadioButton rbDefault;

    final String[] EXTERNAL_STORAGE_PERMISSION =
            {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    boolean mCanReadFile = false;
    private Bitmap bitmap;
    private ColorMatrix mColorMatrix;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            final int granted =
                    ActivityCompat.checkSelfPermission(this, EXTERNAL_STORAGE_PERMISSION[0]);
            if (granted == PackageManager.PERMISSION_GRANTED) {
                // do something
                mCanReadFile = true;
                Toast.makeText(this, "permission granted..", Toast.LENGTH_SHORT).show();
            } else {
                // can show request UI
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, EXTERNAL_STORAGE_PERMISSION[0])) {
                    // can show request UI,then show UI
                } else {
                    // request permission
                    ActivityCompat.requestPermissions(this, new String[]{EXTERNAL_STORAGE_PERMISSION[0]}, 11);
                }
            }
        } else {
            // directly do something
            mCanReadFile = true;
        }


        initViews();

    }

    private void initViews()
    {
        ivBottom.setImageResource(R.drawable.image79);

        // 初始化矩阵向量
        final float[] colorMatrix = new float[]{
                //
                1, 0, 0, 0, 0,  // red vector
                0, 1, 0, 0, 0,  // green vector
                0, 0, 1, 0, 0,  // blue vector
                0, 0, 0, 1, 0,   // alpha vector
        };
        mColorMatrix = new ColorMatrix(colorMatrix);

        CompoundButton.OnCheckedChangeListener changeListener =
                new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        switch (buttonView.getId()) {
                            case R.id.rbRgb:
                                mColorMatrix.setYUV2RGB();
                                break;
                            case R.id.rbDefault:
                                mColorMatrix.reset();
                                break;
                            case R.id.rbYuv:
                                mColorMatrix.setRGB2YUV();
                                break;
                        }
                        ivBottom.setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
                    }
                };
        rbRgb.setOnCheckedChangeListener(changeListener);
        rbYuv.setOnCheckedChangeListener(changeListener);
        rbDefault.setOnCheckedChangeListener(changeListener);
        ivBottom.setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (!fromUser) return;
                switch (seekBar.getId()) {
                    case R.id.offsetRed:
                        mColorMatrix.getArray()[4] = progress;
                        break;
                    case R.id.offsetGreen:
                        mColorMatrix.getArray()[9] = progress;
                        break;
                    case R.id.offsetBlue:
                        mColorMatrix.getArray()[14] = progress;
                        break;
                    case R.id.offsetAlpha:
                        mColorMatrix.getArray()[19] = progress;
                        break;
                }
                ivBottom.setColorFilter(new ColorMatrixColorFilter(mColorMatrix));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        };
        offsetBlue.setOnSeekBarChangeListener(listener);
        offsetRed.setOnSeekBarChangeListener(listener);
        offsetGreen.setOnSeekBarChangeListener(listener);
        offsetAlpha.setOnSeekBarChangeListener(listener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted
            Toast.makeText(this, "permission granted..", Toast.LENGTH_SHORT).show();

            mCanReadFile = true;
        } else {
            //permission denied
            Toast.makeText(this, "permission denied..", Toast.LENGTH_SHORT).show();

            mCanReadFile = false;
        }

    }

    public void selectPicture(View view)
    {
        if (!mCanReadFile) {
            Toast.makeText(this, "can not select picture, as permission denied..", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Intent chooser = Intent.createChooser(intent, "select image");
        if (chooser.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 11);
        } else {
            Toast.makeText(this, "no such Application.", Toast.LENGTH_SHORT).show();
        }
    }

    private static final String TAG = "MainActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 11) {
            final Uri picUri = data.getData();
            Log.i(TAG, "onActivityResult: picUri= " + picUri);

            if (picUri != null) {
                final String picPath = contentUri2FilePath(picUri);

                Log.i(TAG, "onActivityResult: picPath= " + picPath);

                if (picPath != null) {
                    bitmap = decodeBitmap(picPath);
                    ivBottom.setImageBitmap(bitmap);
                }
            } else {
                Toast.makeText(this, "cursor is null", Toast.LENGTH_SHORT).show();
            }

        } else {
            ivBottom.setImageResource(R.drawable.image79);
        }


    }

    private Bitmap decodeBitmap(String picPath)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(picPath, options);
        final int height = options.outHeight;
        final int width = options.outWidth;

        int reqWidth = ivBottom.getWidth();
        int reqHeight = ivBottom.getHeight();
        if (width > height) options.inSampleSize =
                Math.max(Math.round((float) reqWidth / width), Math.round((float) width / reqWidth));
        else options.inSampleSize =
                Math.max(Math.round((float) reqHeight / height), Math.round((float) height / reqHeight));

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picPath, options);
    }


    public String contentUri2FilePath(Uri contentUri)
    {
        String path = contentUri.toString();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && path.startsWith("file")) {  //file://
            return path.substring(path.indexOf("s"));
        }
        Context context = getApplicationContext();
        if (DocumentsContract.isDocumentUri(context, contentUri)) {
            String wholeID = DocumentsContract.getDocumentId(contentUri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + " = ?";
            Cursor cursor =
                    context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column[0]);
                try {
                    if (cursor.moveToFirst()) return cursor.getString(columnIndex);
                } finally {
                    cursor.close();
                }
            }
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor =
                    context.getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                try {
                    if (cursor.moveToFirst()) return cursor.getString(column_index);
                } finally {
                    cursor.close();
                }
            }
        }

        return contentUri.toString();
    }

    @Override
    protected void onDestroy()
    {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menuBlur:
                startActivity(new Intent(this, BlurActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
