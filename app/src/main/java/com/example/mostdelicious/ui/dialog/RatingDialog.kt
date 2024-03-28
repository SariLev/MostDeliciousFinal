package com.example.mostdelicious.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.mostdelicious.databinding.RatingDialogBinding
import com.example.mostdelicious.models.MealPost

class RatingDialog(
    val post: MealPost,
    val onRatingSubmitted: (MealPost, Float) -> Unit,
) : DialogFragment() {


    private var _binding: RatingDialogBinding? = null
    private val binding: RatingDialogBinding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = RatingDialogBinding.inflate(layoutInflater)


        binding.ratePostNameTv.text = post.name
        val dialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton(
                "Submit rating"
            ) { _, _ ->
                val rating = binding.mRatingBar.rating
                onRatingSubmitted(post, rating)
            }
            .setNegativeButton(
                "Cancel"
            ) { p0, p1 -> }
            .create()
        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }


}