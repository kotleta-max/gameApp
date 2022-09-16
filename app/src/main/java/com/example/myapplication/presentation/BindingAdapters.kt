package com.example.myapplication.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.myapplication.R
import com.example.myapplication.domain.entity.GameResult

@BindingAdapter ("requiredAnswers")
fun bindRequiredAnswers(textView: TextView, count: Int){
    textView.text = String.format(
        textView.context.getString(R.string.required_score),
        count
    )
}
@BindingAdapter ("bindScore")
fun bindScore(textView: TextView, score: Int){
    textView.text = String.format(
        textView.context.getString(R.string.score_answers),
        score
    )
}
@BindingAdapter ("requiredPercentage")
fun bindRequiredPercentage(textView: TextView, percentage: Int){
    textView.text = String.format(
        textView.context.getString(R.string.required_percentage),
        percentage
    )
}
@BindingAdapter ("scorePercentage")
fun bindScorePercentage(textView: TextView, gameResult: GameResult){
    textView.text = String.format(
        textView.context.getString(R.string.score_percentage),
        getPercentOfRightAnswers(gameResult)
    )
}

private fun getPercentOfRightAnswers(gameResult: GameResult) = with(gameResult) {
    if (countOfQuestions == 0) {
        0
    } else {
        ((countOfRightAnswers / countOfQuestions.toDouble()) * 100).toInt()
    }
}

@BindingAdapter ("resultEmoji")
fun bindEmoji(imageView: ImageView, winner: Boolean){
    imageView.setImageResource(getSmileRedId(winner))
}

private fun getSmileRedId(winner: Boolean): Int {
    return if (winner) {
        R.drawable.ic_smile
    } else {
        R.drawable.ic_sad
    }
}
