package com.example.mostdelicious.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mostdelicious.R
import com.example.mostdelicious.databinding.MealPostItemBinding
import com.example.mostdelicious.helpers.toDateString
import com.example.mostdelicious.models.MealPost
import com.example.mostdelicious.models.User
import com.squareup.picasso.Picasso

class MealPostsAdapter(
    private var posts: MutableList<MealPost>,
    allUsers: List<User>,
    userLikedPosts: List<String> = listOf(),

    ) : RecyclerView.Adapter<MealPostsAdapter.MealPostViewHolder>() {

    private var userMap: Map<String, User> = allUsers.associateBy(User::id)

    private var userLikedPostsSet = HashSet(userLikedPosts)

    fun setPosts(posts: MutableList<MealPost>) {
        this.posts = posts
        notifyDataSetChanged()
    }

    fun setUserMap(users: List<User>) {
        val map = mutableMapOf<String, User>()
        for (user in users) {
            map[user.id] = user
        }
        this.userMap = map
        notifyDataSetChanged()
    }

    fun setUserLikedPosts(userLikedPosts: List<String>) {
        this.userLikedPostsSet = HashSet(userLikedPostsSet)
        notifyDataSetChanged()
    }

    fun setPost(post: MealPost, index: Int) {
        this.posts[index] = post
        notifyItemChanged(index)
    }

    class MealPostViewHolder(val binding: MealPostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(post: MealPost, postOwner: User, isLikedByUser: Boolean) {
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
        val isLikedByCurrentUser = userLikedPostsSet.contains(post.id)
        holder.bind(
            post = post,
            postOwner = user,
            isLikedByUser = isLikedByCurrentUser
        )
    }
}