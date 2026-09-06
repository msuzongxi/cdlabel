let adminFilings = [];
let assignableUsers = [];
let assignmentPairs = [];

function setupCsrf() {
    const token = $('meta[name="_csrf"]').attr("content");
    const header = $('meta[name="_csrf_header"]').attr("content");
    $(document).ajaxSend((e, xhr, settings) => {
        if (settings.type !== "GET" && settings.type !== "HEAD" && token && header) {
            xhr.setRequestHeader(header, token);
        }
    });
}

function escapeHtml(value) {
    return $("<div>").text(value ?? "").html();
}

function loadImportStatus() {
    $.getJSON("/api/admin/import/status").done(r => {
        $("#importFolder").text(r.folder || "Not configured");
        const errorText = r.errors && r.errors.length ? ` · ${r.errors.length} error(s)` : "";
        $("#importStats").text(
            `${r.filesRead || 0} file(s) · ${r.filings || 0} filing(s) · ${r.paragraphs || 0} paragraph(s)${errorText}`
        );
    });
}

function renderUsers() {
    const labelers = assignableUsers.filter(u => u.role === "LABELER");
    const supervisors = assignableUsers.filter(u => u.role === "SUPERVISOR");

    const group = (title, users) => `
        <div class="user-group">
            <strong>${title}</strong>
            ${users.map(u => `
                <label><input type="checkbox" class="assignee-choice" data-role="${escapeHtml(u.role)}" value="${u.id}"> ${escapeHtml(u.username)}</label>
            `).join("") || '<span class="muted">None</span>'}
        </div>
    `;

    $("#userChoices").html(group("Labelers", labelers) + group("Supervisor", supervisors));
}

function assignmentBadges(filingId) {
    const rows = assignmentPairs.filter(a => a.filingId === filingId);
    if (!rows.length) return '<span class="muted">Unassigned</span>';
    return rows.map(a => `
        <span class="assignment-badge ${a.assignmentType.toLowerCase()}">
            ${escapeHtml(a.username)} · ${escapeHtml(a.assignmentType)}
            ${a.batchCode ? ` · ${escapeHtml(a.batchCode)}` : ""}
        </span>
    `).join(" ");
}

function stageLabel(stage) {
    switch (stage) {
        case "CALIBRATION": return "Calibration";
        case "BLIND_PILOT": return "Blind pilot";
        case "FORMAL_VALIDATION": return "Formal validation";
        default: return "Other";
    }
}

function stageBatchCode(stage) {
    switch (stage) {
        case "CALIBRATION": return "CALIBRATION_01";
        case "BLIND_PILOT": return "BLIND_PILOT_01";
        case "FORMAL_VALIDATION": return "FORMAL_VALIDATION_01";
        default: return null;
    }
}

function updateStageSummary() {
    const counts = { CALIBRATION: 0, BLIND_PILOT: 0, FORMAL_VALIDATION: 0, OTHER: 0 };
    adminFilings.forEach(r => {
        const key = counts.hasOwnProperty(r.datasetStage) ? r.datasetStage : "OTHER";
        counts[key]++;
    });
    $("#stageSummary").remove();
    const summary = `Calibration ${counts.CALIBRATION} · Blind pilot ${counts.BLIND_PILOT} · Formal validation ${counts.FORMAL_VALIDATION}`;
    $("#importStats").after(`<div id="stageSummary" class="muted">${summary}</div>`);
}

function renderFilings() {
    const q = $("#adminFilingSearch").val().trim().toLowerCase();
    const stage = $("#datasetStageFilter").val() || "CALIBRATION";
    const rows = adminFilings.filter(r => {
        const haystack = `${r.company || ""} ${r.masterCik || ""} ${r.filename || ""}`.toLowerCase();
        const matchesSearch = !q || haystack.includes(q);
        const matchesStage = stage === "ALL" || r.datasetStage === stage;
        return matchesSearch && matchesStage;
    });

    if (!rows.length) {
        $("#adminFilingRows").html('<tr><td colspan="7" class="empty-table">No filings in this stage.</td></tr>');
        $("#selectAllFilings").prop("checked", false);
        $("#adminStatus").text(`0 visible · ${adminFilings.length} total filings`);
        return;
    }

    $("#adminFilingRows").html(rows.map((r, index) => `
        <tr data-search-visible="true">
            <td><input class="filing-choice" type="checkbox" value="${r.filingId}"></td>
            <td class="row-number">${index + 1}</td>
            <td>
                <strong>${escapeHtml(r.company)}</strong>
                <div class="muted">CIK ${escapeHtml(r.masterCik || "")} · ${escapeHtml(r.filename || "")}</div>
            </td>
            <td><span class="assignment-badge ${String(r.datasetStage || "other").toLowerCase()}">${stageLabel(r.datasetStage)}</span></td>
            <td>${r.fiscalYear}</td>
            <td>${r.paragraphCount}</td>
            <td class="assignment-cell">${assignmentBadges(r.filingId)}</td>
        </tr>
    `).join(""));

    $("#selectAllFilings").prop("checked", false);
    $("#adminStatus").text(`${rows.length} visible · ${adminFilings.length} total filings`);
}

function reloadData() {
    return $.when(
        $.getJSON("/api/admin/users"),
        $.getJSON("/api/admin/filings"),
        $.getJSON("/api/admin/assignments")
    ).done((usersResp, filingsResp, assignmentsResp) => {
        assignableUsers = usersResp[0];
        adminFilings = filingsResp[0];
        assignmentPairs = assignmentsResp[0];
        renderUsers();
        updateStageSummary();
        renderFilings();
    });
}

function selectedIds(selector) {
    return $(selector + ":checked").map(function () { return Number(this.value); }).get();
}

function applyAssignments(action) {
    const filingIds = selectedIds(".filing-choice");
    const userIds = selectedIds(".assignee-choice");

    if (!filingIds.length || !userIds.length) {
        $("#adminStatus").text("Select at least one filing and one user.");
        return;
    }

    if (action === "REMOVE" && !confirm("Remove the selected filing assignments? Existing annotations will be kept.")) {
        return;
    }

    $("#adminStatus").text(action === "ADD" ? "Assigning…" : "Removing…");
    $.ajax({
        url: "/api/admin/assignments",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            filingIds,
            userIds,
            batchCode: $("#batchCode").val(),
            action
        })
    }).done(() => {
        $("#adminStatus").text("Saved.");
        reloadData();
    }).fail(xhr => {
        $("#adminStatus").text(readError(xhr, "Assignment failed"));
    });
}


function clearSelectedLabels() {
    const filingIds = selectedIds(".filing-choice");
    const userIds = $(".assignee-choice:checked[data-role='LABELER']")
        .map(function () { return Number(this.value); }).get();

    if (!filingIds.length || !userIds.length) {
        $("#adminStatus").text("Select at least one filing and one labeler.");
        return;
    }

    const labelerNames = $(".assignee-choice:checked[data-role='LABELER']")
        .map(function () { return $(this).parent().text().trim(); }).get().join(", ");

    const message = `Delete ALL saved/submitted labels for ${userIds.length} labeler(s) (${labelerNames}) on ${filingIds.length} selected filing(s)? Assignments and filing data will be kept.`;
    if (!confirm(message)) return;

    $("#adminStatus").text("Clearing selected labels…");
    $("#clearLabelsBtn").prop("disabled", true);
    $.ajax({
        url: "/api/admin/annotations/clear",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ filingIds, userIds })
    }).done(r => {
        $("#adminStatus").text(`Cleared ${r.deleted || 0} annotation(s).`);
        reloadData();
    }).fail(xhr => {
        $("#adminStatus").text(readError(xhr, "Clear labels failed"));
    }).always(() => $("#clearLabelsBtn").prop("disabled", false));
}

function readError(xhr, fallback) {
    return xhr.responseJSON && xhr.responseJSON.error
        ? xhr.responseJSON.error
        : `${fallback}: HTTP ${xhr.status}`;
}

$(function () {
    setupCsrf();
    loadImportStatus();
    reloadData();

    $("#adminFilingSearch").on("input", renderFilings);
    $("#datasetStageFilter").on("change", function () {
        const code = stageBatchCode(this.value);
        if (code) $("#batchCode").val(code);
        renderFilings();
    });
    $("#selectAllFilings").on("change", function () {
        $(".filing-choice").prop("checked", this.checked);
    });
    $("#addAssignmentsBtn").on("click", () => applyAssignments("ADD"));
    $("#removeAssignmentsBtn").on("click", () => applyAssignments("REMOVE"));
    $("#clearLabelsBtn").on("click", clearSelectedLabels);

    $("#reloadFolderBtn").on("click", function () {
        $(this).prop("disabled", true);
        $("#adminStatus").text("Reloading configured folder…");
        $.ajax({ url: "/api/admin/import/reload", type: "POST" })
            .done(r => {
                const msg = `${r.filings} filing(s), ${r.paragraphs} paragraph(s)`;
                $("#adminStatus").text(r.errors && r.errors.length ? `${msg}; ${r.errors.length} error(s)` : msg);
                loadImportStatus();
                reloadData();
            })
            .fail(xhr => $("#adminStatus").text(readError(xhr, "Reload failed")))
            .always(() => $("#reloadFolderBtn").prop("disabled", false));
    });
});
