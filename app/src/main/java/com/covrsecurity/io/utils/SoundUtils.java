package com.covrsecurity.io.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.covrsecurity.io.R;
import com.covrsecurity.io.app.AppAdapter;

import timber.log.Timber;

public class SoundUtils {
    public static int KB_SOUND = R.raw.click;
    public static int RESPONSE_SOUND = R.raw.whoosh;
    public static int TIMEOUT_SOUND = R.raw.timeout;

    private static SoundPool soundPool = null;
    private static AudioManager audioManager = null;
    private static SparseIntArray sounds;
    private static int soundsLoaded = 0;
    private static boolean soundPoolLoaded = false;

    public static void initSoundPool(Context context, final int[] soundsToLoad, IInitFinishedListener listener) {
        if (soundPool != null) {
            Timber.d("Sound pool already exists - release previous one and initiate new one.");
            releaseSoundPool();
        }
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sounds = new SparseIntArray();
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundsLoaded++;
                if (soundsLoaded >= soundsToLoad.length) {
                    soundPoolLoaded = true;
                    listener.onInitFinished();
                }
            }
        });
        for (int aSoundsToLoad : soundsToLoad) {
            sounds.put(aSoundsToLoad, soundPool.load(context, aSoundsToLoad, 1));
        }
    }

    public static void playSound(int soundResId) {
        ActivityUtils.runOnMainThread(() -> {
            if (soundPool != null && soundPoolLoaded && sounds.indexOfKey(soundResId) > 0) {
                if (audioManager != null) {
                    float soundLevel = (float) audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
                    soundPool.play(sounds.get(soundResId), soundLevel, soundLevel, 1, 0, 1f);
                } else {
                    soundPool.play(sounds.get(soundResId), 1f, 1f, 1, 0, 1f);
                }
            } else {
                Timber.e("Could not play sound %d - sound pool wasn't initialized or the sound wasn't properly loaded", soundResId);
            }
        });
    }

    public static void releaseSoundPool() {
        Timber.d("Release sound pool");
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            soundsLoaded = 0;
            soundPoolLoaded = false;
        }
    }

    public static boolean isSoundPoolLoaded() {
        return soundPoolLoaded;
    }

    public interface IInitFinishedListener {
        void onInitFinished();
    }

    public static void playResponseSound() {
        if (AppAdapter.settings().getInappResponseSoundEnabled()) {
            SoundUtils.playSound(SoundUtils.RESPONSE_SOUND);
        }
    }

    public static void playTimeoutSound() {
        if (AppAdapter.settings().getInappTimeoutSoundEnabled()) {
            SoundUtils.playSound(SoundUtils.TIMEOUT_SOUND);
        }
    }
}
