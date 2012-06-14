package com.pgu.translate.server;

import java.nio.charset.Charset;

public class Test {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final Charset cs = Charset.forName("UTF-8");

        System.out.println(cs.name());
        System.out.println(Charset.forName("euc-kr"));
        System.out.println(Charset.forName("iso-8859-1"));
        System.out.println(Charset.forName("iso-8859-6"));
        System.out.println(Charset.forName("shift-jis"));
        System.out.println(Charset.forName("EUC-CN"));
        System.out.println(Charset.forName("iso-8859-5"));

    }

}
