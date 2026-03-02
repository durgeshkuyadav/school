package com.school.content.service;

import com.school.content.document.Content;
import com.school.content.dto.*;
import com.school.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;

    /**
     * CORE VISIBILITY RULE:
     * - CLASS_TEACHER → scope = CLASS_WIDE → visible to ALL students in classId
     * - SUBJECT_TEACHER → scope = SUBJECT_SPECIFIC → visible only to students in classId AND enrolled in subjectId
     *
     * This method is called with the student's JWT claims:
     * @param classId        from student's JWT
     * @param subjectIdsStr  comma-separated subject IDs from student's JWT
     */
    public List<ContentResponse> getVisibleContentForStudent(Long classId, String subjectIdsStr) {
        List<Long> enrolledSubjectIds = parseSubjectIds(subjectIdsStr);

        // Get all class-wide content for this class
        List<Content> classWide = contentRepository
            .findByClassIdAndScopeAndPublishedTrue(classId, Content.ContentScope.CLASS_WIDE);

        // Get subject-specific content only for enrolled subjects
        List<Content> subjectSpecific = enrolledSubjectIds.isEmpty()
            ? List.of()
            : contentRepository.findByClassIdAndScopeAndSubjectIdInAndPublishedTrue(
                classId, Content.ContentScope.SUBJECT_SPECIFIC, enrolledSubjectIds);

        return java.util.stream.Stream.concat(classWide.stream(), subjectSpecific.stream())
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Used by teachers to see content they uploaded
     */
    public List<ContentResponse> getContentByUploader(Long teacherUserId) {
        return contentRepository.findByUploadedByUserId(teacherUserId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<ContentResponse> getContentByClass(Long classId) {
        return contentRepository.findByClassIdAndPublishedTrue(classId)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public ContentResponse uploadContent(UploadContentRequest req, Long userId, String uploaderName, String uploaderRole) {
        // Enforce: CLASS_TEACHER can only post CLASS_WIDE; SUBJECT_TEACHER must specify subjectId
        Content.ContentScope scope = "CLASS_TEACHER".equals(uploaderRole)
            ? Content.ContentScope.CLASS_WIDE
            : Content.ContentScope.SUBJECT_SPECIFIC;

        if (scope == Content.ContentScope.SUBJECT_SPECIFIC && req.getSubjectId() == null) {
            throw new IllegalArgumentException("Subject teachers must specify a subjectId");
        }

        Content content = Content.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .contentType(Content.ContentType.valueOf(req.getContentType()))
            .fileUrl(req.getFileUrl())
            .fileName(req.getFileName())
            .videoLink(req.getVideoLink())
            .classId(req.getClassId())
            .subjectId(req.getSubjectId())
            .scope(scope)
            .uploadedByUserId(userId)
            .uploaderName(uploaderName)
            .uploaderRole(uploaderRole)
            .published(true)
            .publishedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        return mapToResponse(contentRepository.save(content));
    }

    public void deleteContent(String contentId, Long requestingUserId) {
        Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new RuntimeException("Content not found"));
        // Only the uploader or admin can delete
        if (!content.getUploadedByUserId().equals(requestingUserId)) {
            throw new SecurityException("Not authorized to delete this content");
        }
        contentRepository.deleteById(contentId);
    }

    private List<Long> parseSubjectIds(String subjectIdsStr) {
        if (subjectIdsStr == null || subjectIdsStr.isBlank()) return List.of();
        return Arrays.stream(subjectIdsStr.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }

    private ContentResponse mapToResponse(Content c) {
        return ContentResponse.builder()
            .id(c.getId()).title(c.getTitle()).description(c.getDescription())
            .contentType(c.getContentType().name())
            .fileUrl(c.getFileUrl()).fileName(c.getFileName())
            .videoLink(c.getVideoLink())
            .classId(c.getClassId()).subjectId(c.getSubjectId())
            .scope(c.getScope().name())
            .uploaderName(c.getUploaderName()).uploaderRole(c.getUploaderRole())
            .createdAt(c.getCreatedAt())
            .build();
    }
}
