package tech.linjiang.pandora;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import tech.linjiang.pandora.cache.Summary;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.Utils;

/**
 *
 *
 * <pre>
 * author : ZH-301-001
 * e-mail : denua@foxmail.com
 * time   : 2019/10/3
 * desc   :
 * </pre>
 */
public class DingTalk {

  public static IDingTalk iDingTalk;
  public static String TOKEN = null;

  public static void send(final String msg){
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  report(Template.INSTANCE.getPlainText(msg));
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            })
            .start();
  }

  public static void gitlab(final Summary summary) {

    if (iDingTalk != null) {
      iDingTalk.message(Template.INSTANCE.getMarkdown(summary));
    }
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                try {
                  report(Template.INSTANCE.getMarkdown(summary));
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            })
        .start();
  }

  private static void report(String json) throws IOException {

    if (TOKEN == null) {
      TOKEN = Config.getDingTalkToken();
    }
    Looper looper = Looper.getMainLooper();
    Handler handler = new Handler(looper);

    URL url = new URL("https://oapi.dingtalk.com/robot/send?access_token=" + TOKEN);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("POST");
    connection.addRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);

    OutputStream outputStream = connection.getOutputStream();
    OutputStreamWriter writer = new OutputStreamWriter(outputStream);
    writer.write(json);
    writer.flush();

    InputStream inputStream = connection.getInputStream();
    InputStreamReader reader = new InputStreamReader(inputStream);
    final StringBuilder builder = new StringBuilder();
    int c;
    while ((c = reader.read()) != -1) {
      builder.append((char) c);
    }
    handler.post(
        new Runnable() {
          @Override
          public void run() {
            Utils.toast(builder.toString());
          }
        });
  }

  public interface IDingTalk {
    void message(String jsonBody);
  }
}
