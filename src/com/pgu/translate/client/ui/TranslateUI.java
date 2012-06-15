package com.pgu.translate.client.ui;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
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
    @UiField
    Anchor                                btnSend;

    private final HashMap<lgs, HTMLPanel> lg2panel = new HashMap<lgs, HTMLPanel>();
    private TranslateUIPresenter          presenter;

    public TranslateUI() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setLanguages(final lgs[] languages) {

        for (final lgs lg : languages) {

            final String lgName = lg.toString();

            final HTMLPanel resultContainer = new HTMLPanel("");
            resultContainer.getElement().setId(lgName);
            resultContainer.setWidth("100%");

            resultsContainer.add(resultContainer);

            lg2panel.put(lg, resultContainer);
            source.addItem(lgName);
        }

    }

    public void setPresenter(final TranslateUIPresenter presenter) {
        this.presenter = presenter;
    }

    @UiHandler("btnSend")
    public void onClickSend(final ClickEvent event) {
        translateWord();
    }

    @UiHandler("inputWord")
    public void onKeyPress(final KeyPressEvent event) {
        if (event.getCharCode() == KeyCodes.KEY_ENTER) {
            translateWord();
        }
    }

    private void translateWord() {
        inputWord.setEnabled(false);
        // btnSend.setEnabled(false);

        final String word = inputWord.getText().trim();
        final String sourceLanguage = source.getValue(source.getSelectedIndex());

        presenter.translate(word, sourceLanguage);
    }

    public void resetInput() {
        inputWord.setEnabled(true);
        // btnSend.setEnabled(true);
    }

    public void setTranslationResult(final HashMap<String, String> lg2result) {
        inputWord.setEnabled(true);
        // btnSend.setEnabled(true);
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
    private static native void renderResult(String lgName, String json) /*-{
		var result = eval(json);
		var container = $wnd.document.getElementById(lgName);

		var resultDom = [];

		for ( var i = 0; i < result.length; i++) {

			var resultPart = result[i];

			if (i == 0) { // basic translation result

				var basicTsl = [];
				for ( var j = 0; j < resultPart.length; j++) {
					var parts = resultPart[j];
					for ( var jj = 0; jj < parts.length; jj++) {
						var part = parts[jj];
						if (null != part && "" != part) {
							basicTsl.push(part);
						}
					}
				}
				var basicTslLabel = basicTsl.join(", ");
				//				basicTslLabel = basicTslLabel.substring(0,
				//						basicTslLabel.length - 2);

				//en fr es it de ja cn ko ru ar;
				resultDom
						.push("" //
								+ "<div class=\"navbar\">" //
								+ "  <div class=\"navbar-inner\">" //
								+ "    <div class=\"container\">" //
								+ "      <a class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">" //
								+ "        <span class=\"icon-bar\"></span>" //
								+ "        <span class=\"icon-bar\"></span>" //
								+ "        <span class=\"icon-bar\"></span>" //
								+ "      </a>" //
								//
								+ "      <a class=\"brand\" href=\"#\"><span class=\"label item_"
								+ lgName + " fg_bigger\">"
								+ lgName.toUpperCase()
								+ "</span><span class=\"fg_white\"> "
								+ basicTslLabel + "</span></a>" //
						);
			} else if (i == 1) { // translations declined by kind (noun, interjection, verbe, ...)

				resultDom.push("" //
						+ "<div class=\"nav-collapse\">" //
						+ "  <ul class=\"nav\">" //
				);

				for ( var j = 0; j < resultPart.length; j++) {
					var kind = resultPart[j];

					resultDom.push("<li><a href=\"#\">" + kind[0] + "</a>");
					resultDom.push("<ul class=\"nav fg_white\">");

					// kind[1] is the list of the translated words
					var kindResults = kind[2]; // array of results
					for ( var k = 0; k < kindResults.length; k++) {
						var kindResult = kindResults[k];
						//						resultDom.push("<div>" + kindResult[0] + "</div>");
						//						resultDom.push("<div style=\"color:grey\">"
						//								+ kindResult[1].join(", ") + "</div>");

						var _tsl = kindResult[1];
						if (_tsl == undefined) {
							_tsl = "";
						} else {
							_tsl = kindResult[1].join(", ");
						}

						resultDom.push("<li><span style=\"font-size:larger\">"
								+ kindResult[0]
								+ "</span><span style=\"padding-left:5px;\">  "
								+ _tsl + "</span></li>");
					}
					resultDom.push("" //
							+ "</li>" //
					);
				}

				resultDom.push("" //
						+ "</ul>" //
						+ "</div>" //
				);
			} else {
				resultDom.push("" //
						+ "</div>" //
						+ "</div>" //
						+ "</div>" //
				);

				break;
			}

		}

		container.innerHTML = resultDom.join("");

    }-*/;

    // <div class="navbar">
    // <div class="navbar-inner">
    // <div class="container">
    // <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
    // <span class="icon-bar"></span>
    // <span class="icon-bar"></span>
    // <span class="icon-bar"></span>
    // </a>
    // <a class="brand" href="#"><span class="label bg_orange fg_bigger">IT</span><span class="fg_white">
    // benvenuto</span></a>
    // <div class="nav-collapse">
    // <ul class="nav">
    // <li>
    // <a href="#">name</a>
    // <ul class="nav fg_white">
    // <li><span style="font-size:larger">benvenuto</span><span style=""> welcome</span></li>
    // <li><span style="font-size:larger">benvenuto2</span><span class=""> welcome2</span></li>
    // </ul>
    // </li>
    // <li>
    // <a href="#">interjection</a>
    // <ul class="nav fg_white">
    // <li><span style="font-size:larger">benvenuto2</span><span class=""> welcome2</span></li>
    // </ul>
    // </li>
    // <li>
    // <a href="#">verb</a></li>
    // </ul>
    // </div>
    // </div>
    // </div>
    // </div>
    //

}
