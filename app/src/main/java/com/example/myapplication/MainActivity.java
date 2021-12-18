package com.example.myapplication;

import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.DecodingAsyncGoneTask;
import com.github.barteksc.pdfviewer.DecodingAsyncTask;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.PdfFile;
import com.github.barteksc.pdfviewer.exception.PageRenderingException;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.github.barteksc.pdfviewer.source.FileSource;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.github.barteksc.pdfviewer.util.SizeEnum;
import com.shockwave.pdfium.PdfiumCore;
import com.shockwave.pdfium.util.Size;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.barteksc.pdfviewer.util.Util.saveBitmapForPdf;

public class MainActivity extends AppCompatActivity {
    public ImageView pdfView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pdfView = findViewById(R.id.pdfView);
    }


    public void test(View view) {
 

    }


    public void test2(View view) throws Exception {
        File file = new File(Environment.getExternalStorageDirectory() + "/documents/a.pdf");

        DecodingAsyncGoneTask decodingAsyncTask = new DecodingAsyncGoneTask(
                this,
                SizeEnum.TABLE,
                file);
        decodingAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        decodingAsyncTask.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {

            }

            @Override
            public void loadComplete(List<Bitmap> bitmapList) throws PageRenderingException {


                saveBitmapForPdf(bitmapList,
                        Environment.getExternalStorageDirectory() + "/documents/", "test000.pdf");
              runOnUiThread(()->{
                  pdfView.setImageBitmap(bitmapList.get(0));

              });
            }

            @Override
            public void loadError(Throwable t) {

            }
        });


    }
}