package elsys.amalino7.features.post

import elsys.amalino7.features.user.User
import elsys.amalino7.utils.AppException
import elsys.amalino7.utils.PageRequest
import elsys.amalino7.utils.PageResult
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class PostService(val postRepository: PostRepository) {
    suspend fun getPosts(input: PageRequest): PageResult<PostResponse> {
        if (input.size > 100) {
            throw AppException.ValidationException("You can't request more than 100 posts at once.")
        }
        val res = postRepository.getAll(input)
        val items = res.items.map { it.toResponse() }
        return PageResult(items, res.totalCount)
    }

    suspend fun createPost(post: PostCreateRequest, user: User): PostResponse {
        val newPost = Post(
            id = Uuid.random(),
            content = post.content,
            imageId = post.imageId.let { if (it == null) null else Uuid.parse(it) },
            imageUrl = null,
            parentPost = null,
            user = user,
            likeCount = 0,
            hasLiked = false,
            commentCount = 0,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
        return postRepository.create(newPost).toResponse()
    }

    suspend fun getById(id: Uuid): PostResponse? {
        return postRepository.getById(id)?.toResponse()
    }

    suspend fun updatePost(post: PostPatchRequest, id: String, user: User): PostResponse {
        val oldPost = postRepository.getById(Uuid.parse(id))
        if (oldPost == null) {
            throw AppException.NotFoundException("Post with id $id not found")
        }
        if (oldPost.user.id != user.id) {
            throw AppException.UnauthorizedException("Post with id $id does not belong to user")
        }
        val newPost = Post(
            id = oldPost.id,
            content = post.content ?: oldPost.content,
            imageId = oldPost.imageId,
            imageUrl = oldPost.imageUrl,
            parentPost = oldPost.parentPost,
            user = oldPost.user,
            likeCount = oldPost.likeCount,
            hasLiked = oldPost.hasLiked,
            commentCount = oldPost.commentCount,
            createdAt = oldPost.createdAt,
            updatedAt = Clock.System.now(),
        )
        return postRepository.update(newPost).toResponse()
    }

    suspend fun deletePost(id: String, user: User): Boolean {
        val post = postRepository.getById(Uuid.parse(id))
        if (post == null) {
            throw AppException.NotFoundException("Post with id $id not found")
        }
        if (post.user.id != user.id) {
            throw AppException.UnauthorizedException("Post with id ${post.id} does not belong to user")
        }
        return postRepository.delete(Uuid.parse(id))
    }

    suspend fun getPostsOfUser(userId: Uuid, requesterId: Uuid?): List<PostResponse> {
        return postRepository.getPostsOfUser(userId, requesterId).map { it.toResponse() }
    }

    suspend fun getFeedOfUser(userId: Uuid): List<PostResponse> {
        return postRepository.getPostsOfUserByCriteria(userId).map { it.toResponse() }
    }
}