// Course Group Management page JS
const modal = document.getElementById("groupModal");
const modalTitle = document.getElementById("modalTitle");
const formAction = document.getElementById("formAction");
const groupIdInput = document.getElementById("groupId");

function openModal(mode, id, courseId, groupName, capacity) {
    modal.style.display = "block";
    if (mode === 'create') {
        modalTitle.innerText = "Add New Course Group";
        formAction.value = "create";
        document.getElementById("groupForm").reset();
        groupIdInput.value = "";
    } else {
        modalTitle.innerText = "Edit Course Group";
        formAction.value = "update";
        groupIdInput.value = id;
        document.getElementById("courseId").value = courseId;
        document.getElementById("groupName").value = groupName;
        document.getElementById("capacity").value = capacity;
    }
}

function closeModal() {
    modal.style.display = "none";
}

function deleteGroup(id) {
    if(confirm("Are you sure you want to delete this group?")) {
        document.getElementById("deleteId").value = id;
        document.getElementById("deleteForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}
