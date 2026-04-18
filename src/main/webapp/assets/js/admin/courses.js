// Course Management page JS
const modal = document.getElementById("courseModal");
const modalTitle = document.getElementById("modalTitle");
const formAction = document.getElementById("formAction");
const courseIdInput = document.getElementById("courseId");

function openModal(mode, id, courseName, courseCode, teacherId, credits) {
    modal.style.display = "block";
    if (mode === 'create') {
        modalTitle.innerText = "Create New Course";
        formAction.value = "create";
        document.getElementById("courseForm").reset();
        courseIdInput.value = "";
    } else {
        modalTitle.innerText = "Edit Course";
        formAction.value = "update";
        courseIdInput.value = id;
        document.getElementById("courseName").value = courseName;
        document.getElementById("courseCode").value = courseCode;
        document.getElementById("teacherId").value = teacherId;
        document.getElementById("credits").value = credits;
    }
}

function closeModal() {
    modal.style.display = "none";
}

function deleteCourse(id) {
    if(confirm("Are you sure you want to delete this course?")) {
        document.getElementById("deleteId").value = id;
        document.getElementById("deleteForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}
