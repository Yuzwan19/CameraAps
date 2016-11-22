package com.dev.izcax.cameraaps;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Kode Permintaan
    private static final int camera_capture_image_request_code = 100;
    public static final int media_type_image = 1;
    //nama folder gambar
    private static final String image_directory_name = "CameraApp";
    //file url untuk mengambil gambar
    private Uri fileUrl;

    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        img = (ImageView)findViewById(R.id.imgPgambar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambilGambar();
            }
        });

        if (!hpsupportcamera()){
            Toast.makeText(MainActivity.this, "Hp tidak support kamera", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void ambilGambar() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUrl = getOutputMediaFileUri(media_type_image);
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUrl);
        startActivityForResult(i, camera_capture_image_request_code);
    }

    private Uri getOutputMediaFileUri(int media_type_image) {
        return Uri.fromFile(getOutputMediaFile(media_type_image));
    }

    private File getOutputMediaFile(int media_type_image) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                ,image_directory_name);
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d(image_directory_name,"Gagal membuat"+image_directory_name+"folder");
                return null;
            }
        }
        String tanggal = new SimpleDateFormat("yyyyMMdd_hhMMSS", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (media_type_image == media_type_image){
            mediaFile = new File(mediaStorageDir.getPath()+File.separator+"IMG"+tanggal+".jpg");
        } else {return null;
        }
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == camera_capture_image_request_code){
            tampilGambar();
        } else if (resultCode == RESULT_CANCELED){
            Toast.makeText(MainActivity.this, "User membatalkan tangkapan gambar",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this,"Gagal menampilkan gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void tampilGambar() {
     try {
         BitmapFactory.Options option = new BitmapFactory.Options();
         option.inSampleSize = 20; //atur ukuran gambar
         final Bitmap bitmap = BitmapFactory.decodeFile(fileUrl.getPath(),option);
         img.setImageBitmap(bitmap);
     } catch (NullPointerException e) {
         e.printStackTrace();
     }

    }

    private boolean hpsupportcamera() {
        if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share) {
            Intent in = new Intent(Intent.ACTION_SEND);
            in.setType("image/*");
            String imagePath = fileUrl.getPath();
            File imageFiletoShare = new File(imagePath);

            Uri uri = Uri.fromFile(imageFiletoShare);
            in.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(in, "Share Gambar"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
