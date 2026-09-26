package com.openhrm.leave;

import com.openhrm.leave.dto.LeaveRequestDto;
import com.openhrm.leave.dto.LeaveResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leave-requests")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping
    public List<LeaveResponse> list() {
        return leaveService.listForCurrentOrg().stream().map(LeaveResponse::from).toList();
    }

    @GetMapping("/{id}")
    public LeaveResponse get(@PathVariable UUID id) {
        return LeaveResponse.from(leaveService.getForCurrentOrg(id));
    }

    @PostMapping
    public ResponseEntity<LeaveResponse> create(@Valid @RequestBody LeaveRequestDto dto) {
        LeaveRequest created = leaveService.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/leave-requests/" + created.getId()))
                .body(LeaveResponse.from(created));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'MANAGER')")
    public LeaveResponse approve(@PathVariable UUID id) {
        return LeaveResponse.from(leaveService.approve(id));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR_MANAGER', 'MANAGER')")
    public LeaveResponse reject(@PathVariable UUID id) {
        return LeaveResponse.from(leaveService.reject(id));
    }
}
