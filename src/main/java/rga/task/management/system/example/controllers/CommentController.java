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
import rga.task.management.system.example.dtos.CommentDto;
import rga.task.management.system.example.dtos.ResponseMessageDto;
import rga.task.management.system.example.services.common.CommentService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/rest/v1/comments")
public class CommentController extends PageableResponseHandler {

    private final CommentService service;

    @Operation(summary = "Get all comments by task id", description = "Getting all comments for particular task",
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
            @ApiResponse(responseCode = "200", description = "Comments have been got successfully",
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
                            array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))),
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
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentDto>> getAllCommentsByTaskId(@PathVariable Long taskId,
                                                                   @Parameter(hidden = true) @PageableDefault Pageable pageable) {
        return pageableSuccessResponse(service.getPageByTaskId(taskId, convertPageable(pageable)));
    }


    @Operation(summary = "Comment deletion", description = "Deleting comment by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment has just been deleted successfully"),
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
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long commentId) {
        service.deleteById(commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(summary = "Comment creation", description = "Add new comment to particular task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment has been added successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class)) }),
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
    @PostMapping("/task/{taskId}")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long taskId, @Valid @RequestBody String content) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(taskId, content));
    }


    @Operation(summary = "Comment updating", description = "Updating comment by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment has been updated successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class)) }),
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
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@Valid @RequestBody String content,
                                                    @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.update(content, commentId));
    }

}
