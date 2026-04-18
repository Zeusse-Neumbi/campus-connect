// Parent Management page JS
const modal = document.getElementById("parentModal");
const parentIdInput = document.getElementById("parentId");
const userIdInput = document.getElementById("userId");

function openModal(id, userId, address, occupation) {
    modal.style.display = "block";
    parentIdInput.value = id;
    userIdInput.value = userId;
    document.getElementById("address").value = address !== "null" ? address : "";
    document.getElementById("occupation").value = occupation !== "null" ? occupation : "";
}

function closeModal() {
    modal.style.display = "none";
}

function deleteParent(id) {
    if(confirm("Are you sure you want to delete this parent?")) {
        document.getElementById("deleteId").value = id;
        document.getElementById("deleteForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}
