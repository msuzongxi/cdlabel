let queueData = null;
let queueOntology = [];
let currentQueueParagraphId = null;

function escapeHtml(value) {
    return $("<div>").text(value ?? "").html();
}

function stageLabel(stage) {
    return ({
        CALIBRATION: "Calibration",
        BLIND_PILOT: "Blind pilot",
        FORMAL_VALIDATION: "Formal validation",
        OTHER: "Other",
        ALL: "All stages"
    })[stage] || stage;
}

function statusClass(status) {
    return (status || "").toLowerCase();
}

function gateLabel(value) {
    return ({PASS: "Eligible", FAIL1: "Exclude · Gate 1", FAIL2: "Exclude · Gate 2", FAIL3: "Exclude · Gate 3"})[value] || "—";
}

function dimensionMap() {
    const map = {};
    (queueOntology || []).forEach(d => map[d.code] = d);
    return map;
}

function attributeLabelMap() {
    const result = {};
    (queueOntology || []).forEach(d => (d.attributes || []).forEach(a => result[a.code] = a.name || a.code));
    return result;
}

function filteredQueueParagraphs() {
    if (!queueData) return [];
    const status = $("#queueStatusFilter").val();
    const difference = $("#queueDifferenceFilter").val();
    const q = $("#queueSearch").val().trim().toLowerCase();

    return (queueData.paragraphs || []).filter(p => {
        if (status !== "ALL" && p.compareStatus !== status) return false;
        if (difference !== "ALL") {
            if (p.compareStatus !== "DISAGREEMENT") return false;
            if (!(p.differenceCodes || []).includes(difference)) return false;
        }
        if (q) {
            const hay = [
                p.company, p.masterCik, p.filename, p.batchCode,
                p.paragraphId, p.sectionHint, p.headingContext, p.text
            ].filter(Boolean).join(" ").toLowerCase();
            if (!hay.includes(q)) return false;
        }
        return true;
    });
}

function renderQueuePairSelectors() {
    const labelers = queueData?.availableLabelers || [];
    const options = labelers.map(u => `<option value="${u.id}">${escapeHtml(u.username)}</option>`).join("");
    $("#queueLabelerA, #queueLabelerB").html(options);

    if (queueData?.selectedLabelerAId != null) $("#queueLabelerA").val(String(queueData.selectedLabelerAId));
    if (queueData?.selectedLabelerBId != null) $("#queueLabelerB").val(String(queueData.selectedLabelerBId));
    if (queueData?.stage) $("#queueStage").val(queueData.stage);

    const enabled = labelers.length >= 2;
    $("#queueLabelerA, #queueLabelerB, #queueStage, #queueCompareBtn").prop("disabled", !enabled);
    const pair = queueData?.selectedLabelerAName && queueData?.selectedLabelerBName
        ? `${queueData.selectedLabelerAName} vs ${queueData.selectedLabelerBName}`
        : "Choose two labelers";
    $("#queuePairMessage").text(queueData?.labelerWarning || pair);
}

function renderQueueCounts() {
    const both = queueData?.bothSubmittedCount || 0;
    const rate = queueData?.agreementRate;
    const rateText = rate == null ? "—" : `${rate.toFixed(1)}%`;
    $("#queueCounts").html(`
        <span><strong>${queueData?.disagreementCount || 0}</strong> disagreements</span>
        <span><strong>${queueData?.agreementCount || 0}</strong> agreements</span>
        <span><strong>${queueData?.incompleteCount || 0}</strong> incomplete</span>
        <span><strong>${queueData?.commonFilingCount || 0}</strong> common filings</span>
        <span><strong>${both}</strong> both submitted</span>
        <span><strong>${rateText}</strong> agreement</span>
    `);
}

function renderQueueParagraphList() {
    const rows = filteredQueueParagraphs();
    $("#queueVisibleSummary").text(`${rows.length} visible · ${stageLabel(queueData?.stage || "")}`);

    if (!rows.length) {
        $("#queueParagraphList").html('<div class="empty-list">No paragraphs match this view.</div>');
        $("#queueDetail").addClass("hidden");
        $("#queueEmpty").removeClass("hidden").text("No paragraphs match this view.");
        currentQueueParagraphId = null;
        return;
    }

    $("#queueParagraphList").html(rows.map(p => `
        <button type="button" class="review-paragraph-item queue-paragraph-item ${p.paragraphDbId === currentQueueParagraphId ? "active" : ""}" data-id="${p.paragraphDbId}">
            <div class="queue-item-top">
                <span class="compare-status ${statusClass(p.compareStatus)}">${escapeHtml(p.compareStatus)}</span>
                <span class="muted">FY${escapeHtml(p.fiscalYear)}</span>
            </div>
            <strong>${escapeHtml(p.company || "")}</strong>
            <span>${escapeHtml(p.paragraphId || "")}${p.sectionHint ? ` · ${escapeHtml(p.sectionHint)}` : ""}</span>
            ${p.differences?.length ? `<span class="difference-summary">${escapeHtml(p.differences.join(" · "))}</span>` : ""}
        </button>
    `).join(""));

    $(".queue-paragraph-item").on("click", function() {
        openQueueParagraph(Number($(this).data("id")));
    });

    if (!rows.some(p => p.paragraphDbId === currentQueueParagraphId)) {
        openQueueParagraph(rows[0].paragraphDbId);
    } else {
        openQueueParagraph(currentQueueParagraphId);
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
    if ((labels || []).length < 2) {
        return '<div class="review-warning">The selected pair is not available for this paragraph.</div>';
    }
    const dims = dimensionMap();
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
                <thead><tr><th>Decision</th><th>${escapeHtml(a.username)}</th><th>${escapeHtml(b.username)}</th></tr></thead>
                <tbody>
                    <tr class="${gateDiff ? "matrix-diff" : ""}"><th>Eligibility</th><td>${escapeHtml(gateLabel(a.gateDecision))}</td><td>${escapeHtml(gateLabel(b.gateDecision))}</td></tr>
                    <tr class="${gapDiff ? "matrix-diff" : ""}"><th>Ontology gap</th><td>${a.ontologyGap ? "Yes" : "No"}</td><td>${b.ontologyGap ? "Yes" : "No"}</td></tr>
                    ${dimensionRows}
                </tbody>
            </table>
        </div>`;
}

function renderLabelNotes(labels) {
    return `<div class="label-note-grid">${(labels || []).map(l => `
        <div class="label-note-card">
            <div class="label-note-head"><strong>${escapeHtml(l.username)}</strong><span class="status-pill" data-status="${escapeHtml(l.status)}">${escapeHtml(l.status)}</span></div>
            ${l.gapNote ? `<div><strong>Ontology-gap note:</strong> ${escapeHtml(l.gapNote)}</div>` : ""}
            <div><strong>Memo:</strong> ${escapeHtml(l.notes || "—")}</div>
        </div>`).join("")}</div>`;
}

function queueNavigation(id) {
    const rows = filteredQueueParagraphs();
    const index = rows.findIndex(p => p.paragraphDbId === id);
    return {
        previous: index > 0 ? rows[index - 1] : null,
        next: index >= 0 && index < rows.length - 1 ? rows[index + 1] : null,
        index,
        total: rows.length
    };
}

function openQueueParagraph(id) {
    const p = (queueData?.paragraphs || []).find(x => x.paragraphDbId === id);
    if (!p) return;
    currentQueueParagraphId = id;
    $(".queue-paragraph-item").removeClass("active");
    $(`.queue-paragraph-item[data-id="${id}"]`).addClass("active");

    const nav = queueNavigation(id);
    const pairQuery = `labelerA=${encodeURIComponent(queueData.selectedLabelerAId)}&labelerB=${encodeURIComponent(queueData.selectedLabelerBId)}`;

    $("#queueEmpty").addClass("hidden");
    $("#queueDetail").removeClass("hidden").html(`
        <div class="queue-detail-actions">
            <button type="button" id="queuePrevious" ${nav.previous ? "" : "disabled"}>← Previous</button>
            <span class="muted">${nav.index + 1} / ${nav.total}</span>
            <button type="button" id="queueNext" ${nav.next ? "" : "disabled"}>Next →</button>
            <a class="button-link small-button" href="/review/filing/${p.filingId}?${pairQuery}">Open filing view</a>
        </div>
        <div class="paragraph-card review-paragraph-card">
            <div class="paragraph-head">
                <div><strong>${escapeHtml(p.company)}</strong><span class="compare-status ${statusClass(p.compareStatus)}">${escapeHtml(p.compareStatus)}</span></div>
                <span>FY${escapeHtml(p.fiscalYear)} · ${escapeHtml(p.paragraphId)}</span>
            </div>
            <div class="muted queue-filing-meta">CIK ${escapeHtml(p.masterCik || "")} · ${escapeHtml(p.filename || "")} · ${escapeHtml(stageLabel(p.datasetStage))}</div>
            <div class="paragraph-text">${escapeHtml(p.text)}</div>
        </div>
        <div class="review-difference-banner ${p.compareStatus === "DISAGREEMENT" ? "has-diff" : ""}">
            <strong>${p.compareStatus === "DISAGREEMENT" ? "Differences:" : p.compareStatus === "AGREEMENT" ? "The selected labelers agree on coded gates, dimensions, and attributes." : "Comparison incomplete."}</strong>
            ${p.differences?.length ? `<span>${escapeHtml(p.differences.join(" · "))}</span>` : ""}
        </div>
        ${renderComparisonMatrix(p.labels || [])}
        ${renderLabelNotes(p.labels || [])}
    `);

    $("#queuePrevious").on("click", () => nav.previous && openQueueParagraph(nav.previous.paragraphDbId));
    $("#queueNext").on("click", () => nav.next && openQueueParagraph(nav.next.paragraphDbId));
}

function applyQueueData(data) {
    queueData = data;
    currentQueueParagraphId = null;
    renderQueuePairSelectors();
    renderQueueCounts();
    renderQueueParagraphList();
}

function loadQueue(labelerAId = null, labelerBId = null, stage = null) {
    const params = new URLSearchParams();
    if (labelerAId != null) params.set("labelerA", labelerAId);
    if (labelerBId != null) params.set("labelerB", labelerBId);
    params.set("stage", stage || $("#queueStage").val() || "CALIBRATION");

    $("#queuePairMessage").text("Loading comparison…");
    $.getJSON(`/api/review/paragraphs?${params.toString()}`)
        .done(applyQueueData)
        .fail(xhr => {
            const message = xhr.responseJSON?.error || "Could not load paragraph comparison.";
            $("#queuePairMessage").text(message);
            $("#queueEmpty").removeClass("hidden").text(message);
        });
}

$(function() {
    $.getJSON("/api/ontology")
        .done(data => {
            queueOntology = data || [];
            loadQueue();
        })
        .fail(() => {
            $("#queueEmpty").text("Could not load ontology definitions for comparison.");
        });

    $("#queueCompareBtn").on("click", function() {
        const a = Number($("#queueLabelerA").val());
        const b = Number($("#queueLabelerB").val());
        if (!a || !b) {
            $("#queuePairMessage").text("Choose two labelers.");
            return;
        }
        if (a === b) {
            $("#queuePairMessage").text("Choose two different labelers.");
            return;
        }
        $("#queueStatusFilter").val("DISAGREEMENT");
        $("#queueDifferenceFilter").val("ALL");
        loadQueue(a, b, $("#queueStage").val());
    });

    $("#queueStage").on("change", function() {
        const a = Number($("#queueLabelerA").val());
        const b = Number($("#queueLabelerB").val());
        if (a && b && a !== b) loadQueue(a, b, $(this).val());
    });

    $("#queueStatusFilter, #queueDifferenceFilter, #queueSearch").on("change input", renderQueueParagraphList);
});
