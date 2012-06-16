package com.pgu.translate.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Test {

    private static final List<String> values = new ArrayList<String>();
    private static boolean            isOver = false;

    public static void main(final String[] args) throws InterruptedException {
        final Charset cs = Charset.forName("UTF-8");

        System.out.println(cs.name());
        System.out.println(Charset.forName("euc-kr"));
        System.out.println(Charset.forName("iso-8859-1"));
        System.out.println(Charset.forName("iso-8859-6"));
        System.out.println(Charset.forName("shift-jis"));
        System.out.println(Charset.forName("EUC-CN"));
        System.out.println(Charset.forName("iso-8859-5"));
        System.out.println(Charset.forName("koi8-r"));

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

}
