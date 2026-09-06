let allFilings = [];

function escapeHtml(value) {
    return $("<div>").text(value ?? "").html();
}

function renderFilings() {
    const q = $("#filingSearch").val().trim().toLowerCase();
    const rows = allFilings.filter(r => {
        const haystack = `${r.company || ""} ${r.masterCik || ""} ${r.filename || ""}`.toLowerCase();
        return !q || haystack.includes(q);
    });

    $("#filingSummary").text(`${rows.length} filing${rows.length === 1 ? "" : "s"}`);

    if (!rows.length) {
        $("#filingRows").html(`
            <tr><td colspan="7" class="empty-table">No assigned filings.</td></tr>
        `);
        return;
    }

    $("#filingRows").html(rows.map(r => `
        <tr class="clickable-row" data-filing-id="${r.filingId}">
            <td>
                <div class="assignment-company">${escapeHtml(r.company)}</div>
                <div class="muted">CIK ${escapeHtml(r.masterCik || "")} · ${escapeHtml(r.filename || "")}</div>
            </td>
            <td>${r.fiscalYear}</td>
            <td><span class="count-badge new">${r.newCount}</span></td>
            <td><span class="count-badge modified">${r.modifiedCount}</span></td>
            <td><span class="count-badge submitted">${r.submittedCount}</span></td>
            <td>${r.totalCount}</td>
            <td><a class="button-link" href="/label/filing/${r.filingId}">Open</a></td>
        </tr>
    `).join(""));

    $(".clickable-row").on("click", function (e) {
        if ($(e.target).closest("a,button,input").length) return;
        window.location.href = `/label/filing/${$(this).data("filing-id")}`;
    });
}

$(function () {
    $.getJSON("/api/my/filings")
        .done(rows => {
            allFilings = rows;
            renderFilings();
        })
        .fail(() => {
            $("#filingRows").html('<tr><td colspan="7" class="empty-table">Could not load filings.</td></tr>');
        });

    $("#filingSearch").on("input", renderFilings);
});
