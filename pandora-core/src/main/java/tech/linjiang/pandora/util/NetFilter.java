package tech.linjiang.pandora.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tech.linjiang.pandora.cache.Summary;

/**
 * <pre>
 * author : ZH-301-001
 * e-mail : denua@foxmail.com
 * time   : 2019/9/30
 * desc   :
 * </pre>
 */
public class NetFilter {

    private List<Filter> filters = new ArrayList<>();
    private static final Pattern PATTERN_SYMBOL = Pattern.compile("(=)|(!=)|(~)|(!~)");
    private static NetFilter NET_FILTER = new NetFilter();
    private static boolean enable = true;

    public static boolean filter200 = false;

    public static void setup(){
        enable = Config.getNetFilterEnable();

        String filter = Config.getNetFilterStr();
        if (filter == null || filter.isEmpty()){
            return;
        }
        filter = filter.replace(" ","");
        String[] lines = filter.split("\n");
        NetFilter netFilter = new NetFilter();
        for (String line:lines){
            try{
                Matcher symbol = PATTERN_SYMBOL.matcher(line);
                if (symbol.find()){
                    String sym = symbol.group();
                    String[] s = line.split(sym);
                    Filter filter1 = new Filter();
                    filter1.type = Type.valueOf(s[0].toUpperCase());
                    filter1.value = s[1];
                    filter1.expr = Expr.get(sym);
                    netFilter.filters.add(filter1);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        NET_FILTER = netFilter;
    }

    public static NetFilter get(){
        return NET_FILTER;
    }

    public static boolean isEnable(){
        return enable;
    }

    public static void disable(){
        Config.setNetFilterEnable(false);
        enable = false;
    }

    public static void enable(){
        Config.setNetFilterEnable(true);
        enable = true;
    }


    public boolean filter(Summary summary){

        if (filter200  && summary.code == 200){
            return true;
        }
        if (!enable){
            return false;
        }
        for (Filter filter:filters){
            String typeValue = "";
            switch (filter.type){
                case URL:
                    typeValue = summary.url;
                    break;
                case HOST:
                    typeValue = summary.host;
                    break;
                case MIME:
                    typeValue = summary.response_content_type;
                    break;
                case METHOD:
                    typeValue = summary.method;
                    break;
                case CODE:
                    typeValue = String.valueOf(summary.code);
                    break;
            }
            if (filter.filter(typeValue)) return true;
        }
        return false;
    }

    private static class Filter{
        private Expr expr;
        private String  value;
        private Type type;
        boolean filter(String v){
            if (expr == null) return false;
            return expr.correct(v, value);
        }
    }


    private static class Expr{

        private static final Expr IS = new Expr("=");
        private static final Expr CONTAIN = new Expr("~");
        private static final Expr NOT_CONTAIN = new Expr("!~");
        private static final Expr NOT = new Expr("!=");

        String symbol;

        private Expr(String symbol) {
            this.symbol = symbol;
        }

        boolean correct(String v1, String v2){
            if (v1 == null || v2 == null) return false;

            switch (this.symbol){
                case "=":return v1.equals(v2);
                case "~":return v1.contains(v2);
                case "!=":return !v1.equals(v2);
                case "!~":return !v1.contains(v2);
                default:
                    throw new IllegalStateException("Unexpected value: " + this);
            }
        }

        static Expr get(String expr){
            switch (expr){
                case "=":return IS;
                case "~":return CONTAIN;
                case "!=":return NOT;
                case "!~":return NOT_CONTAIN;
            }
            return null;
        }
    }

    public enum Type{
        URL, HOST, MIME, METHOD, CODE
    }
}
