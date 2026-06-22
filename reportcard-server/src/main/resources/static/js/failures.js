"use strict";

function initFailuresDashboard() {
    var params = new URLSearchParams(window.location.search);

    var daysSelect = document.getElementById("days-select");
    var daysParam = params.get("days") || "7";
    for (var i = 0; i < daysSelect.options.length; i++) {
        if (daysSelect.options[i].value === daysParam) {
            daysSelect.selectedIndex = i;
            break;
        }
    }

    var jobInfoParam = params.get("jobInfo");
    if (jobInfoParam) {
        var idx = jobInfoParam.indexOf(":");
        if (idx > 0) {
            document.getElementById("jobInfoKey").value = jobInfoParam.substring(0, idx);
            document.getElementById("jobInfoValue").value = jobInfoParam.substring(idx + 1);
        }
    }

    document.getElementById("failures-form").addEventListener("submit", function(e) {
        e.preventDefault();
        var btn = e.target.querySelector('button[type="submit"]');
        btn.disabled = true;
        btn.textContent = "Generating...";
        btn.style.background = "#9ca3af";
        btn.style.cursor = "wait";
        document.body.style.cursor = "wait";

        var key = document.getElementById("jobInfoKey").value.trim();
        var value = document.getElementById("jobInfoValue").value.trim();
        var days = document.getElementById("days-select").value;
        var threshold = document.getElementById("threshold-input").value.trim();
        var repos = document.getElementById("repos-input").value.trim();

        var p = new URLSearchParams();
        if (key && value) {
            p.set("jobInfo", key + ":" + value);
        }
        p.set("days", days);
        if (threshold) {
            p.set("failureThreshold", threshold);
        }
        if (repos) {
            p.set("repos", repos);
        }
        var url = window.location.pathname + "?" + p.toString();
        setTimeout(function() { window.location.href = url; }, 50);
    });

    renderCharts();
}

function renderCharts() {
    // Single chart mode (org-level)
    var dataEl = document.getElementById("daily-data");
    if (dataEl) {
        var data;
        try { data = JSON.parse(dataEl.textContent); } catch (e) { console.error("Failed to parse chart data:", e); return; }
        var container = document.getElementById("chart-container");
        if (container) {
            renderChartInto(container, data);
        }
        return;
    }

    // Multi-chart mode (company-level, per-org)
    var containers = document.querySelectorAll(".chart-container[data-daily]");
    for (var i = 0; i < containers.length; i++) {
        var el = containers[i];
        var json = el.getAttribute("data-daily");
        var data;
        try { data = JSON.parse(json); } catch (e) { console.error("Failed to parse chart data for container:", e); continue; }
        renderChartInto(el, data);
    }
}

function renderChartInto(container, data) {
    if (!data || data.length === 0) {
        container.innerHTML = "<p>No daily data available.</p>";
        return;
    }

    data.sort(function (a, b) {
        return a.date < b.date ? -1 : a.date > b.date ? 1 : 0;
    });

    var width = container.clientWidth || 800;
    var height = 280;
    var margin = { top: 20, right: 50, bottom: 40, left: 50 };
    var chartW = width - margin.left - margin.right;
    var chartH = height - margin.top - margin.bottom;

    var barWidth = Math.max(8, Math.floor(chartW / data.length) - 4);
    var barSpacing = chartW / data.length;

    var svg = '<svg viewBox="0 0 ' + width + ' ' + height + '" xmlns="http://www.w3.org/2000/svg">';

    // Y-axis labels (percentage)
    svg += '<text x="' + (margin.left - 10) + '" y="' + margin.top + '" text-anchor="end" font-size="11">100%</text>';
    svg += '<text x="' + (margin.left - 10) + '" y="' + (margin.top + chartH) + '" text-anchor="end" font-size="11">0%</text>';

    // Bars — normalized to 100% height
    for (var i = 0; i < data.length; i++) {
        var d = data[i];
        var x = margin.left + i * barSpacing + (barSpacing - barWidth) / 2;
        var passPct = d.totalTests > 0 ? d.passCount / d.totalTests : 0;
        var failPct = 1 - passPct;
        var passH = passPct * chartH;
        var failH = failPct * chartH;

        // Green bar (pass) at bottom
        var passY = margin.top + chartH - passH;
        svg += '<rect x="' + x + '" y="' + passY + '" width="' + barWidth + '" height="' + passH + '" fill="#22c55e" />';

        // Red bar (fail) stacked on top
        var failY = passY - failH;
        svg += '<rect x="' + x + '" y="' + failY + '" width="' + barWidth + '" height="' + failH + '" fill="#ef4444" />';

        // Pass% label on top of bar
        var pct = Math.round(passPct * 100);
        var pctY = failY - 4;
        svg += '<text x="' + (x + barWidth / 2) + '" y="' + pctY + '" text-anchor="middle" font-size="10" font-weight="bold">' + pct + '%</text>';

        // X-axis label (date)
        if (data.length <= 14 || i % Math.ceil(data.length / 10) === 0) {
            var labelX = x + barWidth / 2;
            var labelY = margin.top + chartH + 16;
            var dateLabel = typeof d.date === "string" ? d.date : d.date.toString();
            svg += '<text x="' + labelX + '" y="' + labelY + '" text-anchor="middle" font-size="10">' + dateLabel + '</text>';
        }
    }

    // Legend
    var legendY = height - 5;
    svg += '<rect x="' + margin.left + '" y="' + legendY + '" width="12" height="12" fill="#22c55e" />';
    svg += '<text x="' + (margin.left + 16) + '" y="' + (legendY + 10) + '" font-size="11">Pass</text>';
    svg += '<rect x="' + (margin.left + 60) + '" y="' + legendY + '" width="12" height="12" fill="#ef4444" />';
    svg += '<text x="' + (margin.left + 76) + '" y="' + (legendY + 10) + '" font-size="11">Fail</text>';

    svg += '</svg>';
    container.innerHTML = svg;
}
