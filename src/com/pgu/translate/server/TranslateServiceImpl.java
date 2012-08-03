package com.pgu.translate.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
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
                        "&sl=" + (source == null ? "auto" : source) + //
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
        final URLFetchService service = getUrlFetchService();

        final ArrayList<Future<HTTPResponse>> futures = new ArrayList<Future<HTTPResponse>>();
        final HashMap<Future<HTTPResponse>, LG> future2lg = new HashMap<Future<HTTPResponse>, LG>();

        for (final Entry<LG, String> lg2url : LG.urls(word, source).entrySet()) {
            try {

                final HTTPRequest req = new HTTPRequest(new URL(lg2url.getValue()));
                req.addHeader(new HTTPHeader("User-Agent", // trick to get the proper encoding
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.56 Safari/536.5"));

                final Future<HTTPResponse> future = service.fetchAsync(req);

                futures.add(future);
                future2lg.put(future, lg2url.getKey());

            } catch (final MalformedURLException ex) {
                LOG.log(Level.SEVERE, "ouch!", ex);
                throw new RuntimeException(ex);
            }
        }

        boolean allFuturesAreDone = false;
        while (!allFuturesAreDone) {

            try {
                Thread.sleep(10);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean areDone = true;
            for (final Future<HTTPResponse> f : futures) {
                areDone &= f.isDone();
            }

            allFuturesAreDone = areDone;
        }

        final HashMap<String, String> lg2result = new HashMap<String, String>();

        for (final Future<HTTPResponse> future : futures) {
            try {

                final ByteArrayInputStream bais = new ByteArrayInputStream(futureGet(future).getContent());
                final BufferedReader reader = new BufferedReader(new InputStreamReader(bais, UTF8));

                final StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();

                final String tsl = sb.toString();

                if (isDevelopmentEnvironment) {
                    System.out.println(tsl);
                }

                lg2result.put(future2lg.get(future).toString(), tsl);

            } catch (final IOException ex) {
                LOG.log(Level.SEVERE, "ouch!", ex);
                throw new RuntimeException(ex);
            }
        }

        return lg2result;
    }

    private URLFetchService getUrlFetchService() {
        return URLFetchServiceFactory.getURLFetchService();
    }

    private HTTPResponse futureGet(final Future<HTTPResponse> future) {
        HTTPResponse httpResponse;
        try {
            httpResponse = future.get();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
        return httpResponse;
    }

    private boolean isDevelopmentEnvironment() {
        return SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
    }

    @Override
    public String detectLanguage(final String word) {

        try {

            final URL url = new URL(LG.en.q(word, null));
            final URLConnection connection = url.openConnection();
            connection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.56 Safari/536.5");

            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF8));

            final String line = reader.readLine();
            reader.close();

            final String lg = extractDetectedLanguage(line);

            if (isDevelopmentEnvironment()) {
                System.out.println("lg: " + lg);
            }

            try {
                LG.valueOf(lg);
                return lg;

            } catch (final IllegalArgumentException e) {
                return "";
            }

        } catch (final MalformedURLException e) {
            LOG.log(Level.SEVERE, "ouch!", e);
            throw new RuntimeException(e);

        } catch (final IOException e) {
            LOG.log(Level.SEVERE, "ouch!", e);
            throw new RuntimeException(e);
        }
    }

    private String extractDetectedLanguage(final String s) {
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
                } else {
                    if (counterSlot > 2) {
                        break;
                    }
                }
            }

        }

        return sb.toString();
    }

}
