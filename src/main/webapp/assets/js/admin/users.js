// User Management page JS
const modal = document.getElementById("userModal");
const modalTitle = document.getElementById("modalTitle");
const formAction = document.getElementById("formAction");
const userIdInput = document.getElementById("userId");
const passwordInput = document.getElementById("password");

// Role fields
const roleSelect = document.getElementById("roleId");
const studentFields = document.getElementById("studentFields");
const teacherFields = document.getElementById("teacherFields");
const parentFields = document.getElementById("parentFields");

function toggleRoleFields() {
    const role = roleSelect.value;
    // Reset
    studentFields.style.display = "none";
    teacherFields.style.display = "none";
    parentFields.style.display = "none";

    // Remove required attributes to avoid validations on hidden fields
    document.getElementById("studentNumber").required = false;
    document.getElementById("dateOfBirth").required = false;
    document.getElementById("employeeId").required = false;
    document.getElementById("specialization").required = false;
    document.getElementById("address").required = false;
    document.getElementById("occupation").required = false;

    if (role === "3") { // Student
        studentFields.style.display = "block";
        if (formAction.value === 'create') {
            document.getElementById("studentNumber").required = true;
            document.getElementById("dateOfBirth").required = true;
        }
    } else if (role === "2") { // Teacher
        teacherFields.style.display = "block";
        if (formAction.value === 'create') {
            document.getElementById("employeeId").required = true;
            document.getElementById("specialization").required = true;
        }
    } else if (role === "4") { // Parent
        parentFields.style.display = "block";
        if (formAction.value === 'create') {
            document.getElementById("address").required = true;
            document.getElementById("occupation").required = true;
        }
    }
}

function openModal(mode, id, email, firstName, lastName, roleId, phone) {
    modal.style.display = "block";
    if (mode === 'create') {
        modalTitle.innerText = "Add New User";
        formAction.value = "create";
        document.getElementById("userForm").reset();
        userIdInput.value = "";
        document.getElementById("phone").value = "";
        passwordInput.required = true;
        passwordInput.placeholder = "Enter password";
        roleSelect.value = "3"; // Default to student
        // Clear student/teacher/parent fields
        document.getElementById("studentNumber").value = "";
        document.getElementById("dateOfBirth").value = "";
        document.getElementById("employeeId").value = "";
        document.getElementById("specialization").value = "";
        document.getElementById("address").value = "";
        document.getElementById("occupation").value = "";
    } else {
        modalTitle.innerText = "Edit User";
        formAction.value = "update";
        userIdInput.value = id;
        document.getElementById("firstName").value = firstName;
        document.getElementById("lastName").value = lastName;
        document.getElementById("email").value = email;
        document.getElementById("phone").value = phone || "";
        roleSelect.value = roleId;
        passwordInput.required = false;
        passwordInput.placeholder = "Leave blank to keep current";

        // Read student/teacher/parent data from the edit button's data attributes
        var editBtn = document.getElementById("editBtn-" + id);
        if (editBtn) {
            var studentNumber = editBtn.getAttribute("data-student-number") || "";
            var dateOfBirth = editBtn.getAttribute("data-date-of-birth") || "";
            var employeeId = editBtn.getAttribute("data-employee-id") || "";
            var specialization = editBtn.getAttribute("data-specialization") || "";
            var address = editBtn.getAttribute("data-parent-address") || "";
            var occupation = editBtn.getAttribute("data-parent-occupation") || "";

            document.getElementById("studentNumber").value = studentNumber;
            document.getElementById("dateOfBirth").value = dateOfBirth;
            document.getElementById("employeeId").value = employeeId;
            document.getElementById("specialization").value = specialization;
            document.getElementById("address").value = address;
            document.getElementById("occupation").value = occupation;
        }
    }
    toggleRoleFields();
}

function closeModal() {
    modal.style.display = "none";
}

function deleteUser(id) {
    if(confirm("Are you sure you want to delete this user?")) {
        document.getElementById("deleteId").value = id;
        document.getElementById("deleteForm").submit();
    }
}

window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}
