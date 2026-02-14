package io.dodn.commerce.core.domain;

import io.dodn.commerce.core.enums.EntityStatus;
import io.dodn.commerce.core.support.OffsetLimit;
import io.dodn.commerce.core.support.Page;
import io.dodn.commerce.core.support.error.CoreException;
import io.dodn.commerce.core.support.error.ErrorType;
import io.dodn.commerce.storage.db.core.AnswerRepository;
import io.dodn.commerce.storage.db.core.QuestionEntity;
import io.dodn.commerce.storage.db.core.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QnAService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public Page<QnA> findQnA(Long productId, OffsetLimit offsetLimit) {
        var questions = questionRepository.findByProductIdAndStatus(
                productId, EntityStatus.ACTIVE, offsetLimit.toPageable()
        );

        Map<Long, io.dodn.commerce.storage.db.core.AnswerEntity> answers =
                answerRepository.findByQuestionIdIn(
                                questions.getContent().stream().map(it -> it.getId()).collect(Collectors.toList())
                        ).stream()
                        .filter(it -> it.isActive())
                        .collect(Collectors.toMap(it -> it.getQuestionId(), Function.identity()));

        return new Page<>(
                questions.getContent().stream()
                        .map(it -> {
                            var answerEntity = answers.get(it.getId());
                            Answer answer = answerEntity != null
                                    ? new Answer(answerEntity.getId(), answerEntity.getAdminId(), answerEntity.getContent())
                                    : Answer.EMPTY;
                            return new QnA(
                                    new Question(it.getId(), it.getUserId(), it.getTitle(), it.getContent()),
                                    answer
                            );
                        })
                        .collect(Collectors.toList()),
                questions.hasNext()
        );
    }

    public Long addQuestion(User user, Long productId, QuestionContent content) {
        var saved = questionRepository.save(new QuestionEntity(
                user.getId(), productId, content.getTitle(), content.getContent()
        ));
        return saved.getId();
    }

    @Transactional
    public Long updateQuestion(User user, Long questionId, QuestionContent content) {
        var found = questionRepository.findByIdAndUserId(questionId, user.getId());
        if (found == null || !found.isActive()) throw new CoreException(ErrorType.NOT_FOUND_DATA);
        found.updateContent(content.getTitle(), content.getContent());
        return found.getId();
    }

    @Transactional
    public Long removeQuestion(User user, Long questionId) {
        var found = questionRepository.findByIdAndUserId(questionId, user.getId());
        if (found == null || !found.isActive()) throw new CoreException(ErrorType.NOT_FOUND_DATA);
        found.delete();
        return found.getId();
    }

    /**
     * NOTE: 답변은 어드민 쪽 기능임
     * void addAnswer(User user, Long questionId, String content) {...}
     * void updateAnswer(User user, Long answerId, String content) {...}
     * void removeAnswer(User user, Long answerId) {...}
     */
}
