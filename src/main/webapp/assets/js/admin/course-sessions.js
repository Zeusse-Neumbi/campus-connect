const modal = document.getElementById("sessionModal");
const modalTitle = document.getElementById("modalTitle");
const formAction = document.getElementById("formAction");

const slotModal = document.getElementById("slotModal");
let currentSlotDate = '';
let currentSlotStart = '';
let currentSlotEnd = '';

function openModal(mode) {
    modal.style.display = "block";
    modalTitle.innerText = "Schedule New Session";
    formAction.value = "create";
    document.getElementById("sessionForm").reset();
    
    // Auto-fill times if triggered from a slot
    if (currentSlotDate && currentSlotStart && currentSlotEnd) {
        document.getElementById("sessionDate").value = currentSlotDate;
        document.getElementById("startTime").value = currentSlotStart;
        document.getElementById("endTime").value = currentSlotEnd;
    }
}

function closeModal() {
    modal.style.display = "none";
    currentSlotDate = '';
    currentSlotStart = '';
    currentSlotEnd = '';
}

function openSlotModal(date, start, end) {
    currentSlotDate = date;
    currentSlotStart = start;
    currentSlotEnd = end;

    slotModal.style.display = "block";
    document.getElementById("slotModalTitle").innerText = "Sessions on " + date + " (" + start + " - " + end + ")";

    const container = document.getElementById("slotModalContent");
    let html = '';
    
    if (typeof sessionsData !== 'undefined') {
        const slotSessions = sessionsData.filter(s => s.date === date && s.start === start);
        if (slotSessions.length > 0) {
            slotSessions.forEach(session => {
                html += `
                <div class="session-card" style="margin-bottom: 0; position:relative; padding: 1.5rem; border: 1px solid rgba(255,255,255,0.1); border-radius: 8px; background: rgba(0,0,0,0.15); display: flex; flex-direction: column; gap: 0.5rem;">
                    <div class="session-course" style="font-size:1.1rem; font-weight: bold; padding-right: 60px;">${session.courseCode} - ${session.courseName}</div>
                    
                    <div style="font-size: 0.95rem; display: flex; align-items: center; gap: 0.5rem;">
                        <span style="color:var(--text-muted)">🕒 Time:</span> ${session.start} - ${session.end}
                    </div>
                    <div style="font-size: 0.95rem; display: flex; align-items: center; gap: 0.5rem;">
                        <span style="color:var(--text-muted)">🏢 Loc:</span> ${session.building}, Room ${session.roomCode}
                    </div>
                    <div style="font-size: 0.95rem; display: flex; align-items: center; gap: 0.5rem;">
                        <span style="color:var(--text-muted)">👥 Group:</span> ${session.group}
                    </div>

                    <button type="button" class="btn btn-sm btn-danger" style="position:absolute; top:1.5rem; right:1.5rem; padding: 0.2rem 0.5rem;" onclick="deleteSession(${session.id});">&times; Del</button>
                </div>
                `;
            });
        } else {
             html = '<div style="color: #64748b;">No sessions scheduled for this timeslot.</div>';
        }
    }
    
    container.innerHTML = html;
}

function closeSlotModal() {
    slotModal.style.display = "none";
}

function addNewSessionForSlot() {
    closeSlotModal();
    openModal('create');
}

function deleteSession(id) {
    if(confirm("Are you sure you want to delete this session?")) {
        document.getElementById("deleteId").value = id;
        document.getElementById("deleteForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    } else if (event.target == slotModal) {
        closeSlotModal();
    }
}
