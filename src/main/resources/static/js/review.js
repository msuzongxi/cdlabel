let reviewFilings = [];

function escapeHtml(value) {
    return $("<div>").text(value ?? "").html();
}

function stageLabel(stage) {
    return ({CALIBRATION: "Calibration", BLIND_PILOT: "Blind pilot", FORMAL_VALIDATION: "Formal validation", OTHER: "Other"})[stage] || stage;
}

function renderReviewFilings() {
    const stage = $("#reviewStageFilter").val();
    const q = $("#reviewSearch").val().trim().toLowerCase();
    const rows = reviewFilings.filter(f => {
        const stageOk = stage === "ALL" || f.datasetStage === stage;
        const hay = [f.company, f.masterCik, f.filename, f.batchCode, f.labelers].filter(Boolean).join(" ").toLowerCase();
        return stageOk && (!q || hay.includes(q));
    });

    const counts = {};
    reviewFilings.forEach(f => counts[f.datasetStage] = (counts[f.datasetStage] || 0) + 1);
    $("#reviewSummary").text(`${rows.length} visible · Calibration ${counts.CALIBRATION || 0} · Blind ${counts.BLIND_PILOT || 0} · Formal ${counts.FORMAL_VALIDATION || 0}`);

    if (!rows.length) {
        $("#reviewFilingRows").html('<tr><td colspan="7" class="empty-table">No filings match this filter.</td></tr>');
        return;
    }

    $("#reviewFilingRows").html(rows.map(f => {
        const expected = (f.totalCount || 0) * (f.labelerCount || 0);
        const submitted = f.submittedCount || 0;
        const pct = expected ? Math.round(100 * submitted / expected) : 0;
        const progressClass = expected > 0 && submitted === expected && f.labelerCount >= 2 ? "ready" : "working";
        const progressText = f.labelerCount < 2 ? "Need 2 labelers" : (submitted === expected ? "Ready" : `${pct}%`);
        return `
            <tr class="clickable-row" data-id="${f.filingId}">
                <td><div class="assignment-company">${escapeHtml(f.company)}</div><div class="muted">CIK ${escapeHtml(f.masterCik || "")} · ${escapeHtml(f.filename || "")}</div></td>
                <td><span class="stage-badge">${escapeHtml(stageLabel(f.datasetStage))}</span></td>
                <td>${escapeHtml(f.fiscalYear)}</td>
                <td>${escapeHtml(f.labelers || "Unassigned")}</td>
                <td>${submitted} / ${expected || 0}</td>
                <td><span class="review-progress ${progressClass}">${escapeHtml(progressText)}</span></td>
                <td><a class="button-link small-button" href="/review/filing/${f.filingId}">Compare</a></td>
            </tr>`;
    }).join(""));

    $("#reviewFilingRows tr[data-id]").on("click", function(e) {
        if ($(e.target).closest("a").length) return;
        window.location.href = `/review/filing/${$(this).data("id")}`;
    });
}

$(function() {
    $.getJSON("/api/review/filings")
        .done(data => { reviewFilings = data || []; renderReviewFilings(); })
        .fail(() => $("#reviewSummary").text("Could not load comparison filings."));

    $("#reviewStageFilter, #reviewSearch").on("change input", renderReviewFilings);
});
