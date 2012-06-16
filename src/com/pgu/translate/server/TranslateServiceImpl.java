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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.pgu.translate.client.TranslateService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TranslateServiceImpl extends RemoteServiceServlet implements TranslateService {

    private static final Logger LOG = Logger.getLogger(TranslateServiceImpl.class.getSimpleName());

    private enum CHARSET { // http://a4esl.org/c/charset.html
        UTF8("UTF-8"), //
        WESTERN("iso-8859-1"), //
        JAPANESE("shift-jis"), //
        CHINESE_SIMPLIFIED("EUC-CN"), //
        KOREAN("euc-kr"), //
        CYRILLIC("koi8-r"), //
        ARABIC("iso-8859-6");

        private final String code;

        CHARSET(final String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }
    }

    private enum LG {
        en(CHARSET.WESTERN), //
        fr(CHARSET.WESTERN), //
        es(CHARSET.WESTERN), //
        it(CHARSET.WESTERN), //
        de(CHARSET.WESTERN), //
        ja(CHARSET.JAPANESE), //
        cn(CHARSET.CHINESE_SIMPLIFIED, "zh-CN"), //
        ko(CHARSET.KOREAN), //
        ru(CHARSET.CYRILLIC), //
        ar(CHARSET.ARABIC);

        private final String  code;
        private final CHARSET charset;

        LG(final CHARSET charset) {
            this(charset, null);
        }

        LG(final CHARSET charset, final String code) {
            this.charset = charset;
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
                        "&text=" + URLEncoder.encode(word, CHARSET.WESTERN.code());

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

        public Charset charset() {
            return Charset.forName(charset.code());
        }
    }

    @Override
    public HashMap<String, String> translate(final String word, final String source) {

        final boolean isDevelopmentEnvironment = isDevelopmentEnvironment();

        final HashMap<String, String> lg2result = new HashMap<String, String>();

        final HashMap<LG, String> urls = LG.urls(word, source);
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
                reader.close();

                final String result = sb.toString();
                if (isDevelopmentEnvironment) {
                    System.out.println(result);
                }
                lg2result.put(lg.toString(), result);

            } catch (final MalformedURLException ex) {
                LOG.log(Level.SEVERE, "ouch!", ex);
            } catch (final IOException ex) {
                LOG.log(Level.SEVERE, "ouch!", ex);
            }
            // TODO PGU to delete when ui testing is done
            // break; // just one request for testing
        }
        return lg2result;
    }

    private static boolean isDevelopmentEnvironment() {
        return SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
    }

}
