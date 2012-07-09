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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Test {

    private enum enumTest {
        fr, en
    }

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

        enumTest.valueOf(""); // IllegalArgumentException

        final String s1 = "[[[\"welcome\",\"welcome\",\"\",\"\"]],,\"en\",,,,,,[[\"en\"]],0]";
        final String s2 = "[[[\"b\",\"b\",\"\",\"\"]],,\"en\",,,,,,,0]";
        final String s3 = "[[[\"because\",\"bo\",\"\",\"\"]],[[\"conjunction\",[\"because\",\"for\",\"as\",\"or else\",\"or\"],[[\"because\",[\"ponieważ\",\"bo\",\"gdyż\",\"dlatego\",\"że\",\"bowiem\"]],[\"for\",[\"aby\",\"bo\",\"ponieważ\",\"albowiem\",\"bowiem\",\"ażeby\"]],[\"as\",[\"jak\",\"gdy\",\"kiedy\",\"bo\",\"skoro\",\"aczkolwiek\"]],[\"or else\",[\"bo inaczej\",\"bo\"]],[\"or\",[\"lub\",\"albo\",\"czy\",\"ani\",\"bądź\",\"bo\"]]]],[\"\",[\"but then\"],[[\"but then\",[\"bo\"]]]],[\"phrase\",[\"that's true\",\"such as it is\"],[[\"that's true\",[\"bo\"]],[\"such as it is\",[\"bo\"]]]]],\"pl\",,[[\"because\",[5],1,0,846,0,1,0]],[[\"bo\",4,,,\"\"],[\"bo\",5,[[\"because\",846,1,0],[\"for\",78,1,0],[\"as\",75,1,0],[\"since\",0,1,0],[\"because the\",0,1,0]],[[0,2]],\"bo\"]],,,,2]";
        final String s4 = "[[[\"good\",\"bon\",\"\",\"\"]],[[\"adjective\",[\"good\",\"right\",\"well\",\"nice\",\"kind\",\"favorable\",\"comfortable\",\"kindly\",\"reliable\",\"lovely\",\"beneficial\",\"dainty\",\"open-hearted\"],[[\"good\",[\"bon\",\"beau\",\"agréable\",\"favorable\",\"brave\",\"grand\"]],[\"right\",[\"droit\",\"bon\",\"juste\",\"bien\",\"vrai\",\"exacte\"]],[\"well\",[\"bon\"]],[\"nice\",[\"agréable\",\"bien\",\"beau\",\"gentil\",\"bon\",\"joli\"]],[\"kind\",[\"aimable\",\"bon\",\"gentil\",\"généreux\",\"obligeant\",\"bienfaisant\"]],[\"favorable\",[\"favorable\",\"propice\",\"bon\",\"intéressant\"]],[\"comfortable\",[\"confortable\",\"à l'aise\",\"agréable\",\"bon\",\"rassurant\",\"moelleux\"]],[\"kindly\",[\"bon\",\"gentil\",\"bienfaisant\",\"généreux\",\"plein de bonté\",\"plein de gentillesse\"]],[\"reliable\",[\"fiable\",\"sûr\",\"digne de confiance\",\"efficace\",\"solide\",\"bon\"]],[\"lovely\",[\"charmant\",\"agréable\",\"joli\",\"magnifique\",\"aimable\",\"bon\"]],[\"beneficial\",[\"avantageux\",\"salutaire\",\"favorable\",\"bon\"]],[\"dainty\",[\"délicat\",\"mignon\",\"bon\",\"chic\"]],[\"open-hearted\",[\"franc\",\"sincère\",\"qui a bon cœur\",\"bon\"]]]],[\"interjection\",[\"right\",\"well\",\"so\"],[[\"right\",[\"bon\",\"bien\",\"c'est ça\"]],[\"well\",[\"bien\",\"bon\",\"eh bien\",\"tout va bien\",\"ah bien\"]],[\"so\",[\"bon\"]]]],[\"noun\",[\"voucher\",\"bond\",\"warrant\"],[[\"voucher\",[\"bon\",\"pièce justificative\",\"reçu\",\"récépissé\",\"garant\",\"quittance\"]],[\"bond\",[\"liaison\",\"lien\",\"obligation\",\"bon\",\"engagement\",\"titre\"]],[\"warrant\",[\"mandat\",\"bon\",\"mandat d'arrêt\",\"garantie\",\"justification\",\"brevet\"]]]],[\"adverb\",[\"then\"],[[\"then\",[\"puis\",\"alors\",\"ensuite\",\"lors\",\"aussi\",\"bon\"]]]]],\"fr\",,[[\"good\",[5],1,0,975,0,1,0]],[[\"bon\",4,,,\"\"],[\"bon\",5,[[\"good\",975,1,0],[\"right\",20,1,0],[\"well\",4,1,0],[\"proper\",0,1,0],[\"a good\",0,1,0]],[[0,3]],\"bon\"]],,,[[\"fr\",\"en\"]],8]";
        final String s5 = "[[[\"bonj\",\"bonj\",\"\",\"\"]],,\"id\",,[[\"bonj\",[5],1,0,1000,0,1,0]],[[\"bonj\",5,[[\"bonj\",1000,1,0]],[[0,4]],\"bonj\"]],,,[[\"id\"]],1]";
        final String s6 = "[[[\"bonjou\",\"bonjou\",\"\",\"\"]],,\"fr\",,[[\"bonjou\",[5],1,0,1000,0,1,0]],[[\"bonjou\",5,[[\"bonjou\",1000,1,0]],[[0,6]],\"bonjou\"]],,,[[\"fr\"]],1]";
        final String s7 = "[[[\"hello\",\"bonjour\",\"\",\"\"]],[[\"interjection\",[\"hello\",\"good morning\",\"hi\",\"good afternoon\",\"welcome\",\"hullo\",\"hallo\",\"how do you do\"],[[\"hello\",[\"bonjour\",\"salut\",\"allô\",\"tiens\"]],[\"good morning\"],[\"hi\",[\"salut\",\"bonjour\",\"hé\"]],[\"good afternoon\",[\"bonjour\"]],[\"welcome\",[\"soyez le bienvenu\",\"salut\",\"bonjour\"]],[\"hullo\",[\"bonjour\",\"allô\",\"salut\",\"tiens\"]],[\"hallo\",[\"bonjour\",\"salut\",\"allô\",\"tiens\"]],[\"how do you do\",[\"bonjour\",\"ça marche\",\"salut\"]]]],[\"\",[\"Hello\"],[[\"Hello\"]]]],\"fr\",,[[\"hello\",[5],1,0,992,0,1,0]],[[\"bonjour\",4,,,\"\"],[\"bonjour\",5,[[\"hello\",992,1,0],[\"hi\",7,1,0],[\"good morning\",0,1,0],[\"morning\",0,1,0],[\"Bonjour\",0,1,0]],[[0,7]],\"bonjour\"]],,,[[\"fr\",\"en\",\"de\"]],7]";

        final List<String> ss = Arrays.asList(s1, s2, s3, s4, s5, s6, s7);
        for (final String s : ss) {

            int counterSlot = 0;
            int counterSqBracket = 0;
            boolean startsRecord = false;
            final int length = s.length();
            final StringBuffer sb = new StringBuffer();

            for (int i = 0; i < length; i++) {
                final char c = s.charAt(i);
                if (c == '[') {
                    counterSqBracket++;
                } else if (c == ']') {
                    counterSqBracket--;
                } else if (c == ',') {
                    if (counterSqBracket == 1) {
                        counterSlot++;
                        startsRecord = counterSlot == 2;
                    }
                } else if (c == '"') {
                    continue;
                } else {
                    if (startsRecord) {
                        sb.append(c);
                    }
                }

            }

            System.out.println(sb.toString());
        }

    }

    private static void testGetTranslation() throws UnsupportedEncodingException, MalformedURLException, IOException {
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
