package tech.linjiang.pandora;

import java.util.HashMap;

import tech.linjiang.pandora.cache.Summary;

/**
 * <pre>
 * author : ZH-301-001
 * e-mail : denua@foxmail.com
 * time   : 2019/10/3
 * desc   :
 * </pre>
 */
public class DingTalk {

    public static IDingTalk iDingTalk;



    public static void gitlab(Summary summary){

    }

    public interface IDingTalk{
        void message(HashMap<String, String> header, String jsonBody);
    }
}