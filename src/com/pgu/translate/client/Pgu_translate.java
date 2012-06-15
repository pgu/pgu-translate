package com.pgu.translate.client;

import java.util.HashMap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.pgu.translate.client.ui.TranslateUI;
import com.pgu.translate.client.ui.TranslateUIPresenter;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Pgu_translate implements EntryPoint, TranslateUIPresenter {

    private final TranslateServiceAsync service = GWT.create(TranslateService.class);

    public enum lgs {
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

    private final TranslateUI translateUI = new TranslateUI();

    @Override
    public void onModuleLoad() {

        translateUI.setPresenter(this);
        translateUI.setLanguages(lgs.values());

        RootPanel.get().add(translateUI);
    }

    @Override
    public void translate(final String wordToTranslate, final String sourceLanguage) {
        if (!isValidWordToTranslate(wordToTranslate)) {
            translateUI.resetInput();
            return;
        }
        service.translate(wordToTranslate.trim(), sourceLanguage, new AsyncCallback<HashMap<String, String>>() {

            @Override
            public void onFailure(final Throwable caught) {
                translateUI.resetInput();
                Window.alert(caught.getMessage());
            }

            @Override
            public void onSuccess(final HashMap<String, String> lg2result) {
                translateUI.setTranslationResult(lg2result);
            }

        });
    }

    private boolean isValidWordToTranslate(final String wordToTranslate) {
        return !wordToTranslate.trim().isEmpty();
    }

}
