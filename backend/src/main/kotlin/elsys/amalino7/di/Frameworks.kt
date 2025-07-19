package elsys.amalino7.di


import elsys.amalino7.features.comment.CommentRepository
import elsys.amalino7.features.comment.CommentService
import elsys.amalino7.features.post.PostRepository
import elsys.amalino7.features.post.PostService
import elsys.amalino7.features.user.UserRepository
import elsys.amalino7.features.user.UserService
import elsys.amalino7.infrastructure.db.CommentRepositoryImpl
import elsys.amalino7.infrastructure.db.PostRepositoryImpl
import elsys.amalino7.infrastructure.db.UserRepositoryImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    install(Koin) {
        slf4jLogger()
        printLogger()
        modules(module {
            single<UserRepository> { UserRepositoryImpl() }
            single<UserService> { UserService(get()) }
            single<CommentRepository> { CommentRepositoryImpl() }
            single<CommentService> { CommentService(get()) }
            single<PostRepository> { PostRepositoryImpl() }
            single<PostService> { PostService(get()) }
        })
    }
    log.info("Koin initialized with: ${getKoin().get<UserService>()}")
}
