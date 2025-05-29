package com.nekosoft.brokenglass.ui.language

import com.brokenscreen.prankapp.crack.screen.R
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.data.local.AppPreference

object LanguageUtils {

    const val IS_SET_LANG = "isSetLang bigfont"
    const val LANGUAGE_KEY = "language key bigfont"
    const val LANG_PREF = " LANG_PREF_BIGFONT"
    const val saved = "first_save"
    const val LANGUAGE_CURRENT_CODE = "LANGUAGE_CURRENT_CODE"


    fun languageList(): MutableList<LanguageModel> {
        val languageList = mutableListOf<LanguageModel>()
        languageList.add(LanguageModel("ja", "Japan", R.drawable.ic_japan, false))
        languageList.add(LanguageModel("ko", "Korea", R.drawable.ic_korea, false))
        languageList.add(LanguageModel("fr", "France", R.drawable.ic_france, false))
        languageList.add(LanguageModel("hi", "India", R.drawable.ic_india, false))
        languageList.add(LanguageModel("pt", "Brasil", R.drawable.ic_brasil, false))
        languageList.add(LanguageModel("tr", "Turkey", R.drawable.ic_turkey, false))
        languageList.add(LanguageModel("nl", "Spain", R.drawable.ic_spain, false))
        languageList.add(LanguageModel("it", "Italy", R.drawable.ic_italy, false))
        languageList.add(LanguageModel("de", "German", R.drawable.ic_germany, false))
        languageList.add(LanguageModel("in", "Indonesia", R.drawable.ic_indonesian, false))
        languageList.add(LanguageModel("zh", "China", R.drawable.ic_china, false))
        languageList.add(LanguageModel("vi", "Vietnamese", R.drawable.ic_vietnam, false))
        languageList.add(LanguageModel("en", "English", R.drawable.ic_english, false))
        return languageList
    }
}