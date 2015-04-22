package com.rockbase.unplugged.engine;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.FrameLayout;

public class RoundedCornerLayout extends FrameLayout {
    private final static float CORNER_RADIUS = 32.0f;
    public final static int MARGIN_BOTTOM = 32;

    private Bitmap maskBitmap;
    private Paint paint, maskPaint;
    private float cornerRadius;
    public boolean last = false;

    public RoundedCornerLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, metrics);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (width > 0 && height > 0) {
            Bitmap offscreenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas offscreenCanvas = new Canvas(offscreenBitmap);

            super.draw(offscreenCanvas);

            if (maskBitmap == null) {
                height = canvas.getHeight();
                if (!last) {
                    height -= MARGIN_BOTTOM;
                }
                maskBitmap = createMask(canvas.getWidth(), height);
            }

            offscreenCanvas.drawBitmap(maskBitmap, 0f, 0f, maskPaint);
            canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint);
        }
    }

    private Bitmap createMask(int width, int height) {
        Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(mask);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);

        canvas.drawRect(0, 0, width, height, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, paint);

        return mask;
    }

}