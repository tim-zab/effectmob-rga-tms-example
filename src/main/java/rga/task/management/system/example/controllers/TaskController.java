package rga.task.management.system.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rga.task.management.system.example.dtos.TaskAddOrUpdateDto;
import rga.task.management.system.example.dtos.ResponseMessageDto;
import rga.task.management.system.example.dtos.TaskDto;
import rga.task.management.system.example.enums.Status;
import rga.task.management.system.example.mappers.impl.TaskMapper;
import rga.task.management.system.example.services.common.TaskService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/rest/v1/tasks")
public class TaskController extends PageableResponseHandler {

    private final TaskService service;
    private final TaskMapper taskMapper;

    @Operation(summary = "Get task by id", description = "Getting task by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task has been got successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access is forbidden",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "There is nothing found",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(taskMapper.toDto(service.getById(id)));
    }


    @Operation(summary = "Get all tasks by author id", description = "Getting all task for particular author",
            parameters = {
                    @Parameter(description = "Page number (0..N)",
                            schema = @Schema(type = "integer", defaultValue = "0"),
                            in = ParameterIn.QUERY,
                            name = "page"),
                    @Parameter(description = "Elements quantity on page",
                            schema = @Schema(type = "integer", maximum = "50", defaultValue = "10"),
                            in = ParameterIn.QUERY,
                            name = "size"),
                    @Parameter(description = "Sorting conditions: property(,asc|desc). " +
                            "<br>By default, data is sorted by timestamp:ASC",
                            schema = @Schema(type = "string"),
                            in = ParameterIn.QUERY,
                            name = "sort")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks have been got successfully",
                    headers = {
                            @Header(name = "X-Total-Elements-Count",
                                    description = "Total elements",
                                    schema = @Schema(type = "integer", format = "int32", example = "1")),
                            @Header(name = "X-Total-Pages",
                                    description = "Total pages",
                                    schema = @Schema(type = "integer", format = "int32", example = "1")),
                            @Header(name = "X-Current-Page",
                                    description = "Current page",
                                    schema = @Schema(type = "integer", format = "int32", example = "1"))},
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TaskDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access is forbidden",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "There is nothing found",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class)))
    })
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<TaskDto>> getAllTasksByAuthorId(@PathVariable Long authorId,
                                                               @Parameter(hidden = true) @PageableDefault Pageable pageable) {
        return pageableSuccessResponse(service.getByAuthorId(authorId, convertPageable(pageable)));
    }



    @Operation(summary = "Get all tasks by executor id", description = "Getting all task for particular executor",
            parameters = {
                    @Parameter(description = "Page number (0..N)",
                            schema = @Schema(type = "integer", defaultValue = "0"),
                            in = ParameterIn.QUERY,
                            name = "page"),
                    @Parameter(description = "Elements quantity on page",
                            schema = @Schema(type = "integer", maximum = "50", defaultValue = "10"),
                            in = ParameterIn.QUERY,
                            name = "size"),
                    @Parameter(description = "Sorting conditions: property(,asc|desc). " +
                            "<br>By default, data is sorted by timestamp:ASC",
                            schema = @Schema(type = "string"),
                            in = ParameterIn.QUERY,
                            name = "sort")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks have been got successfully",
                    headers = {
                            @Header(name = "X-Total-Elements-Count",
                                    description = "Total elements",
                                    schema = @Schema(type = "integer", format = "int32", example = "1")),
                            @Header(name = "X-Total-Pages",
                                    description = "Total pages",
                                    schema = @Schema(type = "integer", format = "int32", example = "1")),
                            @Header(name = "X-Current-Page",
                                    description = "Current page",
                                    schema = @Schema(type = "integer", format = "int32", example = "1"))},
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = TaskDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access is forbidden",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "There is nothing found",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class)))
    })
    @GetMapping("executor/{executorId}")
    public ResponseEntity<List<TaskDto>> getAllTasksByExecutorId(@PathVariable Long executorId,
                                                                 @Parameter(hidden = true) @PageableDefault Pageable pageable) {
        return pageableSuccessResponse(service.getByExecutorId(executorId, convertPageable(pageable)));
    }


    @Operation(summary = "Task deletion", description = "Deleting task by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "task has just been deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access is forbidden",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "There is nothing found",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(summary = "Task creation", description = "Add new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task has been added successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access is forbidden",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "There is nothing found",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class)))
    })
    @PostMapping("/")
    public ResponseEntity<TaskDto> addTask(@Valid @RequestBody TaskAddOrUpdateDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(dto));
    }


    @Operation(summary = "Task updating", description = "Update the existent task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task has been updated successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access is forbidden",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "There is nothing found",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskAddOrUpdateDto dto){
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, dto));
    }

    @Operation(summary = "Task status updating", description = "Update status of the existent task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status has been updated successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "403", description = "Access is forbidden",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "There is nothing found",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json", schema =
                    @Schema(implementation = ResponseMessageDto.class)))
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody Status status){
        return ResponseEntity.status(HttpStatus.OK).body(service.update(id, status));
    }

}
