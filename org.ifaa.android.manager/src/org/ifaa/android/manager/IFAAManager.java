package org.ifaa.android.manager;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

public abstract class IFAAManager
{
  static
  {
    System.loadLibrary("ifaa_jni");
  }

  public abstract String getDeviceModel();

  public abstract int getSupportBIOTypes(Context paramContext);

  public abstract int getVersion();

  public native byte[] processCmd(Context paramContext, byte[] paramArrayOfByte);

  public abstract int startBIOManager(Context paramContext, int paramInt);
}
