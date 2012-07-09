package com.pgu.translate.client.ui;

public interface TranslateUIPresenter {

    void translate(String wordToTranslate, String sourceLanguage);

    void detectLanguage(String text);

}
