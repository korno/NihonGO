package uk.me.mikemike.nihongo.utils;

import android.view.textservice.SentenceSuggestionsInfo;

import uk.me.mikemike.nihongo.model.Card;

public class NihonGOUtils {

    public enum JapaneseDisplayMode { JAPANESE_DISPLAY, KANJI_DISPLAY, KANA};

    public  static  String getJapaneseDisplay(Card card, JapaneseDisplayMode mode){
        String japanese="";
        switch(mode){
            case KANA:
                japanese = card.getJapaneseHiragana();
                break;
            case KANJI_DISPLAY:
                japanese = card.hasKanji() ? card.getJapaneseKanji() : card.getJapaneseHiragana();
                break;
            case JAPANESE_DISPLAY:
                japanese = card.hasDisplayJapanese() ? card.getJapaneseDisplay() : card.getJapaneseHiragana();
        }
        return japanese;
    }


    public static String generateDictionaryResultsString(SentenceSuggestionsInfo[] results){
        final StringBuffer sb = new StringBuffer("");
        for(SentenceSuggestionsInfo result:results){
            int n = result.getSuggestionsCount();
            for(int i=0; i < n; i++){
                int m = result.getSuggestionsInfoAt(i).getSuggestionsCount();

                for(int k=0; k < m; k++) {
                    sb.append(result.getSuggestionsInfoAt(i).getSuggestionAt(k))
                            .append("\n");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
