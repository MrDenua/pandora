package tech.linjiang.pandora;

import android.app.Activity;
import android.support.v4.content.FileProvider;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import tech.linjiang.pandora.database.Databases;
import tech.linjiang.pandora.function.IFunc;
import tech.linjiang.pandora.inspector.attribute.AttrFactory;
import tech.linjiang.pandora.preference.SharedPref;
import tech.linjiang.pandora.util.SensorDetector;

/** Created by linjiang on 29/05/2018. */
public final class Pandora extends FileProvider implements SensorDetector.Callback {

  private static Pandora PANDORA;

  public Pandora() {
  }

  public static void setDingTalk(DingTalk.IDingTalk iDingTalk) {}

  @Override
  public boolean onCreate() {
    PANDORA = this;
    return super.onCreate();
  }

  public static Pandora get() {
    return PANDORA;
  }

  public Interceptor getInterceptor() {
    return new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request());
      }
    };
  }

  public Databases getDatabases() {
    return null;
  }

  public SharedPref getSharedPref() {
    return null;
  }

  public AttrFactory getAttrFactory() {
    return null;
  }

  /** @hide */
  public Activity getTopActivity() {
    return null;
  }

  public void addFunction(IFunc func) {}

  /** Open the panel. */
  public void open() {}

  /** Close the panel. */
  public void close() {}

  /** Disable the Shake feature. */
  public void disableShakeSwitch() {}

  @Override
  public void shakeValid() {
    open();
  }
}
