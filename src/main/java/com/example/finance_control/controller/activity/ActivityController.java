package com.example.finance_control.controller.activity;

import com.example.finance_control.dto.ActivityRequestDTO;
import com.example.finance_control.dto.ActivityResponseDTO;
import com.example.finance_control.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

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
}
