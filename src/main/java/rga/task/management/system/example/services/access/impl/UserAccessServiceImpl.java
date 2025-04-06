package rga.task.management.system.example.services.access.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rga.task.management.system.example.entities.Comment;
import rga.task.management.system.example.entities.Task;
import rga.task.management.system.example.enums.Role;
import rga.task.management.system.example.exceptions.AccessForbiddenException;
import rga.task.management.system.example.security.CustomAuthentication;
import rga.task.management.system.example.services.access.UserAccessService;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserAccessServiceImpl implements UserAccessService {

    @Override
    public boolean isAdmin() {
        return getAuthenticatedUser()
                .map(
                        principal -> isRole(principal.getAuthorities(), Role.ROLE_ADMIN.getValue())
                )
                .orElse(false);
    }

    @Override
    public boolean isRole(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private Optional<CustomAuthentication> getAuthenticatedUser() {
        return Optional.ofNullable(
                (CustomAuthentication) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
    }

    @Override
    public boolean isAdminOrTaskExecutor(Task task) {
        return getAuthenticatedUser()
                .map(
                        principal ->
                                isRole(principal.getAuthorities(), Role.ROLE_ADMIN.getValue()) ||
                                (task.getExecutor() != null && isTaskExecutor(task, principal))
                )
                .orElse(false);
    }

    @Override
    public boolean isAdminOrCommentator(Comment comment) {
        return getAuthenticatedUser()
                .map(
                        principal ->
                                isRole(principal.getAuthorities(), Role.ROLE_ADMIN.getValue()) ||
                                (comment.getCommentator() != null && isCommentator(comment))
                )
                .orElse(false);
    }

    private boolean isTaskExecutor(Task task, CustomAuthentication principal) {
        return task.getExecutor().getEmail().equals(principal.getEmail());
    }

    private boolean isCommentAuthor(Comment comment, CustomAuthentication principal) {
        return comment.getCommentator().getEmail().equals(principal.getEmail());
    }

    @Override
    public boolean isCommentator(Comment comment) {
        return getAuthenticatedUser()
                .map(
                        principal -> comment.getCommentator() != null && isCommentAuthor(comment, principal)
                )
                .orElse(false);
    }

    @Override
    public String getCurrentEmail() {
        return getAuthenticatedUser()
                .map(CustomAuthentication::getEmail)
                .orElseThrow(() -> new AccessForbiddenException(HttpStatus.FORBIDDEN, "Access denied. Please authenticate first."));
    }

}
