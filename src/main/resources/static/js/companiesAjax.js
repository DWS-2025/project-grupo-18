const companyList = document.getElementById("company-list");
const loadMoreButton = document.getElementById("load-more-button");
const spinner = document.getElementById("spinner");

let currentPage = 0;
const pageSize = 10;
let totalPages = null;

async function fetchCompanies(page) {
    spinner.style.display = "block";
    loadMoreButton.disabled = true;

    const response = await fetch(`/api/companies?page=${page}&size=${pageSize}`);
    const data = await response.json();

    renderCompanies(data.content);

    totalPages = data.totalPages;
    currentPage = data.number;

    spinner.style.display = "none";
    loadMoreButton.disabled = false;

    if (currentPage + 1 >= totalPages) {
        loadMoreButton.style.display = "none";
    }
}

function renderCompanies(companies) {
    companies.forEach(company => {
        const div = document.createElement("div");
        div.className = "company-item";

        const link = document.createElement("a");
        link.href = `/companies/${company.id}`;
        link.textContent = company.name;

        div.appendChild(link);
        companyList.appendChild(div);
    });
}

loadMoreButton.addEventListener("click", () => {
    fetchCompanies(currentPage + 1);
});

fetchCompanies(currentPage);
