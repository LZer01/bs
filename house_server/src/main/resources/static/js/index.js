function createListing() {
    document.getElementById("createModal").style.display = "block";
}

function closeModal() {
    document.getElementById("createModal").style.display = "none";
}

function submitListing() {
    const form = document.getElementById("createForm");
    const formData = new FormData(form);

    fetch("/create", {
        method: "POST",
        body: formData
    })
        .then(response => response.text())
        .then(msg => {
            alert(msg);
            closeModal();
        });
}

function toggleListingStatus() {
    const id = prompt("请输入要上下架的房源ID：");
    if (id) {
        fetch(`/toggle/${id}`, { method: "POST" })
            .then(response => response.text())
            .then(msg => alert(msg));
    }
}

function deleteListing() {
    const id = prompt("请输入要删除的房源ID：");
    if (id) {
        fetch(`/delete/${id}`, { method: "DELETE" })
            .then(response => response.text())
            .then(msg => alert(msg));
    }
}
