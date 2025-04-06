package rga.task.management.system.example.mappers.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import rga.task.management.system.example.dtos.CommentDto;
import rga.task.management.system.example.entities.Comment;
import rga.task.management.system.example.mappers.Mapper;
import rga.task.management.system.example.mappers.PageMapper;
import rga.task.management.system.example.services.common.UserService;

import java.sql.Timestamp;

@Component
@AllArgsConstructor
public class CommentMapper implements Mapper<Comment, CommentDto, String>, PageMapper<Comment, CommentDto> {

    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    public CommentDto toDto(Comment comment) {
        var dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCommentator(userMapper.toDto(userService.getById(comment.getCommentator().getId())));
        dto.setTimestamp(new Timestamp(System.currentTimeMillis()));
        return dto;
    }

    @Override
    public Comment toEntity(String content) {
        var comment = new Comment();
        comment.setContent(content);
        return comment;
    }

    @Override
    public Page<CommentDto> toDtosPage(Page<Comment> comments) {
        return new PageImpl<>(comments.stream().map(this::toDto).toList());
    }

}
