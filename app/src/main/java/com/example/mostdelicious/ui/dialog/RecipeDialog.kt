package com.example.mostdelicious.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.mostdelicious.databinding.RecipeDialogBinding
import com.example.mostdelicious.models.Meal
import com.squareup.picasso.Picasso

class RecipeDialog(
    private var lifecycleOwner: LifecycleOwner?,
    private val liveData: MutableLiveData<Meal?>,
) : DialogFragment() {
    private var _binding: RecipeDialogBinding? = null
    private val binding: RecipeDialogBinding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = RecipeDialogBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setNegativeButton(
                "Close"
            ) { p0, p1 -> }
            .create()

        liveData.observe(lifecycleOwner!!) {
            if (it == null) {
                Log.d("No results", "!!")
                binding.recipeLayout.visibility = View.GONE
                binding.noResultsLayout.root.visibility = View.VISIBLE
                binding.pbRecipeDialog.visibility = View.GONE
                return@observe
            } else {
                binding.recipeLayout.visibility = View.VISIBLE
                binding.noResultsLayout.root.visibility = View.GONE
            }

            Picasso.get()
                .load(it.strMealThumb)
                .into(binding.ivRecipeDialog)


            binding.tvMealNameRecipeDialog.text = it.strMeal
            binding.tvInstructionsRecipeDialog.text = it.strInstructions
            val ingredients = it.ingredients()

            for (i in 0 until ingredients.ingredients.size) {
                val tv = TextView(requireContext())
                tv.text = "${ingredients.ingredients[i]} : ${ingredients.measures[i]}"
                binding.ingredientsLayout.addView(tv)
            }
            binding.pbRecipeDialog.visibility = View.GONE

            liveData.removeObservers(this)
        }

        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        liveData
        lifecycleOwner?.let {
            liveData.removeObservers(it)
        }
        liveData.postValue(null)
        lifecycleOwner = null
    }

}