package com.pgu.translate.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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

    private static final Logger LOG  = Logger.getLogger(TranslateServiceImpl.class.getSimpleName());

    private static final String UTF8 = "UTF-8";

    private enum LG {
        en, //
        fr, //
        es, //
        it, //
        de, //
        ja, //
        cn("zh-CN"), //
        ko, //
        ru, //
        ar;

        private String code = null;

        LG() {
        }

        LG(final String code) {
            this.code = code;
        }

        public String q(final String word, final LG source) {
            try {
                return "http://translate.google.com/translate_a/t?client=t&hl=en" + //
                        "&sl=" + source + //
                        "&tl=" + (code == null ? this : code) + //
                        "&text=" + URLEncoder.encode(word, UTF8);

            } catch (final UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public static HashMap<LG, String> urls(final String word, final String source) {
            final LG lgSource = LG.valueOf(source);

            final HashMap<LG, String> urls = new HashMap<LG, String>();

            for (final LG lg : LG.values()) {
                if (lgSource == lg) {
                    continue;
                }

                urls.put(lg, lg.q(word, lgSource));
            }
            return urls;
        }

    }

    @Override
    public HashMap<String, String> translate(final String word, final String source) {

        final boolean isDevelopmentEnvironment = isDevelopmentEnvironment();

        final HashMap<String, String> lg2result = new HashMap<String, String>();

        final HashMap<LG, String> urls = LG.urls(word, source);
        for (final Entry<LG, String> e : urls.entrySet()) {
            try {
                final LG lg = e.getKey();
                final String _url = e.getValue();

                final URL url = new URL(_url);
                final URLConnection connection = url.openConnection();
                // trick to get the proper encoding!
                connection
                        .setRequestProperty("User-Agent",
                                "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071615 Fedora/3.0.1-1.fc9 Firefox/3.0.1");

                final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                        UTF8));

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
