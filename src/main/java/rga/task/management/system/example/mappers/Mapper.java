package rga.task.management.system.example.mappers;

public interface Mapper<Entity, Dto, AnotherDto> {

    Dto toDto (Entity entity);

    Entity toEntity (AnotherDto dto);

}
