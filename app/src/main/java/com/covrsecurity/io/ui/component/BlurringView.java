package com.covrsecurity.io.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;

import com.covrsecurity.io.R;

/**
 * Created by DellUser on 26.12.2016.
 */
public class BlurringView extends View {
    private static final int DEFAULT_BLUR_RADIUS = 15;
    private static final int DEFAULT_BLUR_FACTOR = 1;
    private static final int DEFAULT_OVERLAY_COLOR = Color.TRANSPARENT;

    private RenderScript mRenderScript;
    private ScriptIntrinsicBlur mBlurScript;
    private Allocation mBlurInput, mBlurOutput;

    private int mBlurRadius;
    private int mDownsampleFactor;
    private int mOverlayColor;

    private View mBlurredView;
    private int mBlurredViewWidth, mBlurredViewHeight;

    private boolean mDownsampleFactorChanged;
    private Bitmap mBitmapToBlur, mBlurredBitmap;
    private Canvas mBlurringCanvas;


    public BlurringView(Context context) {
        this(context, null);
    }

    public BlurringView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeRenderScript(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlurringView);
        setBlurRadius(a.getInt(R.styleable.BlurringView_blurRadius, DEFAULT_BLUR_RADIUS));
        setDownsampleFactor(a.getInt(R.styleable.BlurringView_downsampleFactor,
                DEFAULT_BLUR_FACTOR));
        setOverlayColor(a.getColor(R.styleable.BlurringView_overlayColor, DEFAULT_OVERLAY_COLOR));
        a.recycle();
    }

    public void setBlurredView(View blurredView) {
        mBlurredView = blurredView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBlurredView != null) {
            if (prepare()) {
                if (mBlurredView.getBackground() != null && mBlurredView.getBackground() instanceof ColorDrawable) {
                    mBitmapToBlur.eraseColor(((ColorDrawable) mBlurredView.getBackground()).getColor());
                } else {
                    mBitmapToBlur.eraseColor(Color.TRANSPARENT);
                }

                mBlurredView.draw(mBlurringCanvas);
                blur();

                canvas.save();
                //canvas.translate(mBlurredView.getX() - getX(), mBlurredView.getY() - getY());
                canvas.scale(mDownsampleFactor, mDownsampleFactor);
                canvas.drawBitmap(mBlurredBitmap, 0, 0, null);
                canvas.restore();
            }
            //canvas.drawColor(mOverlayColor);
        }
    }

    public void setBlurRadius(int radius) {
        mBlurScript.setRadius(radius);
    }

    public void setDownsampleFactor(int factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Downsample factor must be greater than 0.");
        }

        if (mDownsampleFactor != factor) {
            mDownsampleFactor = factor;
            mDownsampleFactorChanged = true;
        }
    }

    public void setOverlayColor(int color) {
        mOverlayColor = color;
    }

    private void initializeRenderScript(Context context) {
        mRenderScript = RenderScript.create(context);
        mBlurScript = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
    }

    protected boolean prepare() {
        final int width = mBlurredView.getWidth();
        final int height = mBlurredView.getHeight();

        if (mBlurringCanvas == null || mDownsampleFactorChanged
                || mBlurredViewWidth != width || mBlurredViewHeight != height) {
            mDownsampleFactorChanged = false;

            mBlurredViewWidth = width;
            mBlurredViewHeight = height;

            int scaledWidth = width / mDownsampleFactor;
            int scaledHeight = height / mDownsampleFactor;

            // The following manipulation is to avoid some RenderScript artifacts at the edge.
            scaledWidth = scaledWidth - scaledWidth % 4 + 4;
            scaledHeight = scaledHeight - scaledHeight % 4 + 4;

            if (mBlurredBitmap == null
                    || mBlurredBitmap.getWidth() != scaledWidth
                    || mBlurredBitmap.getHeight() != scaledHeight) {
                mBitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight,
                        Bitmap.Config.ARGB_8888);
                if (mBitmapToBlur == null) {
                    return false;
                }

                mBlurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight,
                        Bitmap.Config.ARGB_8888);
                if (mBlurredBitmap == null) {
                    return false;
                }
            }

            mBlurringCanvas = new Canvas(mBitmapToBlur);
            mBlurringCanvas.scale(1f / mDownsampleFactor, 1f / mDownsampleFactor);
            mBlurInput = Allocation.createFromBitmap(mRenderScript, mBitmapToBlur,
                    Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mBlurOutput = Allocation.createTyped(mRenderScript, mBlurInput.getType());
        }
        return true;
    }

    protected void blur() {
        mBlurInput.copyFrom(mBitmapToBlur);
        mBlurScript.setInput(mBlurInput);
        mBlurScript.forEach(mBlurOutput);
        mBlurOutput.copyTo(mBlurredBitmap);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRenderScript != null) {
            mRenderScript.destroy();
        }
    }
}
