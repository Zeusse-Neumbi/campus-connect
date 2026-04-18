// Student-Parent Links page JS
const modal = document.getElementById("linkModal");

function openModal() {
    modal.style.display = "block";
}

function closeModal() {
    modal.style.display = "none";
}

function unlinkPair(studentId, parentId) {
    if(confirm("Are you sure you want to remove this link?")) {
        document.getElementById("unlinkStudentId").value = studentId;
        document.getElementById("unlinkParentId").value = parentId;
        document.getElementById("unlinkForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}
