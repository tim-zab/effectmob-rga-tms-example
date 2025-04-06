package rga.task.management.system.example.mappers;

import org.springframework.data.domain.Page;

public interface PageMapper<Entity, Dto> {

    Page<Dto> toDtosPage(Page<Entity> entities);

}
