package com.pgu.translate.client.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
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

    interface MyStyle extends CssResource {
        String enabled();

        String disabled();

        String progress0();

        String progress20();

        String progress40();

        String progress60();

        String progress80();

        String progress100();
    }

    @UiField
    MyStyle                         style;
    @UiField
    TextBox                         inputWord;
    @UiField
    ListBox                         source;
    @UiField
    HTMLPanel                       resultsContainer;
    @UiField
    Anchor                          btnSend;
    @UiField
    DivElement                      progressBarContainer, progressBar;

    private final ArrayList<String> resultContainerIds = new ArrayList<String>();
    private TranslateUIPresenter    presenter;

    private final ArrayList<String> progressWidths     = new ArrayList<String>();

    public TranslateUI() {
        initWidget(uiBinder.createAndBindUi(this));

        progressWidths.add(style.progress0());
        progressWidths.add(style.progress20());
        progressWidths.add(style.progress40());
        progressWidths.add(style.progress60());
        progressWidths.add(style.progress80());
        progressWidths.add(style.progress100());
    }

    public void setLanguages(final lgs[] languages) {
        resultContainerIds.clear();
        source.clear();
        resultsContainer.clear();

        for (final lgs lg : languages) {

            final String lgName = lg.toString();

            final HTMLPanel resultContainer = new HTMLPanel("");
            resultContainer.getElement().setId(lgName);
            resultContainer.setWidth("100%");

            resultContainerIds.add(lgName);
            resultsContainer.add(resultContainer);

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

    private Timer progressTimer;
    private Timer progressTimerEnd;
    private int   progressWidthCount = 0;

    private void runProgressBar() {
        progressBarContainer.replaceClassName(style.disabled(), style.enabled());
        progressBar.replaceClassName(style.progress100(), style.progress0());

        progressTimer = new Timer() {

            @Override
            public void run() {
                progressBar.replaceClassName(progressCurrent(), progressNext());
                progressTimer.schedule(1000);
            }

        };
        progressTimer.schedule(300);
    }

    private String progressCurrent() {
        return progressWidths.get(progressWidthCount % progressWidths.size());
    }

    private String progressNext() {
        return progressWidths.get(++progressWidthCount % progressWidths.size());
    }

    private void stopProgressBar() {
        progressTimer.cancel();
        progressTimer = null;

        progressBar.replaceClassName(progressCurrent(), style.progress100());

        progressTimerEnd = new Timer() {

            @Override
            public void run() {
                progressWidthCount = 0;
                progressBarContainer.replaceClassName(style.enabled(), style.disabled());

                progressTimerEnd.cancel();
                progressTimerEnd = null;
            }

        };
        progressTimerEnd.schedule(500);
    }

    private void translateWord() {
        runProgressBar();
        inputWord.setEnabled(false);
        btnSend.setEnabled(false);

        final String word = inputWord.getText().trim();
        final String sourceLanguage = source.getValue(source.getSelectedIndex());

        presenter.translate(word, sourceLanguage);
    }

    public void resetInput() {
        stopProgressBar();
        inputWord.setEnabled(true);
        btnSend.setEnabled(true);

        for (final String containerId : resultContainerIds) {
            cleanContainerResult(containerId);
        }
    }

    public void setTranslationResult(final HashMap<String, String> lg2result) {
        stopProgressBar();
        inputWord.setEnabled(true);
        btnSend.setEnabled(true);

        for (final String containerId : resultContainerIds) {

            cleanContainerResult(containerId);

            if (lg2result.containsKey(containerId)) {
                renderResult(containerId, lg2result.get(containerId));
            }
        }

    }

    private void cleanContainerResult(final String lgName) {
        final Element container = DOM.getElementById(lgName);
        final int count = container.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            container.removeChild(container.getChild(i));
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

		if (result instanceof Array) {
			for ( var i = 0; i < result.length; i++) {

				var resultPart = result[i];

				if (i == 0) { // basic translation result

					var basicTsl = [];
					if (resultPart instanceof Array) {
						for ( var j = 0; j < resultPart.length; j++) {
							var parts = resultPart[j];
							if (parts instanceof Array) {
								for ( var jj = 0; jj < parts.length; jj++) {
									var part = parts[jj];
									if (null != part && "" != part) {
										basicTsl.push(part);
									}
								}
							}
						}
						var basicTslLabel = basicTsl.join(", ");

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
										+ "      <a class=\"brand\" href=\"javascript:void(0);\"><span class=\"label item_"
										+ lgName
										+ " fg_bigger\">"
										+ lgName.toUpperCase()
										+ "</span><span class=\"fg_white fg_font_25\"> "
										+ basicTslLabel + "</span></a>" //
								);
					}
				} else if (i == 1) { // translations declined by kind (noun, interjection, verbe, ...)

					resultDom.push("" //
							+ "<div class=\"nav-collapse\">" //
							+ "  <ul class=\"nav\">" //
					);

					if (resultPart instanceof Array) {
						for ( var j = 0; j < resultPart.length; j++) {
							var kind = resultPart[j];

							if (kind instanceof Array && kind.length >= 3) {
								resultDom
										.push("<li><a href=\"javascript:void(0);\">"
												+ kind[0] + "</a>");
								resultDom.push("<ul class=\"nav fg_white\">");

								// kind[1] is the list of the translated words
								var kindResults = kind[2]; // array of results
								if (kindResults instanceof Array) {
									for ( var k = 0; k < kindResults.length; k++) {
										var kindResult = kindResults[k];
										//						resultDom.push("<div>" + kindResult[0] + "</div>");
										//						resultDom.push("<div style=\"color:grey\">"
										//								+ kindResult[1].join(", ") + "</div>");

										if (kindResult instanceof Array
												&& kindResult.length >= 2) {

											var _tsl = kindResult[1];
											if (_tsl == undefined) {
												_tsl = "";
											} else {
												_tsl = kindResult[1].join(", ");
											}

											resultDom
													.push("<li style=\"line-height:25px\"><span style=\"font-size:larger\">"
															+ kindResult[0]
															+ "</span><span style=\"padding-left:5px;\">  "
															+ _tsl
															+ "</span></li>");
										}
									}
								}
								resultDom.push("" //
										+ "</li>" //
								);
							}
						}
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
