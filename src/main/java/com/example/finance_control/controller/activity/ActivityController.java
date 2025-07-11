package com.example.finance_control.controller.activity;

import com.example.finance_control.dto.ActivityRequestDTO;
import com.example.finance_control.dto.ActivityResponseDTO;
import com.example.finance_control.service.ActivityService;
import com.example.finance_control.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ExportService exportService;

    @PostMapping
    public ResponseEntity<ActivityResponseDTO> createActivity(@RequestBody ActivityRequestDTO activityRequestDTO) {
        ActivityResponseDTO response = activityService.insertActivity(activityRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable String id,
            @RequestBody ActivityRequestDTO activityRequestDTO) {
        ActivityResponseDTO response = activityService.updateActivity(id, activityRequestDTO);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable String id, @RequestParam String userId) {
        activityService.removeActivity(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponseDTO>> listActivities(@RequestParam String userId) {
        List<ActivityResponseDTO> activities = activityService.listActivities(userId);
        return ResponseEntity.ok().body(activities);
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> calculateBalance(@RequestParam String userId) {
        Double balance = activityService.calculateBalance(userId);
        return ResponseEntity.ok().body(balance);
    }

    @GetMapping("/csv")
    public void exportToCsv(@RequestParam String userId, HttpServletResponse response) throws IOException {
        exportService.writeActivitiesToCsv(userId, response);
    }

    @GetMapping("/pdf")
    public void exportToPdf(@RequestParam String userId, HttpServletResponse response) throws Exception {
        exportService.writeActivitiesToPdf(userId, response);
    }
}
