const companyList = document.getElementById("company-list");
const prevButton = document.getElementById("prev-button");
const nextButton = document.getElementById("next-button");
const pageInfo = document.getElementById("page-info");

const pageSize = 10;
let currentPage = 0;

const urlParams = new URLSearchParams(window.location.search);
const initialPage = parseInt(urlParams.get("page")) || 0;

async function fetchCompanies(page) {
    const response = await fetch(`/api/companies?page=${page}&size=${pageSize}`);
    const data = await response.json();

    renderCompanies(data.content);
    updatePagination(data.number, data.totalPages);

    window.history.pushState({ page }, '', `/companies?page=${page}`);
}

function renderCompanies(companies) {
    companyList.innerHTML = ""; 
    companies.forEach(company => {
        const div = document.createElement("div");
        div.className = "company-item";
        div.innerHTML = `<a href="/companies/${company.id}">${company.name}</a>`;
        companyList.appendChild(div);
    });
}

function updatePagination(pageNumber, totalPages) {
    currentPage = pageNumber;

    prevButton.style.display = (pageNumber > 0) ? "inline-block" : "none";
    nextButton.style.display = (pageNumber + 1 < totalPages) ? "inline-block" : "none";

    if (pageInfo) {
        pageInfo.textContent = `Página ${pageNumber + 1} de ${totalPages}`;
    }
}

prevButton.addEventListener("click", () => {
    if (currentPage > 0) {
        fetchCompanies(currentPage - 1);
    }
});

nextButton.addEventListener("click", () => {
    fetchCompanies(currentPage + 1);
});

window.addEventListener("popstate", (event) => {
    const page = event.state?.page ?? 0;
    fetchCompanies(page);
});

fetchCompanies(initialPage);
