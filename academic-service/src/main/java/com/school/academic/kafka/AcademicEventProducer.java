package com.school.academic.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcademicEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public static final String TOPIC_RESULTS_PUBLISHED = "school.results.published";
    public static final String TOPIC_EXAM_CREATED = "school.exam.created";

    public void publishResultsPublished(Long examId, Long classId, String examName) {
        Map<String, Object> event = Map.of(
            "type", "RESULTS_PUBLISHED",
            "examId", examId,
            "classId", classId,
            "examName", examName,
            "timestamp", System.currentTimeMillis()
        );
        kafkaTemplate.send(TOPIC_RESULTS_PUBLISHED, "class-" + classId, event);
        log.info("Published results event for exam: {} class: {}", examId, classId);
    }

    public void publishExamCreated(Long examId, Long classId, String examName) {
        Map<String, Object> event = Map.of(
            "type", "EXAM_CREATED",
            "examId", examId,
            "classId", classId,
            "examName", examName,
            "timestamp", System.currentTimeMillis()
        );
        kafkaTemplate.send(TOPIC_EXAM_CREATED, "class-" + classId, event);
    }
}
