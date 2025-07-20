package elsys.amalino7.features.post

import elsys.amalino7.utils.CrudRepository
import kotlin.uuid.Uuid

interface PostRepository : CrudRepository<Uuid, Post> {
    suspend fun getPostsOfUser(userId: Uuid, requesterId: Uuid?): List<Post>
    suspend fun getPostsOfUserByCriteria(userId: Uuid): List<Post>
    suspend fun likePost(postId: Uuid, userId: Uuid)
    suspend fun unlikePost(postId: Uuid, userId: Uuid)
}