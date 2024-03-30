package com.example.mostdelicious.models

import kotlinx.serialization.Serializable


@Serializable
data class MealsResponse(
    val meals: List<Meal>?,
) {
    fun isEmpty(): Boolean {
        return meals.isNullOrEmpty()
    }
}

sealed class ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Failure<T>(val status: Int, val message: String) : ApiResponse<T>()
}

data class Ingredients(
    val ingredients: List<String>,
    val measures: List<String>,
)


@Serializable
data class Meal(
    val idMeal: String?,
    val strMeal: String?,
    val strDrinkAlternate: String?,
    val strCategory: String?,
    val strArea: String?,
    val strInstructions: String?,
    val strMealThumb: String?,
    val strTags: String?,
    val strYoutube: String?,
    val strIngredient1: String?,
    val strIngredient2: String?,
    val strIngredient3: String?,
    val strIngredient4: String?,
    val strIngredient5: String?,
    val strIngredient6: String?,
    val strIngredient7: String?,
    val strIngredient8: String?,
    val strIngredient9: String?,
    val strIngredient10: String?,
    val strIngredient11: String?,
    val strIngredient12: String?,
    val strIngredient13: String?,
    val strIngredient14: String?,
    val strIngredient15: String?,
    val strIngredient16: String?,
    val strIngredient17: String?,
    val strIngredient18: String?,
    val strIngredient19: String?,
    val strIngredient20: String?,
    val strMeasure1: String?,
    val strMeasure2: String?,
    val strMeasure3: String?,
    val strMeasure4: String?,
    val strMeasure5: String?,
    val strMeasure6: String?,
    val strMeasure7: String?,
    val strMeasure8: String?,
    val strMeasure9: String?,
    val strMeasure10: String?,
    val strMeasure11: String?,
    val strMeasure12: String?,
    val strMeasure13: String?,
    val strMeasure14: String?,
    val strMeasure15: String?,
    val strMeasure16: String?,
    val strMeasure17: String?,
    val strMeasure18: String?,
    val strMeasure19: String?,
    val strMeasure20: String?,
    val strSource: String?,
    val strImageSource: String?,
    val strCreativeCommonsConfirmed: String?,
    val dateModified: String?,
) {
    fun ingredients(): Ingredients {
        val ingredients = mutableListOf<String>()
        val measures = mutableListOf<String>()

        (strIngredient1)?.let {
            ingredients.add(it)
        }
        (strIngredient2)?.let {
            ingredients.add(it)
        }
        (strIngredient3)?.let {
            ingredients.add(it)
        }
        (strIngredient4)?.let {
            ingredients.add(it)
        }
        (strIngredient5)?.let {
            ingredients.add(it)
        }
        (strIngredient6)?.let {
            ingredients.add(it)
        }
        (strIngredient7)?.let {
            ingredients.add(it)
        }
        (strIngredient8)?.let {
            ingredients.add(it)
        }
        (strIngredient9)?.let {
            ingredients.add(it)
        }
        (strIngredient10)?.let {
            ingredients.add(it)
        }
        (strIngredient11)?.let {
            ingredients.add(it)
        }
        (strIngredient12)?.let {
            ingredients.add(it)
        }
        (strIngredient13)?.let {
            ingredients.add(it)
        }
        (strIngredient14)?.let {
            ingredients.add(it)
        }
        (strIngredient15)?.let {
            ingredients.add(it)
        }
        (strIngredient16)?.let {
            ingredients.add(it)
        }

        (strIngredient17)?.let {
            ingredients.add(it)
        }

        (strIngredient18)?.let {
            ingredients.add(it)
        }

        (strIngredient19)?.let {
            ingredients.add(it)
        }

        (strIngredient20)?.let {
            ingredients.add(it)
        }

        (strMeasure1)?.let {
            measures.add(it)
        }
        (strMeasure2)?.let {
            measures.add(it)
        }
        (strMeasure3)?.let {
            measures.add(it)
        }
        (strMeasure4)?.let {
            measures.add(it)
        }
        (strMeasure5)?.let {
            measures.add(it)
        }
        (strMeasure6)?.let {
            measures.add(it)
        }
        (strMeasure7)?.let {
            measures.add(it)
        }
        (strMeasure8)?.let {
            measures.add(it)
        }
        (strMeasure9)?.let {
            measures.add(it)
        }
        (strMeasure10)?.let {
            measures.add(it)
        }
        (strMeasure11)?.let {
            measures.add(it)
        }
        (strMeasure12)?.let {
            measures.add(it)
        }
        (strMeasure13)?.let {
            measures.add(it)
        }
        (strMeasure14)?.let {
            measures.add(it)
        }
        (strMeasure15)?.let {
            measures.add(it)
        }
        (strMeasure16)?.let {
            measures.add(it)
        }

        (strMeasure17)?.let {
            measures.add(it)
        }

        (strMeasure18)?.let {
            measures.add(it)
        }

        (strMeasure19)?.let {
            measures.add(it)
        }

        (strMeasure20)?.let {
            measures.add(it)
        }
        return Ingredients(
            ingredients = ingredients,
            measures = measures
        )
    }
}
