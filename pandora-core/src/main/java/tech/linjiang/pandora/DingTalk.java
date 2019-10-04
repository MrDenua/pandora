package tech.linjiang.pandora;

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

  public interface IDingTalk {
    void message(String jsonBody);
  }
}
