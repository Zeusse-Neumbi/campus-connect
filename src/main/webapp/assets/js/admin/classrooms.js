// Classroom Management page JS
const modal = document.getElementById("classroomModal");
const modalTitle = document.getElementById("modalTitle");
const formAction = document.getElementById("formAction");
const classroomIdInput = document.getElementById("classroomId");

function generateRoomCode() {
    if (formAction.value === 'update') return;

    const buildSelect = document.getElementById("building").value;
    const floor = document.getElementById("floor").value;
    const roomNo = document.getElementById("roomNo").value;

    if (!buildSelect || !floor || !roomNo) {
        document.getElementById("roomCode").value = "";
        return;
    }

    const initials = buildSelect.split(' ').map(w => w[0]).join('').toUpperCase();
    // Pad room number to 2 digits
    const padRoom = roomNo.toString().padStart(2, '0');

    document.getElementById("roomCode").value = initials + "-" + floor + padRoom;
}

function openModal(mode, id, roomCode, buildingStr, capacity) {
    modal.style.display = "block";
    if (mode === 'create') {
        modalTitle.innerText = "Add New Classroom";
        formAction.value = "create";
        document.getElementById("classroomForm").reset();
        classroomIdInput.value = "";
        document.getElementById("roomCode").readOnly = true;
        document.getElementById("floor").disabled = false;
        document.getElementById("roomNo").disabled = false;
    } else {
        modalTitle.innerText = "Edit Classroom";
        formAction.value = "update";
        classroomIdInput.value = id;
        document.getElementById("roomCode").value = roomCode;
        document.getElementById("roomCode").readOnly = false;
        document.getElementById("capacity").value = capacity;

        document.getElementById("floor").disabled = true;
        document.getElementById("roomNo").disabled = true;
        document.getElementById("floor").value = "";
        document.getElementById("roomNo").value = "";

        document.getElementById("building").value = buildingStr;
    }
}

function closeModal() {
    modal.style.display = "none";
}

function deleteClassroom(id) {
    if(confirm("Are you sure you want to delete this classroom?")) {
        document.getElementById("deleteId").value = id;
        document.getElementById("deleteForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}
