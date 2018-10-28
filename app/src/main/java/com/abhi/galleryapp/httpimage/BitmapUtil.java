
package com.abhi.galleryapp.httpimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Bimtap encoding/decoding helper methods based on BitmapFactory
 * 
 * @author abhikrpaul92
 */
public class BitmapUtil {

    private static final int UNCONSTRAINED = -1;

    public static Bitmap decodeByteArray(byte[] bytes, int maxNumOfPixels) {
        
        if (bytes == null) return null;
        
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            // Decode only image size
            option.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, option);

            option.inJustDecodeBounds = false;
            option.inSampleSize = computeSampleSize(option, UNCONSTRAINED,
                    maxNumOfPixels);

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, option);//scaleCenterCrop(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, option),133,220);

        } catch (OutOfMemoryError oom) {

        	oom.printStackTrace();
            return null;
        }
    }

    public static Bitmap decodeStream(InputStream is, int maxNumOfPixels) {

        if (is == null) return null;
        
        try {
            return decodeByteArray( readStream(is), maxNumOfPixels);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public static Bitmap decodeFile(String filePath, int maxNumOfPixels) {
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(filePath));
            return decodeByteArray(readStream(fis), maxNumOfPixels);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(fis != null) {
                try { fis.close(); } catch (IOException e) {}
            }
        }
        
    }
    
    /*
     * Compute the sample size as a function of minSideLength and
     * maxNumOfPixels. minSideLength is used to specify that minimal width or
     * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
     * pixels that is tolerable in terms of memory usage.
     * 
     * The function returns a sample size based on the constraints. Both size
     * and minSideLength can be passed in as IImage.UNCONSTRAINED, which
     * indicates no care of the corresponding constraint. The functions prefers
     * returning a sample size that generates a smaller bitmap, unless
     * minSideLength = IImage.UNCONSTRAINED.
     * 
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way. For example,
     * BitmapFactory downsamples an image by 2 even though the request is 3. So
     * we round up the sample size to avoid OOM.
     */
    private static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
                .ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    
    private static byte[] readStream(InputStream is) throws IOException {
        int len;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            int size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[1024];
            while ((len = is.read(buf, 0, 1024)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        
        return buf;
    }
}
