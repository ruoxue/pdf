/*
 * Copyright (C) 2016 Bartosz Schiller.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.barteksc.pdfviewer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.print.PrintAttributes;
import android.util.TypedValue;

import java.io.*;
import java.util.List;

public class Util {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static int getDP(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int n;
        while (-1 != (n = inputStream.read(buffer))) {
            os.write(buffer, 0, n);
        }
        return os.toByteArray();
    }

    public static File saveBitmapForPdf(List<Bitmap> bitmaps, String appDir, String name) {
        android.graphics.pdf.PdfDocument doc = new android.graphics.pdf.PdfDocument();


        int pageWidth = PrintAttributes.MediaSize.ISO_A4.getWidthMils() * 72 / 1000;

        float scale = (float) pageWidth / (float) bitmaps.get(0).getWidth();
        int pageHeight = (int) (bitmaps.get(0).getHeight() * scale);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < bitmaps.size(); i++) {
            android.graphics.pdf.PdfDocument.PageInfo newPage = new android.graphics.pdf.PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create();
            android.graphics.pdf.PdfDocument.Page page = doc.startPage(newPage);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bitmaps.get(i), matrix, paint);
            doc.finishPage(page);
        }
        File file = new File(appDir, name);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            doc.writeTo(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            doc.close();
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
