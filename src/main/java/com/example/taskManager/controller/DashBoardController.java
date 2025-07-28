package com.example.taskManager.controller;


import com.example.taskManager.common.exception.Response;
import com.example.taskManager.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashBoardController {

    private final DashBoardService dashBoardService;

    @GetMapping("/get-overview")
    public ResponseEntity<Response<?>> getDashboardOverview(@RequestParam(name = "departmentId", required = false) Long departmentId) {

        return ResponseEntity.ok(Response.success(dashBoardService.getDashboardOverview(departmentId)));
    }

    @GetMapping("/get-dashboard-projects")
    public ResponseEntity<Response<?>> getDashboardProjects(@RequestParam(name = "departmentId", required = false) Long departmentId,
                                                            @RequestParam(name = "startTime", required = false) String startTime,
                                                            @RequestParam(name = "endTime", required = false) String endTime) {
        return ResponseEntity.ok(Response.success(dashBoardService.getDashboardProjects(departmentId, startTime, endTime)));
    }

    @GetMapping("/get-dashboard-tasks")
    public ResponseEntity<Response<?>> getDashboardTasks(@RequestParam(name = "departmentId", required = false) Long departmentId,
                                                         @RequestParam(name = "projectId", required = false) Long projectId,
                                                         @RequestParam(name = "startTime", required = false) String startTime,
                                                         @RequestParam(name = "endTime", required = false) String endTime) {
        return ResponseEntity.ok(Response.success(dashBoardService.getDashboardTasks(departmentId,projectId, startTime, endTime)));
    }

    @GetMapping("/get-dashboard-users")
    public ResponseEntity<Response<?>> getDashboardUsers(@RequestParam(name = "departmentId", required = false) Long departmentId,
                                                         @RequestParam(name = "startTime", required = false) String startTime,
                                                         @RequestParam(name = "endTime", required = false) String endTime,
                                                         @RequestParam(name = "page", defaultValue = "0") int page,
                                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(Response.success(dashBoardService.getDashboardUsers(departmentId, startTime, endTime, page, size)));
    }

    @GetMapping("/get-dashboard-userTask-overview")
    public ResponseEntity<Response<?>> getDashboardTaskOverview(@RequestParam(name = "departmentId", required = false) Long departmentId,
                                                                @RequestParam(name = "startTime", required = false) String startTime,
                                                                @RequestParam(name = "endTime", required = false) String endTime) {
        return ResponseEntity.ok(Response.success(dashBoardService.getDashboardTaskOverview(departmentId, startTime, endTime)));
    }

}
