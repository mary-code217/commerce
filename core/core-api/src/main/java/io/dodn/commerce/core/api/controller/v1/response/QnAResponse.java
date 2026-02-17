package io.dodn.commerce.core.api.controller.v1.response;

import io.dodn.commerce.core.domain.QnA;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class QnAResponse {
    private final Long questionId;
    private final String questionTitle;
    private final String question;
    private final Long answerId;
    private final String answer;

    public static QnAResponse of(QnA qna) {
        return new QnAResponse(
                qna.getQuestion().getId(),
                qna.getQuestion().getTitle(),
                qna.getQuestion().getContent(),
                qna.getAnswer().getId(),
                qna.getAnswer().getContent()
        );
    }

    public static List<QnAResponse> of(List<QnA> qna) {
        return qna.stream().map(QnAResponse::of).collect(Collectors.toList());
    }
}
