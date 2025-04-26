const userList = document.getElementById("user-list");
const loadMoreButton = document.getElementById("load-more-button");
const spinner = document.getElementById("spinner");

let currentPage = 0;
const pageSize = 10;
let totalPages = null;

async function fetchUsers(page) {
    spinner.style.display = "block";
    loadMoreButton.disabled = true;

    const response = await fetch(`/api/users?page=${page}&size=${pageSize}`);
    const data = await response.json();

    renderUsers(data.content);

    totalPages = data.totalPages;
    currentPage = data.number;

    spinner.style.display = "none";
    loadMoreButton.disabled = false;

    if (currentPage + 1 >= totalPages) {
        loadMoreButton.style.display = "none";
    }
}

function renderUsers(users) {
    users.forEach(user => {
        const div = document.createElement("div");
        div.className = "user-item";
        div.innerHTML = `<a href="/users/${user.id}">${user.name}</a>`;
        userList.appendChild(div);
    });
}

loadMoreButton.addEventListener("click", () => {
    fetchUsers(currentPage + 1);
});

fetchUsers(currentPage);
