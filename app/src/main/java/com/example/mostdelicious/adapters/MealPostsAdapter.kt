package com.example.mostdelicious.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mostdelicious.R
import com.example.mostdelicious.databinding.MealPostItemBinding
import com.example.mostdelicious.helpers.extensions.toDateString
import com.example.mostdelicious.models.FeedData
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MealPostsAdapter(
    private var posts: MutableList<MealPost> = mutableListOf(),
    allUsers: List<User> = listOf(),
    userLikedPosts: List<String> = listOf(),
    private val onRatingClicked: (MealPost) -> Unit,
    private val onLikeButtonClick: (MealPost) -> Unit,
    private val onRecipeRequest: ((MealPost) -> Unit)? = null,
) : RecyclerView.Adapter<MealPostsAdapter.MealPostViewHolder>() {

    private var userMap: Map<String, User> = allUsers.associateBy(User::id)

    private var userLikedPostsSet = HashSet(userLikedPosts)

    class MealPostViewHolder(val binding: MealPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(
            post: MealPost,
            postOwner: User,
            currentUser: User,
            isLikedByUser: Boolean,
            onRecipeRequest: ((MealPost) -> Unit)?,
        ) {
            Picasso.get()
                .load(post.image)
                .into(binding.ivPostItem)

            binding.rbPostItemRating.rating = post.averageRating.toFloat()
            binding.tvPostItemCreatedAt.text = post.createdAt.toDateString("yyyy-MM-dd")
            binding.tvPostItemName.text = post.name
            binding.tvPostItemContent.text = post.content
            binding.tvPostItemPostedBy.text = "Posted by ${postOwner.name}"


            if (isLikedByUser) {
                binding.ivPostItemLike.setImageResource(R.drawable.heart_full)
            } else {
                binding.ivPostItemLike.setImageResource(R.drawable.heart_empty)
            }

            binding.ivPostItemLike.visibility =
                if (postOwner.id == FirebaseAuth.getInstance().uid) {
                    View.GONE
                } else {
                    View.VISIBLE
                }

            binding.btnRatePost.visibility = if (postOwner.id == FirebaseAuth.getInstance().uid) {
                View.GONE
            } else {
                View.VISIBLE
            }

            onRecipeRequest?.let { request ->
                binding.btnGetRecipe.visibility = View.VISIBLE

                binding.btnGetRecipe.setOnClickListener {
                    request.invoke(post)
                }
            } ?: run {
                binding.btnGetRecipe.visibility = View.GONE
            }
            binding.btnRatePost.isEnabled = !currentUser.ratedPosts.contains(post.id)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPostViewHolder {
        val binding = MealPostItemBinding.inflate(LayoutInflater.from(parent.context))
        return MealPostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: MealPostViewHolder, position: Int) {
        val post = posts[position]
        val user = userMap[post.postUserId] ?: return
        val currentUser = userMap[FirebaseAuth.getInstance().uid] ?: return
        val isLikedByCurrentUser = userLikedPostsSet.contains(post.id)

        holder.binding.ivPostItemLike.setOnClickListener {
            onLikeButtonClick.invoke(post)
        }
        holder.binding.btnRatePost.setOnClickListener {
            onRatingClicked.invoke(post)
        }
        holder.bind(
            post = post,
            postOwner = user,
            currentUser = currentUser,
            isLikedByUser = isLikedByCurrentUser,
            onRecipeRequest
        )
    }

    fun updateFeedData(feedData: FeedData) {
        feedData.posts?.let {
            this.posts = it.toMutableList()
        }
        feedData.userLikedPosts?.let {
            this.userLikedPostsSet = HashSet(it)
        }
        feedData.allUsers?.let {
            val map = mutableMapOf<String, User>()
            for (user in it) {
                map[user.id] = user
            }
            this.userMap = map
        }
        notifyDataSetChanged()
    }
}