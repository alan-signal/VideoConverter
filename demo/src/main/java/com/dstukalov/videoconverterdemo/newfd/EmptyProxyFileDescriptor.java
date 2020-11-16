package com.dstukalov.videoconverterdemo.newfd;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.ParcelFileDescriptor;
import android.os.ProxyFileDescriptorCallback;
import android.os.storage.StorageManager;
import android.system.ErrnoException;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.FileDescriptor;
import java.io.IOException;

@RequiresApi(api = 26)
public final class EmptyProxyFileDescriptor {

  private static final String TAG = EmptyProxyFileDescriptor.class.getSimpleName();

  public static FileDescriptor newX(Context context) {
    StorageManager sm = context.getSystemService(StorageManager.class);

    if (sm == null) {
      throw new AssertionError("No storage manager");
    }

    HandlerThread thread = new HandlerThread("FILE");
    thread.start();
    Log.d(TAG, "Thread started");

    try {
      ParcelFileDescriptor parcelFileDescriptor = sm.openProxyFileDescriptor(ParcelFileDescriptor.MODE_READ_WRITE, new CallbackX(), new Handler(thread.getLooper()));
      Log.d(TAG, "Proxy created");

      return parcelFileDescriptor.getFileDescriptor();
    } catch (IOException e) {
      e.printStackTrace();
      throw new AssertionError(e);
    }
  }

  private static class CallbackX extends ProxyFileDescriptorCallback {

    private static final String TAG = ProxyFileDescriptorCallback.class.getSimpleName();

    public CallbackX() {
    }

    @Override
    public long onGetSize() {
      Log.d(TAG, "onGetSize()");
      return 0L;
    }

    @Override
    public int onRead(long offset, int size, byte[] data) throws ErrnoException {
      Log.d(TAG, String.format("onRead(offset: %d, size: %d, data: [%d]", offset, size, data.length));
      throw new AssertionError("Unexpected");
    }

    @Override
    public int onWrite(long offset, int size, byte[] data) throws ErrnoException {
      Log.d(TAG, String.format("onWrite(offset: %d, size: %d, data: [%d]", offset, size, data.length));
      return size;
    }

    @Override
    public void onFsync() throws ErrnoException {
      Log.d(TAG, "onFsync()");
      super.onFsync();
    }

    @Override
    public void onRelease() {
      Log.d(TAG, "onRelease()");
    }
  }
}
