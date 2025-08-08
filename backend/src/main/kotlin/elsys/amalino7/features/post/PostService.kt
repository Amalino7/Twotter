package elsys.amalino7.features.post

import elsys.amalino7.features.images.ImageService
import elsys.amalino7.features.user.User
import elsys.amalino7.features.user.toView
import elsys.amalino7.utils.AppException
import elsys.amalino7.utils.PageRequest
import elsys.amalino7.utils.PageResult
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class PostService(
    private val postRepository: PostRepository,
    private val imageService: ImageService // Add image service for handling images
) {
    suspend fun createPost(request: PostCreateRequest, user: User): PostResponse {
        request.validate()

        // Handle parent post if it exists
        val parentPost = request.parentPostId?.let { parentId ->
            postRepository.getById(Uuid.parse(parentId))
                ?: throw AppException.NotFoundException("Parent post not found")
        }

        // Verify image exists if imageId is provided
        request.imageId?.let { imageId ->
            val image = imageService.getImageById(Uuid.parse(imageId))
                ?: throw AppException.NotFoundException("Image not found")
            if (image.uploaderId != user.id) {
                throw AppException.UnauthorizedException("Image does not belong to user")
            }
        }

        val newPost = Post(
            id = Uuid.random(),
            content = request.content,
            imageId = request.imageId?.let { Uuid.parse(it) },
            imageUrl = null, // Will be set by repository
            parentPost = parentPost,
            postType = request.postType,
            user = user.toView(),
            likeCount = 0,
            hasLiked = false,
            commentCount = 0,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )

        return postRepository.create(newPost).toResponse()
    }

//    suspend fun getPostThread(postId: Uuid): List<PostResponse> {
//        postRepository.getById(postId)
//            ?: throw AppException.NotFoundException("Post not found")
//
//        return postRepository.getPostThread(postId)
//            .map { it.toResponse() }
//    }

    suspend fun repost(postId: Uuid, user: User): PostResponse {
        val originalPost = postRepository.getById(postId)
            ?: throw AppException.NotFoundException("Post not found")

        if (originalPost.user.id == user.id) {
            throw AppException.ValidationException("Cannot repost your own post")
        }

        return createPost(
            PostCreateRequest(
                content = originalPost.content,
                parentPostId = postId.toString(),
                postType = PostType.QUOTE
            ),
            user
        )
    }

    suspend fun getPosts(input: PageRequest, requesterId: Uuid? = null): PageResult<PostResponse> {
        if (input.size > 100) {
            throw AppException.ValidationException("You can't request more than 100 posts at once.")
        }
        val res = if (requesterId == null) postRepository.getAll(input) else postRepository.getAllWithRequester(
            input,
            requesterId
        )
        val items = res.items.map { it.toResponse() }
        return PageResult(items, res.totalCount)
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
            postType = oldPost.postType,
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

    suspend fun likePost(postId: Uuid, userId: Uuid) = postRepository.likePost(postId, userId)
    suspend fun unlikePost(postId: Uuid, userId: Uuid) = postRepository.unlikePost(postId, userId)
}