package hedgehog.tech.ilauncher.ui.search

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

object SearchAnimations {

    private const val animDuration = 300L

     fun scaleSearchButtonAnimation(view: View?, viewWidth : Float, rootViewWidth : Float){

         val viewXStart = 0f
         val viewXEnd = rootViewWidth/2f - viewWidth/2f
         val startScale = 0.5f
         val endScale = 1f

         val widthAnimation = ObjectAnimator.ofFloat(view, "x", viewXStart, viewXEnd)
         widthAnimation.interpolator = AccelerateDecelerateInterpolator()

         val scaleX = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale)
         val scaleY = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale)

         widthAnimation.duration = animDuration
         scaleX.duration = animDuration
         scaleY.duration = animDuration

         val animationSet = AnimatorSet()
         animationSet.play(widthAnimation)
             .with(scaleX)
             .with(scaleY)

         animationSet.start()
    }

    fun scaleEditTextAnimation(view: View?, rootViewWidth : Float){

        val viewXStart = rootViewWidth/3f
        val viewXEnd = 0f
        val startScale = 1.3f
        val endScale = 1f

        val widthAnimation = ObjectAnimator.ofFloat(view, "x", viewXStart, viewXEnd)
        widthAnimation.interpolator = AccelerateDecelerateInterpolator()

        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", startScale, endScale)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", startScale, endScale)

        widthAnimation.duration = animDuration
        scaleX.duration = animDuration
        scaleY.duration = animDuration

        val animationSet = AnimatorSet()
        animationSet.play(widthAnimation)
            .with(scaleX)
            .with(scaleY)

        animationSet.start()
    }

}