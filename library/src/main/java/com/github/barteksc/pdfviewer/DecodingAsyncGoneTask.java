/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.barteksc.pdfviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.AsyncTask;
import com.github.barteksc.pdfviewer.exception.PageRenderingException;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.source.DocumentSource;
import com.github.barteksc.pdfviewer.source.FileSource;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.shockwave.pdfium.util.Size;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class DecodingAsyncGoneTask extends AsyncTask<Void, Void, Throwable> {

    private boolean cancelled;

    private OnLoadCompleteListener onLoadCompleteListener;


    private PdfiumCore pdfiumCore;
    private String password;
    private DocumentSource docSource;
    private int[] userPages;
    private PdfFile pdfFile;
    private Context mCtx;
    private Size size;

    /**
     * True if should scroll through pages vertically instead of horizontally
     */
    private boolean swipeVertical = true;

    private boolean enableSwipe = true;

    private boolean doubletapEnabled = true;

    private boolean nightMode = false;

    private boolean pageSnap = true;
    private boolean autoSpacing = true;
    private int spacingPx = 0;
    private boolean fitEachPage = false;
    /**
     * Policy for fitting pages to screen
     */
    private FitPolicy pageFitPolicy = FitPolicy.WIDTH;


    public DecodingAsyncGoneTask(Context mCtx, Size size, File file
    ) {
        this(mCtx, size, file, "", null);


    }

    public DecodingAsyncGoneTask(Context mCtx, Size size, File file,
                                 String password,
                                 int[] userPages) {
        this.docSource = new FileSource(file);
        this.userPages = userPages;
        this.cancelled = false;
        this.password = password;
        this.pdfiumCore = new PdfiumCore(mCtx);
        this.mCtx = mCtx;
        this.size = size;


    }

    @Override
    protected Throwable doInBackground(Void... params) {
        try {

            PdfDocument pdfDocument = docSource.createDocument(mCtx, pdfiumCore, password);
            pdfFile = new PdfFile(pdfiumCore, pdfDocument,
                    pageFitPolicy,
                    size,
                    userPages,
                    swipeVertical,
                    spacingPx,
                    autoSpacing,
                    fitEachPage);


            return null;


        } catch (Throwable t) {
            return t;
        }
    }


    @Override
    protected void onPostExecute(Throwable t) {

        if (t != null) {
            onLoadCompleteListener.loadError(t);
            return;
        }
        if (!cancelled) {
            try {
                int pagesCount = pdfFile.getPagesCount();
                List<Bitmap> bitmaps = new ArrayList<>();
                for (int i = 0; i < pagesCount; i++) {
                    Bitmap bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);

                    pdfFile.openPage(i);
                    pdfFile.renderPageBitmap(bitmap, i, new Rect(0, 0, size.getWidth(), size.getHeight()), true);

                    if (swipeVertical) {
                        bitmap = rotateBitmap(bitmap, 90);
                    }

                    bitmaps.add(bitmap);
                }
                pdfFile.dispose();
                onLoadCompleteListener.loadComplete(bitmaps);
            } catch (PageRenderingException e) {
                e.printStackTrace();
                onLoadCompleteListener.loadError(e);

            }

        }


    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    @Override
    protected void onCancelled() {
        cancelled = true;
    }

    public boolean isSwipeVertical() {
        return swipeVertical;
    }

    public void setSwipeVertical(boolean swipeVertical) {
        this.swipeVertical = swipeVertical;
    }

    public boolean isEnableSwipe() {
        return enableSwipe;
    }

    public void setEnableSwipe(boolean enableSwipe) {
        this.enableSwipe = enableSwipe;
    }

    public boolean isDoubletapEnabled() {
        return doubletapEnabled;
    }

    public void setDoubletapEnabled(boolean doubletapEnabled) {
        this.doubletapEnabled = doubletapEnabled;
    }

    public boolean isNightMode() {
        return nightMode;
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public boolean isPageSnap() {
        return pageSnap;
    }

    public void setPageSnap(boolean pageSnap) {
        this.pageSnap = pageSnap;
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
        this.onLoadCompleteListener = onLoadCompleteListener;
    }

    public FitPolicy getPageFitPolicy() {
        return pageFitPolicy;
    }

    public void setPageFitPolicy(FitPolicy pageFitPolicy) {
        this.pageFitPolicy = pageFitPolicy;
    }
}
