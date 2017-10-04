package com.maddog05.whatanime.ui.tor;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.maddog05.whatanime.R;
import com.maddog05.whatanime.ui.MainActivity;

/**
 * Created by andreetorres on 27/09/17.
 */

public class Animator {
    public static Animation hideAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(300);
        alphaAnimation.setFillAfter(true);
        return alphaAnimation;
    }

    public static Animation showFab(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.fab_open);
    }

    public static Animation hideFab(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.fab_close);
    }
}
