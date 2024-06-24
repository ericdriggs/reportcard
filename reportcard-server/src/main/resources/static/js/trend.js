function applyTestFilters() {

    const testCaseStatusFilter = document.getElementById('test-case-status-filter').value;
    const testRunFilter = document.getElementById('test-run-filter').value;

    const allRows = document.querySelectorAll('.test-row');
    if (testCaseStatusFilter === "all") {
        allRows.forEach((el) => {
            el.style.display = 'table-row';
        });
    } else {
        allRows.forEach((el) => {
            el.style.display = 'none';
        });

        var filterRows;
        if (testCaseStatusFilter === "fail") {
            filterRows = document.querySelectorAll('.test-case-fail');
        } else if (testCaseStatusFilter === "skip") {
            filterRows = document.querySelectorAll('.test-case-skip');
        } else if (testCaseStatusFilter === "success") {
            filterRows = document.querySelectorAll('.test-case-success');
        }
        filterRows.forEach((el) => {
            el.style.display = 'table-row';
        });
    }
}