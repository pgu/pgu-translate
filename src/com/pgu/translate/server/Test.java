package com.pgu.translate.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Test {

    private static final List<String> values       = new ArrayList<String>();
    private static boolean            isOver       = false;

    private static final String       EN           = "en";
    private static final String       FR           = "fr";
    private static final String       ES           = "es";
    private static final String       DE           = "de";
    private static final String       IT           = "it";
    private static final String       JA           = "ja";
    private static final String       CN           = "zh-CN";
    private static final String       KO           = "ko";
    private static final String       RU           = "ru";
    private static final String       AR           = "ar";
    private static final String       CS_UTF8      = "UTF-8";
    private static final String       CS_WEST      = "iso-8859-1";
    private static final String       CS_JAP_JIS   = "shift-jis";
    private static final String       CS_JAP_EUC   = "x-euc-jp";
    private static final String       CS_CN_EUC    = "EUC-CN";
    private static final String       CS_CN_GB2312 = "gb2312";
    private static final String       CS_KO        = "euc-kr";
    private static final String       CS_RU        = "koi8-r";
    private static final String       CS_AR        = "iso-8859-6";

    public static void main(final String[] args) throws InterruptedException, IOException {
        //        testCharsetCodes();

        //        final String word = "école";
        //        final String word = URLEncoder.encode("école", CS_WEST);
        final String word = URLEncoder.encode("merci", CS_UTF8);
        System.out.println(word);
        final String source = FR;
        final Map<String, String> lg2charset = lg2charset(source);
        for (final Entry<String, String> e : lg2charset.entrySet()) {

            final String to = e.getKey();
            final String charset = e.getValue();

            final String _url = "http://translate.google.com/translate_a/t?client=t&hl=en" + //
                    "&sl=" + source + //
                    "&tl=" + to + //
                    "&text=" + word;

            getTranslation(charset, _url);
        }
    }

    private static void getTranslation(final String charset, final String _url) throws MalformedURLException,
            IOException, UnsupportedEncodingException {
        final URL url = new URL(_url);
        final URLConnection connection = url.openConnection();

        connection
                .setRequestProperty("User-Agent",
                        "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071615 Fedora/3.0.1-1.fc9 Firefox/3.0.1");
        //                connection.setRequestProperty("Accept-Charset", lg.charset().name());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));

        final StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();

        System.out.println(sb.toString());
    }

    private static Map<String, String> lg2charset(final String lg) {
        final Map<String, String> lg2charset = new LinkedHashMap<String, String>();

        if (EN.equals(lg)) {
            lg2charset.put(FR, CS_UTF8);
            lg2charset.put(ES, CS_UTF8);
            lg2charset.put(IT, CS_UTF8);
            lg2charset.put(DE, CS_UTF8);
            //            lg2charset.put(JA, CS_JAP_JIS);
            lg2charset.put(JA, CS_UTF8);
            lg2charset.put(CN, CS_UTF8);
            //            lg2charset.put(CN, CS_CN_EUC);
            //            lg2charset.put(CN, CS_CN_GB2312);
            lg2charset.put(KO, CS_UTF8);
            lg2charset.put(RU, CS_UTF8);
            lg2charset.put(AR, CS_UTF8);

        } else if (FR.equals(lg)) {
            lg2charset.put(EN, CS_UTF8);
            lg2charset.put(ES, CS_UTF8);
            lg2charset.put(IT, CS_UTF8);
            lg2charset.put(DE, CS_UTF8);
            //            lg2charset.put(JA, CS_JAP_JIS);
            //            lg2charset.put(JA, CS_JAP_EUC);
            lg2charset.put(JA, CS_UTF8);
            lg2charset.put(CN, CS_UTF8);
            //            lg2charset.put(CN, CS_CN_EUC);
            //            lg2charset.put(CN, CS_CN_GB2312);
            lg2charset.put(KO, CS_UTF8);
            lg2charset.put(RU, CS_UTF8);
            lg2charset.put(AR, CS_UTF8);
        }

        return lg2charset;
    }

    private static void testIndexLooping() throws InterruptedException {
        values.add("000");
        values.add("020");
        values.add("040");
        values.add("060");
        values.add("080");
        values.add("100");

        final Runnable runnable = new Runnable() {

            int idx = 0;

            @Override
            public void run() {
                if (!isOver) {
                    System.out.println(values.get(idx++ % values.size()));
                    try {
                        Thread.sleep(500);
                        new Thread(this).run();

                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable).run();

        Thread.sleep(2000);
        isOver = true;
    }

    private static void testCharsetCodes() {
        final Charset cs = Charset.forName("UTF-8");

        System.out.println(cs.name());
        System.out.println(Charset.forName("euc-kr"));
        System.out.println(Charset.forName("iso-8859-1"));
        System.out.println(Charset.forName("iso-8859-6"));
        System.out.println(Charset.forName("shift-jis"));
        System.out.println(Charset.forName("EUC-CN"));
        System.out.println(Charset.forName("iso-8859-5"));
        System.out.println(Charset.forName("koi8-r"));
        System.out.println(Charset.forName("x-euc-jp"));
    }

}
