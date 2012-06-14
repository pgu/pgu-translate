package com.pgu.translate.client;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Pgu_translate implements EntryPoint {
    /**
     * The message displayed to the user when the server cannot be reached or returns an error.
     */
    private static final String         SERVER_ERROR = "An error occurred while "
                                                             + "attempting to contact the server. Please check your network "
                                                             + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final TranslateServiceAsync service      = GWT.create(TranslateService.class);

    private final TextBox               inputWord    = new TextBox();
    private final ListBox               source       = new ListBox();

    private enum lgs {
        en, //
        fr, //
        es, //
        it, //
        de, //
        ja, //
        cn, //
        ko, //
        ru, //
        ar;
    }

    private final HashMap<lgs, HTMLPanel> lg2panel = new HashMap<lgs, HTMLPanel>();

    @Override
    public void onModuleLoad() {

        final HTMLPanel resultsContainer = new HTMLPanel("");

        for (final lgs lg : lgs.values()) {

            final String lgName = lg.toString();

            final Label resultTitle = new Label(lgName);
            final HTMLPanel resultBody = new HTMLPanel("");
            resultBody.getElement().setId(lgName);

            final VerticalPanel resultContainer = new VerticalPanel();
            resultContainer.add(resultTitle);
            resultContainer.add(resultBody);
            resultsContainer.add(resultContainer);

            lg2panel.put(lg, resultBody);
            source.addItem(lgName);
        }

        inputWord.setText("welcome");

        final HorizontalPanel hp = new HorizontalPanel();
        hp.add(inputWord);
        hp.add(source);

        final VerticalPanel vp = new VerticalPanel();
        vp.add(hp);
        vp.add(resultsContainer);
        RootPanel.get().add(vp);

        inputWord.addKeyPressHandler(new KeyPressHandler() {

            @Override
            public void onKeyPress(final KeyPressEvent event) {
                if (event.getCharCode() == KeyCodes.KEY_ENTER) {

                    final String word = inputWord.getText().trim();
                    if (word == null //
                            || "".equals(word)) {
                        return;
                    }

                    inputWord.setEnabled(false);
                    service.translate(word, source.getValue(source.getSelectedIndex()),
                            new AsyncCallback<HashMap<String, String>>() {

                                @Override
                                public void onFailure(final Throwable caught) {
                                    inputWord.setEnabled(true);
                                    Window.alert(caught.getMessage());
                                }

                                @Override
                                public void onSuccess(final HashMap<String, String> lg2result) {
                                    inputWord.setEnabled(true);
                                    for (final Entry<String, String> e : lg2result.entrySet()) {
                                        final String lgName = e.getKey();
                                        lg2panel.get(lgs.valueOf(lgName)).clear();

                                        renderResult(lgName, e.getValue());
                                    }
                                }

                            });
                }

            }
        });
    }

    // [
    // [
    // ["歓迎","welcome","Kangei",""]
    // ],
    // [
    // ["nom"
    // ,["歓迎","ウエルカム","接待","奉迎","優待","遠見"]
    // ,[
    // ["歓迎",["welcome","reception"]]
    // ,["ウエルカム",["welcome"]]
    // ,["接待",["reception","welcome","serving"]]
    // ,["奉迎",["welcome"]]
    // ,["優待",["preferential treatment","hospitality","welcome","warm reception"]]
    // ,["遠見",["audience","guest-night","reception","social","welcome","ball"]]
    // ]
    // ]
    // ,["interjection"
    // ,["ようこそ","おいでやす","おこしやす"]
    // ,[
    // ["ようこそ",["welcome","nice to see you"]]
    // ,["おいでやす",["welcome"]]
    // ,["おこしやす",["welcome"]]
    // ]
    // ]
    // ,["verbe"
    // ,["歓迎する"]
    // ,[
    // ["歓迎する",["welcome","acclaim"]]
    // ]
    // ]
    // ]
    // ,"en",,[["歓迎",[5],0,0,517,0,1,0]],[["welcome",4,,,""],["welcome",5,[["歓迎",517,0,0],["ようこそ",386,0,0],["ウェルカム",95,0,0],["歓迎さ",0,0,0]],[[0,7]],"welcome"]],,,[["en"]],47]
    private static native void renderResult(String divId, String json) /*-{
		var result = eval(json);
		var container = $wnd.document.getElementById(divId);

		if (result.length === 0) {
			return;
		}

		var resultDom = "";

		var resultTsl = result[0];
		for ( var i = 0; i < resultTsl.length; i++) {
			console.log(resultTsl[i].join(", "));
			resultDom += "<div>" + resultTsl[i].join(", ") + "</div>";
		}

		container.innerHTML = resultDom;

    }-*/;
}
