package com.example.finance_control.controller.activity;

import com.example.finance_control.domain.activity.Activity;
import com.example.finance_control.domain.type.Type;
import com.example.finance_control.dto.ActivityRequestDTO;
import com.example.finance_control.dto.ActivityResponseDTO;
import com.example.finance_control.service.ActivityService;
import com.example.finance_control.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
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
    public ResponseEntity<ActivityResponseDTO> createActivity(@RequestBody @Valid ActivityRequestDTO activityRequestDTO) {
        ActivityResponseDTO response = activityService.insertActivity(activityRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable String id,
            @RequestBody @Valid ActivityRequestDTO activityRequestDTO) {
        ActivityResponseDTO response = activityService.updateActivity(id, activityRequestDTO);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable String id, @RequestParam @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "ID do usuário deve ser um UUID válido"
    )  String userId) {
        activityService.removeActivity(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ActivityResponseDTO>> listActivities(@RequestParam @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "ID do usuário deve ser um UUID válido"
    ) String userId) {
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

    @GetMapping("/filter")
    public ResponseEntity<List<ActivityResponseDTO>> getActivitiesByType(
            @RequestParam String userId,
            @RequestParam Type type
    ) {
        List<ActivityResponseDTO> activities = activityService.getActivitiesByType(userId, type);
        return ResponseEntity.ok(activities);
    }
}
