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
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.pgu.translate.client.TranslateService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TranslateServiceImpl extends RemoteServiceServlet implements TranslateService {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private enum LG {
        en, fr, es, it, de, ja, cn("zh-CN"), ko, ru, ar;

        private final String code;

        LG() {
            this(null);
        }

        LG(final String code) {
            this.code = code;
        }

        public String q(final String word, final LG source) {
            if (source == this) {
                return "";
            }

            try {
                return "http://translate.google.com/translate_a/t?client=t&hl=en" + //
                        "&sl=" + source + //
                        "&tl=" + (code == null ? this : code) + //
                        "&text=" + URLEncoder.encode(word, UTF8.name());

            } catch (final UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public static HashMap<LG, String> urls(final String word, final String source) {
            final LG lgSource = LG.valueOf(source);

            final HashMap<LG, String> urls = new HashMap<LG, String>();

            for (final LG lg : LG.values()) {
                final String q = lg.q(word, lgSource);
                if (!"".equals(q)) {
                    urls.put(lg, q);
                }
            }
            return urls;
        }

        //        http://a4esl.org/c/charset.html
        public Charset charset() {
            //            en, fr, es, it, de, ja, cn("zh-CN"), ko, ru, ar;
            if (this == fr //
                    || this == de //
                    || this == es //
                    || this == it //
            ) {
                return Charset.forName("iso-8859-1");
            }
            if (this == ja) {
                return Charset.forName("shift-jis");
            }
            if (this == cn) {
                return Charset.forName("EUC-CN");
            }
            if (this == ko) {
                return Charset.forName("euc-kr");
            }
            if (this == ru) {
                return Charset.forName("iso-8859-5");
            }
            if (this == ar) {
                return Charset.forName("iso-8859-6");
            }
            return UTF8;
        }
    }

    @Override
    public HashMap<String, String> translate(final String word, final String source) {

        final HashMap<LG, String> urls = LG.urls(word, source);

        final HashMap<String, String> lg2result = new HashMap<String, String>();

        for (final Entry<LG, String> e : urls.entrySet()) {
            try {
                final URL url = new URL(e.getValue());
                final URLConnection connection = url.openConnection();
                final LG lg = e.getKey();

                connection.setRequestProperty("Accept-Charset", lg.charset().name());
                final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), lg.charset()));

                final StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                //                int cp;
                //                while ((cp = reader.read()) != -1) {
                //                    sb.append((char) cp);
                //                }
                reader.close();

                final String result = sb.toString();
                System.out.println(result);
                lg2result.put(lg.toString(), result);

            } catch (final MalformedURLException ex) {
                System.out.println(ex);
            } catch (final IOException ex) {
                System.out.println(ex);
            }
        }
        return lg2result;
    }

}
