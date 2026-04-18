// Student Management page JS
const modal = document.getElementById("studentModal");
const modalTitle = document.getElementById("modalTitle");
const formAction = document.getElementById("formAction");
const studentIdInput = document.getElementById("studentId");
const userIdInput = document.getElementById("userId");

function openModal(mode, id, userId, studentNumber, dateOfBirth) {
    modal.style.display = "block";
    if (mode === 'create') {
        modalTitle.innerText = "Enroll New Student";
        formAction.value = "create";
        document.getElementById("studentForm").reset();
        studentIdInput.value = "";
    } else {
        modalTitle.innerText = "Edit Student";
        formAction.value = "update";
        studentIdInput.value = id;
        userIdInput.value = userId;
        document.getElementById("studentNumber").value = studentNumber;
        document.getElementById("dateOfBirth").value = dateOfBirth;
    }
}

function closeModal() {
    modal.style.display = "none";
}

function deleteStudent(id) {
    if(confirm("Are you sure you want to unenroll this student? (This does not delete the User account)")) {
        document.getElementById("deleteId").value = id;
        document.getElementById("deleteForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}
