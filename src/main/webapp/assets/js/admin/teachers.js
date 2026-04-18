// Teacher Management page JS
const modal = document.getElementById("teacherModal");
const modalTitle = document.getElementById("modalTitle");
const formAction = document.getElementById("formAction");
const teacherIdInput = document.getElementById("teacherId");
const userIdInput = document.getElementById("userId");

function openModal(mode, id, userId, employeeId, specialization) {
    modal.style.display = "block";
    if (mode === 'create') {
        modalTitle.innerText = "Add New Teacher";
        formAction.value = "create";
        document.getElementById("teacherForm").reset();
        teacherIdInput.value = "";
    } else {
        modalTitle.innerText = "Edit Teacher";
        formAction.value = "update";
        teacherIdInput.value = id;
        userIdInput.value = userId;
        document.getElementById("employeeId").value = employeeId;
        document.getElementById("specialization").value = specialization;
    }
}

function closeModal() {
    modal.style.display = "none";
}

function deleteTeacher(id) {
    if(confirm("Are you sure you want to remove this teacher record?")) {
        document.getElementById("deleteId").value = id;
        document.getElementById("deleteForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}
