package com.covrsecurity.io.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.covrsecurity.io.R;

public class FaceFocusView extends View {

    private Paint mStrokePaint;
    private Paint mFontPaint;
    private Path mPath = new Path();

    public FaceFocusView(Context context) {
        super(context);
        initPaints();
    }

    public FaceFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public FaceFocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    private void initPaints() {

        final int covrGreen = getResources().getColor(R.color.covr_green);
        final float strokeWidth = getResources().getDimension(R.dimen.six_dp);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(covrGreen);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(strokeWidth);

        mFontPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFontPaint.setColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();
        mPath.addOval(0, 0, getWidth(), getHeight(), Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        canvas.drawOval(0, 0, getWidth(), getHeight(), mStrokePaint);
        canvas.drawPath(mPath, mFontPaint);
        canvas.clipPath(mPath);
        canvas.drawColor(Color.WHITE);
    }
}