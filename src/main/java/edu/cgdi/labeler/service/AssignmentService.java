package edu.cgdi.labeler.service;

import edu.cgdi.labeler.mapper.AssignmentMapper;
import edu.cgdi.labeler.model.AdminFilingRow;
import edu.cgdi.labeler.model.AssignableUser;
import edu.cgdi.labeler.model.AssignmentPair;
import edu.cgdi.labeler.web.dto.BulkAssignmentRequest;
import edu.cgdi.labeler.web.dto.ClearAnnotationsRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentMapper assignmentMapper;

    public AssignmentService(AssignmentMapper assignmentMapper) {
        this.assignmentMapper = assignmentMapper;
    }

    public List<AssignableUser> users() {
        return assignmentMapper.findAssignableUsers();
    }

    public List<AdminFilingRow> filings() {
        return assignmentMapper.findFilings();
    }

    public List<AssignmentPair> assignments() {
        return assignmentMapper.findAssignments();
    }

    @Transactional
    public void apply(BulkAssignmentRequest request) {
        if (request.filingIds() == null || request.filingIds().isEmpty()) {
            throw new IllegalArgumentException("Select at least one filing.");
        }
        if (request.userIds() == null || request.userIds().isEmpty()) {
            throw new IllegalArgumentException("Select at least one labeler or supervisor.");
        }

        String action = request.action() == null ? "ADD" : request.action().trim().toUpperCase();
        if (!action.equals("ADD") && !action.equals("REMOVE")) {
            throw new IllegalArgumentException("Assignment action must be ADD or REMOVE.");
        }

        for (Long userId : request.userIds()) {
            String role = assignmentMapper.findUserRole(userId);
            if (role == null || (!role.equals("LABELER") && !role.equals("SUPERVISOR"))) {
                throw new IllegalArgumentException("Selected user is not an active labeler or supervisor: " + userId);
            }
            String assignmentType = role.equals("LABELER") ? "LABELER" : "SUPERVISOR";

            for (Long filingId : request.filingIds()) {
                if (action.equals("ADD")) {
                    assignmentMapper.addAssignment(
                        filingId,
                        userId,
                        blankToNull(request.batchCode()),
                        assignmentType
                    );
                } else {
                    assignmentMapper.removeAssignment(filingId, userId);
                }
            }
        }
    }

    @Transactional
    public int clearLabels(ClearAnnotationsRequest request) {
        if (request.filingIds() == null || request.filingIds().isEmpty()) {
            throw new IllegalArgumentException("Select at least one filing.");
        }
        if (request.userIds() == null || request.userIds().isEmpty()) {
            throw new IllegalArgumentException("Select at least one labeler.");
        }

        for (Long userId : request.userIds()) {
            String role = assignmentMapper.findUserRole(userId);
            if (!"LABELER".equals(role)) {
                throw new IllegalArgumentException("Testing labels can only be cleared for active labelers: " + userId);
            }
        }

        return assignmentMapper.clearAnnotations(request.filingIds(), request.userIds());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
