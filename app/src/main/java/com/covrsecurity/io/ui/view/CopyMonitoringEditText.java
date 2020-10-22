package com.covrsecurity.io.ui.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class CopyMonitoringEditText extends androidx.appcompat.widget.AppCompatEditText {

    public static final String TAG = CopyMonitoringEditText.class.getSimpleName();

    private ClipboardManager mClipboard;

    public CopyMonitoringEditText(Context context) {
        super(context);
        init();
    }

    public CopyMonitoringEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CopyMonitoringEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mClipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * <p>This is where the "magic" happens.</p>
     * <p>The menu used to cut/copy/paste is a normal ContextMenu, which allows us to
     * overwrite the consuming method and react on the different events.</p>
     *
     * @see <a href="http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3_r1/android/widget/TextView.java#TextView.onTextContextMenuItem%28int%29">Original Implementation</a>
     */
    @Override
    public boolean onTextContextMenuItem(int id) {
        final StringBuilder initialText = new StringBuilder(getText());
        final int selectionStart = getSelectionStart();
        final int selectionEnd = getSelectionEnd();
        boolean consumed = super.onTextContextMenuItem(id);
        if (id == android.R.id.paste) {
            ClipData abc = mClipboard.getPrimaryClip();
            ClipData.Item item = abc.getItemAt(0);
            String pastedText = item.getText().toString();
            initialText.replace(selectionStart, selectionEnd, pastedText);
            getText().replace(0, getText().length(), initialText);
        }
        return consumed;
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        super.addTextChangedListener(watcher);
    }
}
