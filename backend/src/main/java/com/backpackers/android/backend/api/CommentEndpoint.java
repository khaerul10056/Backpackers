package com.backpackers.android.backend.api;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.users.User;

import com.backpackers.android.backend.authenticator.GoogleAuthenticator;
import com.backpackers.android.backend.controller.CommentController;
import com.backpackers.android.backend.model.comment.Comment;
import com.backpackers.android.backend.validator.Validator;
import com.backpackers.android.backend.validator.rule.comment.CommentCreateRule;
import com.backpackers.android.backend.validator.rule.common.IdValidationRule;
import com.backpackers.android.backend.Constants;
import com.backpackers.android.backend.authenticator.FacebookAuthenticator;
import com.backpackers.android.backend.authenticator.YolooAuthenticator;
import com.backpackers.android.backend.validator.rule.common.AllowedToOperate;
import com.backpackers.android.backend.validator.rule.common.AuthenticationRule;
import com.backpackers.android.backend.validator.rule.common.NotFoundRule;

import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

@Api(
        name = "yolooApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(
        resource = "comments",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID},
        authenticators = {
                GoogleAuthenticator.class,
                FacebookAuthenticator.class,
                YolooAuthenticator.class
        }
)
public class CommentEndpoint {

    private static final Logger logger =
            Logger.getLogger(CommentEndpoint.class.getName());

    /**
     * Inserts a new {@code Comment}.
     */
    @ApiMethod(
            name = "posts.comments.add",
            path = "posts/{postId}/comments",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Comment addPostComment(@Named("postId") final String websafePostId,
                                  @Named("text") final String text,
                                  final User user) throws ServiceException {

        Validator.builder()
                .addRule(new IdValidationRule(websafePostId))
                .addRule(new AuthenticationRule(user))
                .addRule(new CommentCreateRule(text))
                .addRule(new NotFoundRule(websafePostId))
                .validate();

        return CommentController.newInstance().add(websafePostId, text, user);
    }

    /**
     * Inserts a new {@code Comment}.
     */
    @ApiMethod(
            name = "questions.comments.add",
            path = "questions/{questionId}/comments",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Comment addQuestionComment(@Named("questionId") final String websafePostId,
                                      @Named("text") final String text,
                                      final User user) throws ServiceException {

        Validator.builder()
                .addRule(new IdValidationRule(websafePostId))
                .addRule(new AuthenticationRule(user))
                .addRule(new CommentCreateRule(text))
                .addRule(new NotFoundRule(websafePostId))
                .validate();

        return CommentController.newInstance().add(websafePostId, text, user);
    }

    /**
     * Deletes the specified {@code Comment}.
     *
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Comment}
     */
    @ApiMethod(
            name = "posts.comments.remove",
            path = "posts/{postId}/comments/{commentId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removePostComment(@Named("postId") final String websafePostId,
                                  @Named("commentId") final String websafeCommentId,
                                  final User user) throws ServiceException {

        Validator.builder()
                .addRule(new IdValidationRule(websafePostId))
                .addRule(new IdValidationRule(websafeCommentId))
                .addRule(new AuthenticationRule(user))
                .addRule(new NotFoundRule(websafePostId, websafeCommentId))
                .addRule(new AllowedToOperate(user, websafeCommentId, AllowedToOperate.Operation.DELETE))
                .validate();

        CommentController.newInstance().remove(websafePostId, websafeCommentId, user);
    }

    /**
     * Deletes the specified {@code Comment}.
     *
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Comment}
     */
    @ApiMethod(
            name = "questions.comments.remove",
            path = "questions/{questionId}/comments/{commentId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void removeQuestionComment(@Named("questionId") final String websafePostId,
                                      @Named("commentId") final String websafeCommentId,
                                      final User user) throws ServiceException {

        Validator.builder()
                .addRule(new IdValidationRule(websafePostId))
                .addRule(new IdValidationRule(websafeCommentId))
                .addRule(new AuthenticationRule(user))
                .addRule(new NotFoundRule(websafePostId, websafeCommentId))
                .addRule(new AllowedToOperate(user, websafeCommentId, AllowedToOperate.Operation.DELETE))
                .validate();

        CommentController.newInstance().remove(websafePostId, websafeCommentId, user);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "questions.comments.list",
            path = "questions/{questionId}/comments",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Comment> listQuestionComments(@Named("questionId") final String websafeCommentableId,
                                                            @Nullable @Named("cursor") String cursor,
                                                            @Nullable @Named("limit") Integer limit,
                                                            final User user) throws ServiceException {

        Validator.builder()
                .addRule(new IdValidationRule(websafeCommentableId))
                .addRule(new AuthenticationRule(user))
                .addRule(new NotFoundRule(websafeCommentableId))
                .validate();

        return CommentController.newInstance()
                .list(websafeCommentableId, cursor, limit, user);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "posts.comments.list",
            path = "posts/{postId}/comments",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Comment> listPostComments(@Named("postId") final String websafeCommentableId,
                                                        @Nullable @Named("cursor") String cursor,
                                                        @Nullable @Named("limit") Integer limit,
                                                        final User user) throws ServiceException {

        Validator.builder()
                .addRule(new IdValidationRule(websafeCommentableId))
                .addRule(new AuthenticationRule(user))
                .addRule(new NotFoundRule(websafeCommentableId))
                .validate();

        return CommentController.newInstance()
                .list(websafeCommentableId, cursor, limit, user);
    }
}