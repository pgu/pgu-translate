package com.pgu.translate.client.ui;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.pgu.translate.client.Pgu_translate.lgs;

public class TranslateUI extends Composite {

    private static TranslateUIUiBinder uiBinder = GWT.create(TranslateUIUiBinder.class);

    interface TranslateUIUiBinder extends UiBinder<Widget, TranslateUI> {
    }

    @UiField
    TextBox                               inputWord;
    @UiField
    ListBox                               source;
    @UiField
    HTMLPanel                             resultsContainer;

    private final HashMap<lgs, HTMLPanel> lg2panel = new HashMap<lgs, HTMLPanel>();
    private TranslateUIPresenter          presenter;

    public TranslateUI() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setLanguages(final lgs[] languages) {

        for (final lgs lg : languages) {

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

    }

    public void setPresenter(final TranslateUIPresenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("inputWord")
    public void onKeyPress(final KeyPressEvent event) {
        if (event.getCharCode() == KeyCodes.KEY_ENTER) {

            inputWord.setEnabled(false);

            final String word = inputWord.getText().trim();
            final String sourceLanguage = source.getValue(source.getSelectedIndex());

            presenter.translate(word, sourceLanguage);
        }
    }

    public void resetInput() {
        inputWord.setEnabled(true);
    }

    public void setTranslationResult(final HashMap<String, String> lg2result) {
        inputWord.setEnabled(true);
        for (final Entry<String, String> e : lg2result.entrySet()) {
            final String lgName = e.getKey();
            lg2panel.get(lgs.valueOf(lgName)).clear();

            renderResult(lgName, e.getValue());
        }

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

		var resultDom = [];

		for ( var i = 0; i < result.length; i++) {

			var resultPart = result[i];

			if (i == 0) { // basic translation result
				for ( var j = 0; j < resultPart.length; j++) {
					resultDom.push("<div style=\"font-size: larger;\">"
							+ resultPart[j].join(", ") + "</div>");
				}
			} else if (i == 1) { // translations declined by kind (noun, interjection, verbe, ...)
				for ( var j = 0; j < resultPart.length; j++) {
					var kind = resultPart[j];
					console.log(kind);
					resultDom.push("<br/><div><b>" + kind[0] + "</b></div>");
					// kind[1] is the list of the translated words
					var kindResults = kind[2]; // array of results
					for ( var k = 0; k < kindResults.length; k++) {
						var kindResult = kindResults[k];
						resultDom.push("<div>" + kindResult[0] + "</div>");
						resultDom.push("<div style=\"color:grey\">"
								+ kindResult[1].join(", ") + "</div>");
					}
				}
			} else {
				break;
			}

		}

		container.innerHTML = resultDom.join("");

    }-*/;

}
