// Student Courses page JS — Enroll modal
function openEnrollModal() {
    document.getElementById('enrollModal').style.display = "block";
}

function closeEnrollModal() {
    document.getElementById('enrollModal').style.display = "none";
}

// Close modal if clicked outside
window.onclick = function(event) {
    var modal = document.getElementById('enrollModal');
    if (event.target == modal) {
        modal.style.display = "none";
    }
}
