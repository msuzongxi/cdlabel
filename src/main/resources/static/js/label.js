let ontology = [];
let filingId = null;
let filing = null;
let activeStatus = "NEW";
let currentParagraphId = null;
let currentDetail = null;
let dirty = false;
let suppressDirty = false;
let currentList = [];
let gateDecision = null;

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


function helpTip(title, meaning, positive, negative, boundary) {
    const parts = [];
    if (meaning) parts.push(`<div class="tip-block"><strong>Meaning</strong><span>${escapeHtml(meaning)}</span></div>`);
    if (positive) parts.push(`<div class="tip-block positive"><strong>Positive example</strong><span>${escapeHtml(positive)}</span></div>`);
    if (negative) parts.push(`<div class="tip-block negative"><strong>Negative example</strong><span>${escapeHtml(negative)}</span></div>`);
    if (boundary) parts.push(`<div class="tip-block"><strong>Boundary</strong><span>${escapeHtml(boundary)}</span></div>`);
    return `<span class="info-tip" tabindex="0" role="button" aria-label="Help for ${escapeHtml(title)}">ⓘ<span class="info-popover">${parts.join("")}</span></span>`;
}

function markDirty() {
    if (suppressDirty || !currentParagraphId) return;
    dirty = true;
    $("#dirtyStatus").text("Unsaved changes");
}

function clearDirty() {
    dirty = false;
    $("#dirtyStatus").text("");
}

function decisionToGates(decision) {
    switch (decision) {
        case "PASS": return { cyberNexus: "YES", registrantAttribution: "YES", governanceProcess: "YES" };
        case "FAIL1": return { cyberNexus: "NO", registrantAttribution: null, governanceProcess: null };
        case "FAIL2": return { cyberNexus: "YES", registrantAttribution: "NO", governanceProcess: null };
        case "FAIL3": return { cyberNexus: "YES", registrantAttribution: "YES", governanceProcess: "NO" };
        default: return { cyberNexus: null, registrantAttribution: null, governanceProcess: null };
    }
}

function deriveGateDecision(annotation) {
    if (!annotation) return null;
    if (annotation.cyberNexus === "YES"
        && annotation.registrantAttribution === "YES"
        && annotation.governanceProcess === "YES") return "PASS";
    if (annotation.cyberNexus === "NO") return "FAIL1";
    if (annotation.cyberNexus === "YES" && annotation.registrantAttribution === "NO") return "FAIL2";
    if (annotation.cyberNexus === "YES"
        && annotation.registrantAttribution === "YES"
        && annotation.governanceProcess === "NO") return "FAIL3";
    return null;
}

function decisionSummary(decision) {
    switch (decision) {
        case "PASS": return "Eligible · Gate 1 YES → Gate 2 YES → Gate 3 YES";
        case "FAIL1": return "Excluded at Gate 1 · no cyber nexus";
        case "FAIL2": return "Excluded at Gate 2 · cyber nexus YES → registrant attribution NO";
        case "FAIL3": return "Excluded at Gate 3 · cyber nexus YES → registrant attribution YES → governance/process NO";
        default: return "No decision yet. If uncertain, Save a draft and add a memo.";
    }
}

function setGateDecision(decision, shouldMarkDirty = true) {
    gateDecision = decision;
    $(".gate-decision-btn").removeClass("selected");
    if (decision) {
        $(`.gate-decision-btn[data-decision="${decision}"]`).addClass("selected");
        $("#clearGateDecisionBtn").removeClass("hidden");
    } else {
        $("#clearGateDecisionBtn").addClass("hidden");
    }
    $("#gateDecisionSummary").text(decisionSummary(decision));
    updateEligibilityUi();
    refreshSubmitState();
    if (shouldMarkDirty) markDirty();
}

function updateEligibilityUi() {
    $("#dimensionSection").toggleClass("hidden", gateDecision !== "PASS");
}

function selectedAttributeCount(code) {
    return $(`.attr-present[data-dimension="${code}"]:checked`).length;
}

function updateAttributeCount(code) {
    const count = selectedAttributeCount(code);
    const badge = $(`.attribute-count[data-code="${code}"]`);
    badge.text(`${count} attribute${count === 1 ? "" : "s"}`);
    badge.toggleClass("has-value", count > 0);
}

function renderOntology() {
    $("#dimensionList").html(ontology.map(d => `
        <div class="dimension-card" data-code="${d.code}">
            <label class="dimension-header">
                <input type="checkbox" class="dimension-select" data-code="${d.code}">
                <span class="dimension-heading-text">
                    <span class="dimension-title-row">
                        <span class="dimension-title">${escapeHtml(d.code)} · ${escapeHtml(d.name)}</span>
                        ${helpTip(`${d.code} ${d.name}`, d.definition || d.question, d.positiveExample, d.negativeExample, d.boundaryNote)}
                    </span>
                    <span class="dimension-aspect">${escapeHtml(d.aspect)}</span>
                    <span class="dimension-question">${escapeHtml(d.question)}</span>
                </span>
                <span class="attribute-count" data-code="${d.code}">0 attributes</span>
            </label>
            <div class="dimension-body hidden" data-code="${d.code}">
                <div class="attribute-title">
                    <strong>Attributes</strong>
                    <span class="muted">Mark only facts explicitly stated in this paragraph.</span>
                </div>
                <div class="attribute-list">
                    ${d.attributes.map(a => `
                        <div class="attribute-item" data-dimension="${d.code}" data-attribute="${a.code}">
                            <label class="attribute-choice">
                                <input type="checkbox" class="attr-present"
                                       data-dimension="${d.code}" data-attribute="${a.code}">
                                <span class="attribute-label-text">${escapeHtml(a.label)}</span>
                                ${helpTip(a.label, a.meaning, a.positiveExample, a.negativeExample, null)}
                            </label>
                            <input class="attr-value hidden" type="text"
                                   data-dimension="${d.code}" data-attribute="${a.code}"
                                   placeholder="optional value/detail (e.g., quarterly, CISO)">
                        </div>
                    `).join("")}
                </div>

                <details class="dimension-optional">
                    <summary>Optional evidence / confidence / ambiguity</summary>
                    <div class="dimension-footer">
                        <div>
                            <label><strong>Evidence text</strong> <span class="muted">optional</span></label>
                            <textarea class="evidence-area" data-code="${d.code}" rows="2"
                                      placeholder="Optional: paste/capture the shortest supporting phrase"></textarea>
                            <input type="hidden" class="evidence-start" data-code="${d.code}">
                            <input type="hidden" class="evidence-end" data-code="${d.code}">
                            <div class="evidence-actions">
                                <button type="button" class="small-button capture-evidence" data-code="${d.code}">Capture selected paragraph text</button>
                                <span class="evidence-hint muted" data-code="${d.code}"></span>
                            </div>
                        </div>
                        <label>Confidence
                            <select class="confidence" data-code="${d.code}">
                                <option value="HIGH">High</option>
                                <option value="MEDIUM" selected>Medium</option>
                                <option value="LOW">Low</option>
                            </select>
                        </label>
                        <label>Dimension note / ambiguity
                            <textarea class="ambiguity" data-code="${d.code}" rows="2"></textarea>
                        </label>
                    </div>
                </details>
            </div>
        </div>
    `).join(""));

    $(".info-tip").on("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).toggleClass("open");
        $(".info-tip").not(this).removeClass("open");
    });

    $(".info-tip").on("keydown", function (e) {
        if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            e.stopPropagation();
            $(this).toggleClass("open");
            $(".info-tip").not(this).removeClass("open");
        }
        if (e.key === "Escape") $(this).removeClass("open");
    });

    $(".dimension-select").on("change", function () {
        const code = $(this).data("code");
        $(`.dimension-body[data-code="${code}"]`).toggleClass("hidden", !this.checked);
        if (this.checked) {
            $("#ontologyGap").prop("checked", false);
            $("#gapNote").val("").prop("disabled", true);
        }
        updateAttributeCount(code);
        refreshSubmitState();
        markDirty();
    });

    $(".attr-present").on("change", function () {
        const code = $(this).data("dimension");
        const attr = $(this).data("attribute");
        const detail = $(`.attr-value[data-dimension="${code}"][data-attribute="${attr}"]`);
        detail.toggleClass("hidden", !this.checked);
        if (!this.checked) detail.val("");
        updateAttributeCount(code);
        refreshSubmitState();
        markDirty();
    });

    $(".capture-evidence").on("click", function () {
        useSelectionForDimension($(this).data("code"));
    });
}

function updateHeader(summary) {
    filing = summary;
    $("#company").text(summary.company);
    $("#filingMeta").text(`FY${summary.fiscalYear} · CIK ${summary.masterCik || ""} · ${summary.filename || ""}`);
    $("#newCount").text(summary.newCount);
    $("#modifiedCount").text(summary.modifiedCount);
    $("#submittedCount").text(summary.submittedCount);
    $("#filingCounts").html(`
        <span><strong>${summary.newCount}</strong> new</span>
        <span><strong>${summary.modifiedCount}</strong> modified</span>
        <span><strong>${summary.submittedCount}</strong> submitted</span>
        <span><strong>${summary.totalCount}</strong> total</span>
    `);
}

function chooseInitialStatus() {
    if (filing.newCount > 0) return "NEW";
    if (filing.modifiedCount > 0) return "MODIFIED";
    return "SUBMITTED";
}

function setActiveTab(status) {
    activeStatus = status;
    $(".status-tab").removeClass("active");
    $(`.status-tab[data-status="${status}"]`).addClass("active");
}

function loadFiling(initial = false) {
    return $.getJSON(`/api/my/filings/${filingId}`).done(summary => {
        updateHeader(summary);
        if (initial) setActiveTab(chooseInitialStatus());
    });
}

function loadParagraphList(options = {}) {
    const openFirst = options.openFirst === true;
    const keepCurrent = options.keepCurrent === true;
    const openAfterId = options.openAfterId ?? null;

    return $.getJSON(`/api/my/filings/${filingId}/paragraphs`, { status: activeStatus })
        .done(rows => {
            currentList = rows;
            renderParagraphList();

            if (keepCurrent && currentParagraphId) {
                const exists = rows.some(r => r.id === currentParagraphId);
                if (exists) highlightCurrent();
                return;
            }

            if (openAfterId !== null && rows.length) {
                const idx = rows.findIndex(r => r.id === openAfterId);
                const next = idx >= 0 ? rows[(idx + 1) % rows.length] : rows[0];
                openParagraph(next.id, true);
                return;
            }

            if (openFirst && rows.length) {
                openParagraph(rows[0].id, true);
            } else if (!rows.length && openFirst) {
                clearCodingPanel(`No ${activeStatus.toLowerCase()} paragraphs in this filing.`);
            }
        });
}

function renderParagraphList() {
    if (!currentList.length) {
        $("#paragraphList").html(`<div class="empty-list">No ${activeStatus.toLowerCase()} paragraphs.</div>`);
        return;
    }

    $("#paragraphList").html(currentList.map(r => `
        <button type="button" class="paragraph-list-item ${r.id === currentParagraphId ? "active" : ""}"
                data-id="${r.id}">
            <span class="paragraph-list-id">${escapeHtml(r.paragraphId)}</span>
            <span class="paragraph-list-section">${escapeHtml(r.sectionHint || r.headingContext || "")}</span>
            <span class="paragraph-list-words">${r.wordCount || ""}${r.wordCount ? " words" : ""}</span>
        </button>
    `).join(""));

    $(".paragraph-list-item").on("click", function () {
        openParagraph(Number($(this).data("id")), false);
    });
}

function highlightCurrent() {
    $(".paragraph-list-item").removeClass("active");
    $(`.paragraph-list-item[data-id="${currentParagraphId}"]`).addClass("active");
}

function clearCodingPanel(message) {
    currentParagraphId = null;
    currentDetail = null;
    gateDecision = null;
    clearDirty();
    $("#codingForm").addClass("hidden");
    $("#emptyCoding").removeClass("hidden").text(message || "Select a paragraph from the left.");
}

function resetForm() {
    suppressDirty = true;
    gateDecision = null;
    $(".gate-decision-btn").removeClass("selected");
    $("#clearGateDecisionBtn").addClass("hidden");
    $("#gateDecisionSummary").text(decisionSummary(null));
    $(".dimension-select").prop("checked", false);
    $(".dimension-body").addClass("hidden");
    $(".attr-present").prop("checked", false);
    $(".attr-value").val("").addClass("hidden");
    $(".attribute-count").text("0 attributes").removeClass("has-value");
    $(".evidence-area").val("");
    $(".evidence-start, .evidence-end").val("");
    $(".evidence-hint").text("");
    $(".confidence").val("MEDIUM");
    $(".ambiguity").val("");
    $(".dimension-optional").prop("open", false);
    $("#ontologyGap").prop("checked", false);
    $("#gapNote").val("").prop("disabled", true);
    $("#notes").val("");
    $("#dimensionSection").addClass("hidden");
    suppressDirty = false;
    clearDirty();
    refreshSubmitState();
}

function openParagraph(id, force) {
    if (!force && dirty && id !== currentParagraphId) {
        if (!confirm("You have unsaved changes. Discard them and open another paragraph?")) return;
    }

    $("#saveStatus").text("Loading…");
    $.getJSON(`/api/my/filings/${filingId}/paragraphs/${id}`)
        .done(detail => {
            currentParagraphId = id;
            currentDetail = detail;
            showDetail(detail);
            highlightCurrent();
            $("#saveStatus").text("");
        })
        .fail(xhr => $("#saveStatus").text(readError(xhr, "Load failed")));
}

function showDetail(detail) {
    resetForm();
    const p = detail.paragraph;
    $("#emptyCoding").addClass("hidden");
    $("#codingForm").removeClass("hidden");
    $("#paragraphId").text(p.paragraphId);
    $("#paragraphStatus").text(detail.status).attr("data-status", detail.status);
    $("#sectionHint").text([p.sectionHint, p.headingContext].filter(Boolean).join(" · "));
    $("#paragraphText").text(p.text);

    if (!detail.annotation) {
        setGateDecision(null, false);
        clearDirty();
        return;
    }

    suppressDirty = true;
    const a = detail.annotation;
    setGateDecision(deriveGateDecision(a), false);

    $("#ontologyGap").prop("checked", !!a.ontologyGap);
    $("#gapNote").val(a.gapNote || "").prop("disabled", !a.ontologyGap);
    $("#notes").val(a.notes || "");

    (a.dimensions || []).forEach(d => {
        $(`.dimension-select[data-code="${d.dimensionCode}"]`).prop("checked", true);
        $(`.dimension-body[data-code="${d.dimensionCode}"]`).removeClass("hidden");
        $(`.evidence-area[data-code="${d.dimensionCode}"]`).val(d.evidenceText || "");
        $(`.evidence-start[data-code="${d.dimensionCode}"]`).val(d.evidenceStart ?? "");
        $(`.evidence-end[data-code="${d.dimensionCode}"]`).val(d.evidenceEnd ?? "");
        $(`.confidence[data-code="${d.dimensionCode}"]`).val(d.confidence || "MEDIUM");
        $(`.ambiguity[data-code="${d.dimensionCode}"]`).val(d.ambiguityNote || "");

        (d.attributes || []).forEach(attr => {
            const present = !!attr.present;
            $(`.attr-present[data-dimension="${d.dimensionCode}"][data-attribute="${attr.attributeCode}"]`)
                .prop("checked", present);
            const input = $(`.attr-value[data-dimension="${d.dimensionCode}"][data-attribute="${attr.attributeCode}"]`);
            input.val(attr.valueText || "").toggleClass("hidden", !present);
        });
        updateAttributeCount(d.dimensionCode);
    });
    suppressDirty = false;
    refreshSubmitState();
    clearDirty();
}

function captureSelection() {
    const selection = window.getSelection();
    if (!selection || selection.rangeCount !== 1 || selection.isCollapsed) return null;
    const container = document.getElementById("paragraphText");
    const range = selection.getRangeAt(0);
    if (!container.contains(range.commonAncestorContainer)) return null;

    const pre = range.cloneRange();
    pre.selectNodeContents(container);
    pre.setEnd(range.startContainer, range.startOffset);
    let start = pre.toString().length;
    const raw = range.toString();
    const trimmedStart = raw.length - raw.trimStart().length;
    const text = raw.trim();
    if (!text) return null;
    start += trimmedStart;
    return { text, start, end: start + text.length };
}

function useSelectionForDimension(code) {
    const selected = captureSelection();
    const hint = $(`.evidence-hint[data-code="${code}"]`);
    if (!selected) {
        hint.text("Select text inside the paragraph first.");
        return;
    }
    $(`.evidence-area[data-code="${code}"]`).val(selected.text);
    $(`.evidence-start[data-code="${code}"]`).val(selected.start);
    $(`.evidence-end[data-code="${code}"]`).val(selected.end);
    hint.text(`Captured characters ${selected.start}–${selected.end}.`);
    markDirty();
}

function collectPayload() {
    const gates = decisionToGates(gateDecision);
    const payload = {
        cyberNexus: gates.cyberNexus,
        registrantAttribution: gates.registrantAttribution,
        governanceProcess: gates.governanceProcess,
        ontologyGap: gateDecision === "PASS" && $("#ontologyGap").prop("checked"),
        gapNote: $("#gapNote").val(),
        notes: $("#notes").val(),
        dimensions: []
    };

    if (gateDecision === "PASS" && !payload.ontologyGap) {
        $(".dimension-select:checked").each(function () {
            const code = $(this).data("code");
            const attributes = [];
            $(`.attr-present[data-dimension="${code}"]`).each(function () {
                const attributeCode = $(this).data("attribute");
                attributes.push({
                    attributeCode,
                    present: this.checked,
                    valueText: this.checked
                        ? $(`.attr-value[data-dimension="${code}"][data-attribute="${attributeCode}"]`).val()
                        : null
                });
            });
            const startValue = $(`.evidence-start[data-code="${code}"]`).val();
            const endValue = $(`.evidence-end[data-code="${code}"]`).val();
            payload.dimensions.push({
                dimensionCode: code,
                evidenceText: $(`.evidence-area[data-code="${code}"]`).val(),
                evidenceStart: startValue === "" ? null : Number(startValue),
                evidenceEnd: endValue === "" ? null : Number(endValue),
                confidence: $(`.confidence[data-code="${code}"]`).val(),
                ambiguityNote: $(`.ambiguity[data-code="${code}"]`).val(),
                attributes
            });
        });
    }
    return payload;
}

function canSubmitCurrent() {
    if (!currentParagraphId || !gateDecision) return false;
    if (gateDecision !== "PASS") return true;

    if ($("#ontologyGap").prop("checked")) {
        return !!$("#gapNote").val().trim();
    }

    const dimensions = $(".dimension-select:checked");
    if (!dimensions.length) return false;

    let allHaveAttributes = true;
    dimensions.each(function () {
        const code = $(this).data("code");
        if (selectedAttributeCount(code) < 1) allHaveAttributes = false;
    });
    return allHaveAttributes;
}

function refreshSubmitState() {
    $("#submitBtn").prop("disabled", !canSubmitCurrent());
}

function saveCurrent(submit) {
    if (!currentParagraphId) return;
    if (submit && !canSubmitCurrent()) return;

    const endpoint = submit ? "submit" : "save";
    const payload = collectPayload();

    $("#saveBtn, #submitBtn").prop("disabled", true);
    $("#saveStatus").text(submit ? "Submitting…" : "Saving…");

    $.ajax({
        url: `/api/my/filings/${filingId}/paragraphs/${currentParagraphId}/${endpoint}`,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(payload)
    }).done(result => {
        clearDirty();
        updateHeader(result.filing);
        $("#paragraphStatus").text(result.status).attr("data-status", result.status);
        $("#saveStatus").text(submit ? "Submitted." : "Saved.");

        if (submit) {
            if (activeStatus === "SUBMITTED") {
                loadParagraphList({ openAfterId: currentParagraphId });
            } else {
                loadParagraphList({ openFirst: true });
            }
        } else {
            loadParagraphList({ keepCurrent: true });
        }
    }).fail(xhr => {
        $("#saveStatus").text(readError(xhr, submit ? "Submit failed" : "Save failed"));
    }).always(() => {
        $("#saveBtn").prop("disabled", false);
        refreshSubmitState();
    });
}

function switchTab(status) {
    if (status === activeStatus) return;
    if (dirty && !confirm("You have unsaved changes. Discard them and switch tabs?")) return;
    clearDirty();
    currentParagraphId = null;
    setActiveTab(status);
    loadParagraphList({ openFirst: true });
}

function readError(xhr, fallback) {
    return xhr.responseJSON && xhr.responseJSON.error
        ? xhr.responseJSON.error
        : `${fallback}: HTTP ${xhr.status}`;
}

function typingTarget(target) {
    const tag = (target.tagName || "").toLowerCase();
    return tag === "input" || tag === "textarea" || tag === "select" || target.isContentEditable;
}

$(function () {
    setupCsrf();
    filingId = Number($('meta[name="filing_id"]').attr("content"));

    $.when($.getJSON("/api/ontology"), $.getJSON(`/api/my/filings/${filingId}`))
        .done((ontologyResp, filingResp) => {
            ontology = ontologyResp[0];
            filing = filingResp[0];
            renderOntology();
            updateHeader(filing);
            setActiveTab(chooseInitialStatus());
            loadParagraphList({ openFirst: true });
        })
        .fail(xhr => clearCodingPanel(readError(xhr, "Could not load filing")));

    $(document).on("change input", "#codingForm textarea, #codingForm select, #codingForm .attr-value", function () {
        markDirty();
        refreshSubmitState();
    });

    $(".gate-decision-btn").on("click", function () {
        setGateDecision($(this).data("decision"), true);
    });

    $("#clearGateDecisionBtn").on("click", function () {
        setGateDecision(null, true);
    });

    $("#ontologyGap").on("change", function () {
        if (this.checked) {
            $(".dimension-select").prop("checked", false);
            $(".dimension-body").addClass("hidden");
        }
        $("#gapNote").prop("disabled", !this.checked);
        refreshSubmitState();
        markDirty();
    });

    $(".status-tab").on("click", function () {
        switchTab($(this).data("status"));
    });

    $("#saveBtn").on("click", () => saveCurrent(false));
    $("#submitBtn").on("click", () => saveCurrent(true));

    $(document).on("keydown", function (e) {
        if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "s") {
            e.preventDefault();
            saveCurrent(false);
            return;
        }
        if ((e.ctrlKey || e.metaKey) && e.key === "Enter") {
            e.preventDefault();
            saveCurrent(true);
            return;
        }
        if (!typingTarget(e.target) && !e.ctrlKey && !e.metaKey && !e.altKey) {
            const map = { "1": "PASS", "2": "FAIL1", "3": "FAIL2", "4": "FAIL3" };
            if (map[e.key]) {
                e.preventDefault();
                setGateDecision(map[e.key], true);
            }
        }
    });

    window.addEventListener("beforeunload", e => {
        if (!dirty) return;
        e.preventDefault();
        e.returnValue = "";
    });
});
