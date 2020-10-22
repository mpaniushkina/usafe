package com.covrsecurity.io.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Pair;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.handlers.ImageHandler;

import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HtmlUtils {

    public static Single<Pair<DrawableImageHandler, Spannable>> loadHtmlText(String url, DisplayMetrics displayMetrics) {
        return Single.fromCallable(() -> {
            DrawableImageHandler imageHandler = new DrawableImageHandler();
            HashMap<String, TagNodeHandler> map = new HashMap<>();
            map.put("img", imageHandler);
            Spannable spannable = (new HtmlSpanner(map, displayMetrics)).fromHtml(url);
            return new Pair<>(imageHandler, (Spannable) StringUtils.trimTrailingWhitespace(spannable));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static class DrawableImageHandler extends ImageHandler {

        private List<Pair<String, LevelListDrawable>> pairList = new ArrayList<>();

        public List<Pair<String, LevelListDrawable>> getPairList() {
            return pairList;
        }

        @Override
        public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack stack) {
            String imageUrl = node.getAttributeByName("src");

            builder.append("\uFFFC");

            LevelListDrawable levelListDrawable = new LevelListDrawable();

            Bitmap placeholderBitmap = loadBitmap(null);
            Drawable placeholderDrawable = new BitmapDrawable(placeholderBitmap);
//            Drawable placeholderDrawable = CovrApp.getInstance().getResources().getDrawable(R.drawable.launcher);

            levelListDrawable.addLevel(0, 0, placeholderDrawable);
            levelListDrawable.setBounds(0, 0, placeholderDrawable.getIntrinsicWidth(), placeholderDrawable.getIntrinsicHeight());
            stack.pushSpan(new ImageSpan(levelListDrawable), start, builder.length());

            pairList.add(new Pair<>(imageUrl, levelListDrawable));
        }

        @Override
        protected Bitmap loadBitmap(String url) {
            return createImage(1, 1, 0);
        }

        private Bitmap createImage(int width, int height, int color) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(color);
            canvas.drawRect(0F, 0F, (float) width, (float) height, paint);
            return bitmap;
        }
    }
}