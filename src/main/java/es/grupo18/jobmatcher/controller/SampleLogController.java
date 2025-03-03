package es.grupo18.jobmatcher.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test") // Endpoint for webpage testing
public class SampleLogController {
  private static final Logger log = LoggerFactory.getLogger(SampleLogController.class);

  @GetMapping("/log")
  public ResponseEntity<Void> logMessages() {
    log.trace("A TRACE Message");
    log.debug("A DEBUG Message");
    log.info("An INFO Message");
    log.warn("A WARN Message");
    log.error("An ERROR Message");

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Answers with 204 No Content
  }

  @GetMapping("/ping")
  public ResponseEntity<Void> ping() {
    log.info("Spring Boot is working");

    return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Answers with 204 No Content

  }

}
