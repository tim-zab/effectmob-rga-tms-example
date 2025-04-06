package rga.task.management.system.example.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

class PageableResponseHandler {

    <T> ResponseEntity<List<T>> pageableSuccessResponse(Page<T> page) {
        return new ResponseEntity<>(
                page.getContent(),
                formHeaders(page),
                HttpStatus.OK);
    }

    Pageable convertPageable(Pageable pageable) {
        return PageRequest.of(
                Math.max(pageable.getPageNumber(), 0),
                Math.min(pageable.getPageSize(), 10),
                pageable.getSort()
        );
    }

    private <T> HttpHeaders formHeaders(Page<T> page) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Total-Elements-Count", String.valueOf(page.getTotalElements()));
        responseHeaders.set("X-Total-Pages", String.valueOf(page.getTotalPages()));
        responseHeaders.set("X-Current-Page", String.valueOf(page.getNumber() + 1));
        return responseHeaders;
    }

}
