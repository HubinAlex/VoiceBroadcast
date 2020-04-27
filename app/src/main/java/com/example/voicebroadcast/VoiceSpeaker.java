package com.example.voicebroadcast;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoiceSpeaker {

    private static VoiceSpeaker sInstance;
    private ExecutorService service;

    private VoiceSpeaker() {
        service = Executors.newCachedThreadPool();
    }

    public static synchronized VoiceSpeaker getInstance() {
        if (sInstance == null) {
            sInstance = new VoiceSpeaker();
        }
        return sInstance;
    }

    public void speak(final List<String> list) {
        if (service != null) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    start(list);
                }
            });
        }
    }

    /**
     * android自带MediaPlayer进行语音播报,简单方便
     * @param list
     */
    private void start(final List<String> list) {
        synchronized (this) {//防止混合播报
            final CountDownLatch latch = new CountDownLatch(1);
            MediaPlayer player = new MediaPlayer();
            if (list != null && list.size() > 0) {
                final int[] counter = {0};
                String path = String.format("sound/tts_%s.mp3", list.get(counter[0]));
                AssetFileDescriptor fd = null;
                try {
                    //加载合成好的本地TTS语音文件
                    fd = getAssetFileDescription(path);
                    player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                    player.prepareAsync();
                    player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.reset();
                            counter[0]++;
                            if (counter[0] < list.size()) {
                                try {
                                    AssetFileDescriptor fileDescriptor =getAssetFileDescription(String.format("sound/tts_%s.mp3", list.get(counter[0])));
                                    mp.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                                    mp.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    latch.countDown();
                                }
                            } else {
                                mp.release();
                                latch.countDown();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    latch.countDown();
                } finally {
                    if (fd != null) {
                        try {
                            fd.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                latch.await();
                this.notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 资源管理器加载对应语音文件
     * @param filename
     * @return
     * @throws IOException
     */
    public static AssetFileDescriptor getAssetFileDescription(String filename) throws IOException {
        AssetManager manager = BaseApp.getContext().getAssets();
        return manager.openFd(filename);
    }
}
