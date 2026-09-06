let reviewData = null;
let ontology = [];
let currentReviewParagraphId = null;
let currentFilingId = null;

function escapeHtml(value) {
    return $("<div>").text(value ?? "").html();
}

function dimensionMap() {
    return Object.fromEntries((ontology || []).map(d => [d.code, d]));
}

function attributeLabelMap() {
    const map = {};
    (ontology || []).forEach(d => (d.attributes || []).forEach(a => map[a.code] = a.label));
    return map;
}

function gateLabel(value) {
    return ({PASS: "Eligible", FAIL1: "Exclude · Gate 1", FAIL2: "Exclude · Gate 2", FAIL3: "Exclude · Gate 3"})[value] || "Not decided";
}

function statusClass(status) {
    return (status || "INCOMPLETE").toLowerCase();
}

function filteredParagraphs() {
    if (!reviewData) return [];
    const filter = $("#paragraphCompareFilter").val();
    const q = $("#reviewParagraphSearch").val().trim().toLowerCase();
    return reviewData.paragraphs.filter(p => {
        const statusOk = filter === "ALL" || p.compareStatus === filter;
        const hay = [p.paragraphId, p.sectionHint, p.headingContext, p.text].filter(Boolean).join(" ").toLowerCase();
        return statusOk && (!q || hay.includes(q));
    });
}

function renderPairSelectors() {
    const labelers = reviewData?.availableLabelers || [];
    const options = labelers.map(u => `<option value="${u.id}">${escapeHtml(u.username)}</option>`).join("");
    $("#compareLabelerA, #compareLabelerB").html(options);

    if (reviewData?.selectedLabelerAId != null) {
        $("#compareLabelerA").val(String(reviewData.selectedLabelerAId));
    }
    if (reviewData?.selectedLabelerBId != null) {
        $("#compareLabelerB").val(String(reviewData.selectedLabelerBId));
    }

    const enabled = labelers.length >= 2;
    $("#compareLabelerA, #compareLabelerB, #compareLabelersBtn").prop("disabled", !enabled);
    if (!enabled) {
        $("#comparePairMessage").text("Assign at least two LABELER accounts to this filing.");
    } else {
        $("#comparePairMessage").text(`${reviewData.selectedLabelerAName || ""} vs ${reviewData.selectedLabelerBName || ""}`);
    }
}

function renderHeader() {
    const f = reviewData.filing;
    $("#reviewCompany").text(f.company);
    const pair = reviewData.selectedLabelerAName && reviewData.selectedLabelerBName
        ? ` · comparing ${reviewData.selectedLabelerAName} vs ${reviewData.selectedLabelerBName}`
        : "";
    $("#reviewFilingMeta").text(`FY${f.fiscalYear} · CIK ${f.masterCik || ""} · ${f.filename || ""}${pair}`);
    $("#reviewCounts").html(`
        <span><strong>${reviewData.disagreementCount}</strong> disagreements</span>
        <span><strong>${reviewData.agreementCount}</strong> agreements</span>
        <span><strong>${reviewData.incompleteCount}</strong> incomplete</span>
        <span><strong>${f.totalCount}</strong> total</span>
    `);
    if (reviewData.labelerWarning) {
        $("#reviewFilingMeta").append(` · ${reviewData.labelerWarning}`);
    }
}

function renderParagraphList() {
    const rows = filteredParagraphs();
    $("#reviewParagraphSummary").text(`${rows.length} visible`);
    if (!rows.length) {
        $("#reviewParagraphList").html('<div class="empty-list">No paragraphs in this view.</div>');
        $("#reviewDetail").addClass("hidden");
        $("#reviewEmpty").removeClass("hidden").text("No paragraphs in this view.");
        return;
    }

    $("#reviewParagraphList").html(rows.map(p => `
        <button type="button" class="review-paragraph-item ${p.paragraphDbId === currentReviewParagraphId ? "active" : ""}" data-id="${p.paragraphDbId}">
            <span class="compare-status ${statusClass(p.compareStatus)}">${escapeHtml(p.compareStatus)}</span>
            <strong>${escapeHtml(p.paragraphId)}</strong>
            <span class="muted">${escapeHtml(p.sectionHint || p.headingContext || "")}</span>
            ${p.differences?.length ? `<span class="difference-summary">${escapeHtml(p.differences.join(" · "))}</span>` : ""}
        </button>
    `).join(""));

    $(".review-paragraph-item").on("click", function() {
        openReviewParagraph(Number($(this).data("id")));
    });

    if (!rows.some(p => p.paragraphDbId === currentReviewParagraphId)) {
        openReviewParagraph(rows[0].paragraphDbId);
    } else {
        openReviewParagraph(currentReviewParagraphId);
    }
}

function dimensionCell(label, code) {
    if (!label || label.status !== "SUBMITTED") return '<span class="muted">Not submitted</span>';
    const d = (label.dimensions || []).find(x => x.code === code);
    if (!d) return '<span class="muted">—</span>';
    const attrLabels = attributeLabelMap();
    const chips = (d.attributes || []).map(a => `<span class="attr-chip">${escapeHtml(attrLabels[a] || a)}</span>`).join(" ");
    return `<strong>✓</strong>${chips ? `<div class="attr-chip-row">${chips}</div>` : ""}`;
}

function dimensionSignature(label, code) {
    if (!label || label.status !== "SUBMITTED") return "__INCOMPLETE__";
    const d = (label.dimensions || []).find(x => x.code === code);
    if (!d) return "";
    return `${code}|${[...(d.attributes || [])].sort().join(",")}`;
}

function renderComparisonMatrix(labels) {
    const dims = dimensionMap();
    const users = labels.map(l => l.username);
    if (labels.length < 2) {
        return '<div class="review-warning">The selected pair has not both been assigned to this filing.</div>';
    }
    const a = labels[0], b = labels[1];
    const gateDiff = gateLabel(a.gateDecision) !== gateLabel(b.gateDecision);
    const gapDiff = !!a.ontologyGap !== !!b.ontologyGap;
    const dimensionRows = ["A1","A2","A3","B1","B2","C1"].map(code => {
        const diff = dimensionSignature(a, code) !== dimensionSignature(b, code);
        const name = dims[code]?.name || code;
        return `
            <tr class="${diff ? "matrix-diff" : ""}">
                <th>${escapeHtml(code)} · ${escapeHtml(name)}</th>
                <td>${dimensionCell(a, code)}</td>
                <td>${dimensionCell(b, code)}</td>
            </tr>`;
    }).join("");

    return `
        <div class="comparison-matrix-wrap">
            <table class="comparison-matrix">
                <thead><tr><th>Decision</th><th>${escapeHtml(users[0])}</th><th>${escapeHtml(users[1])}</th></tr></thead>
                <tbody>
                    <tr class="${gateDiff ? "matrix-diff" : ""}"><th>Eligibility</th><td>${escapeHtml(gateLabel(a.gateDecision))}</td><td>${escapeHtml(gateLabel(b.gateDecision))}</td></tr>
                    <tr class="${gapDiff ? "matrix-diff" : ""}"><th>Ontology gap</th><td>${a.ontologyGap ? "Yes" : "No"}</td><td>${b.ontologyGap ? "Yes" : "No"}</td></tr>
                    ${dimensionRows}
                </tbody>
            </table>
        </div>`;
}

function renderLabelNotes(labels) {
    return `<div class="label-note-grid">${labels.map(l => `
        <div class="label-note-card">
            <div class="label-note-head"><strong>${escapeHtml(l.username)}</strong><span class="status-pill" data-status="${escapeHtml(l.status)}">${escapeHtml(l.status)}</span></div>
            ${l.gapNote ? `<div><strong>Ontology-gap note:</strong> ${escapeHtml(l.gapNote)}</div>` : ""}
            <div><strong>Memo:</strong> ${escapeHtml(l.notes || "—")}</div>
        </div>`).join("")}</div>`;
}

function openReviewParagraph(id) {
    const p = reviewData.paragraphs.find(x => x.paragraphDbId === id);
    if (!p) return;
    currentReviewParagraphId = id;
    $(".review-paragraph-item").removeClass("active");
    $(`.review-paragraph-item[data-id="${id}"]`).addClass("active");
    $("#reviewEmpty").addClass("hidden");
    $("#reviewDetail").removeClass("hidden").html(`
        <div class="paragraph-card review-paragraph-card">
            <div class="paragraph-head"><div><strong>${escapeHtml(p.paragraphId)}</strong><span class="compare-status ${statusClass(p.compareStatus)}">${escapeHtml(p.compareStatus)}</span></div><span>${escapeHtml(p.sectionHint || p.headingContext || "")}</span></div>
            <div class="paragraph-text">${escapeHtml(p.text)}</div>
        </div>
        <div class="review-difference-banner ${p.compareStatus === "DISAGREEMENT" ? "has-diff" : ""}">
            <strong>${p.compareStatus === "DISAGREEMENT" ? "Differences:" : p.compareStatus === "AGREEMENT" ? "Selected labelers agree on coded gates, dimensions, and attributes." : "Comparison incomplete."}</strong>
            ${p.differences?.length ? `<span>${escapeHtml(p.differences.join(" · "))}</span>` : ""}
        </div>
        ${renderComparisonMatrix(p.labels || [])}
        ${renderLabelNotes(p.labels || [])}
    `);
}

function applyReviewData(data) {
    reviewData = data;
    renderPairSelectors();
    renderHeader();
    if (!reviewData.disagreementCount && $("#paragraphCompareFilter").val() === "DISAGREEMENT") {
        $("#paragraphCompareFilter").val(reviewData.incompleteCount ? "INCOMPLETE" : "ALL");
    }
    renderParagraphList();
}

function loadComparison(labelerAId = null, labelerBId = null) {
    let url = `/api/review/filings/${currentFilingId}`;
    if (labelerAId != null && labelerBId != null) {
        url += `?labelerA=${encodeURIComponent(labelerAId)}&labelerB=${encodeURIComponent(labelerBId)}`;
    }
    $("#comparePairMessage").text("Loading comparison…");
    $.getJSON(url)
        .done(applyReviewData)
        .fail(xhr => {
            const message = xhr.responseJSON?.error || "Could not load filing comparison.";
            $("#comparePairMessage").text(message);
            if (!reviewData) {
                $("#reviewCompany").text("Could not load comparison");
                $("#reviewEmpty").text(message);
            }
        });
}

$(function() {
    currentFilingId = Number($("meta[name='filing_id']").attr("content"));

    $.getJSON("/api/ontology")
        .done(data => {
            ontology = data || [];
            const params = new URLSearchParams(window.location.search);
            const a = Number(params.get("labelerA"));
            const b = Number(params.get("labelerB"));
            if (a && b && a !== b) loadComparison(a, b);
            else loadComparison();
        })
        .fail(() => {
            $("#reviewCompany").text("Could not load ontology");
            $("#reviewEmpty").text("Could not load ontology definitions for comparison.");
        });

    $("#compareLabelersBtn").on("click", function() {
        const a = Number($("#compareLabelerA").val());
        const b = Number($("#compareLabelerB").val());
        if (!a || !b) {
            $("#comparePairMessage").text("Choose two labelers.");
            return;
        }
        if (a === b) {
            $("#comparePairMessage").text("Choose two different labelers.");
            return;
        }
        currentReviewParagraphId = null;
        $("#paragraphCompareFilter").val("DISAGREEMENT");
        loadComparison(a, b);
    });

    $("#paragraphCompareFilter, #reviewParagraphSearch").on("change input", renderParagraphList);
});
