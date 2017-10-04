package com.maddog05.whatanime.ui.tor;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;

import com.maddog05.whatanime.R;

import java.util.ArrayList;
import java.util.List;

import me.toptas.fancyshowcase.DismissListener;
import me.toptas.fancyshowcase.FancyShowCaseView;

/**
 * Created by andreetorres on 1/10/17.
 */

public class Tutorator {

    private Activity activity;
    private List<Pair<View, String>> pairs;
    private int index;

    public static Tutorator with(Activity activity) {
        return new Tutorator(activity);
    }

    private Tutorator(Activity activity) {
        this.activity = activity;
        pairs = new ArrayList<>();
        index = 0;
    }

    public Tutorator addViewString(View view, String text) {
        pairs.add(Pair.create(view, text));
        return this;
    }

    private int getGravity() {
        if (index == 0)
            return Gravity.BOTTOM | Gravity.CENTER;
        else
            return Gravity.CENTER;
    }

    public void start() {
        if (index < pairs.size()) {
            Pair<View, String> pair = pairs.get(index);
            new FancyShowCaseView.Builder(activity)
                    .focusOn(pair.first)
                    .title(pair.second)
                    .titleStyle(R.style.TutorialTitleStyle, getGravity())
                    .backgroundColor(ContextCompat.getColor(activity, R.color.tutorial_background))
                    .dismissListener(new DismissListener() {
                        @Override
                        public void onDismiss(String id) {
                            index++;
                            start();
                        }

                        @Override
                        public void onSkipped(String id) {
                            index++;
                            start();
                        }
                    })
                    .build()
                    .show();
        }
    }
}
