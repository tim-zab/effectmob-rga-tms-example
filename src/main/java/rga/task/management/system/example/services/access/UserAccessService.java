package rga.task.management.system.example.services.access;

import org.springframework.security.core.GrantedAuthority;
import rga.task.management.system.example.entities.Comment;
import rga.task.management.system.example.entities.Task;

import java.util.Collection;

public interface UserAccessService {

    boolean isAdmin();

    boolean isRole(Collection<? extends GrantedAuthority> authorities, String role);

    boolean isAdminOrTaskExecutor(Task task);

    boolean isAdminOrCommentator(Comment comment);

    boolean isCommentator(Comment comment);

    String getCurrentEmail();

}
