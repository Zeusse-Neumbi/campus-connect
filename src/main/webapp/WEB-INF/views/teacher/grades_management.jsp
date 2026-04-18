<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Grades Management - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/teacher-grades.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/teacher_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Grades Management</div>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #f59e0b, #d97706);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem; margin-bottom: 2rem;">
            <div style="display: flex; justify-content: space-between; align-items: end; flex-wrap: wrap; gap: 1rem;">
                <h3 style="margin: 0;">📝 Record / Update Grades</h3>
                <c:if test="${not empty selectedCourseId}">
                    <button class="btn btn-primary" onclick="document.getElementById('createExamModal').classList.add('active')">
                        + Create Assessment
                    </button>
                </c:if>
            </div>

            <form method="get" action="${pageContext.request.contextPath}/teacher/grades" style="margin-top: 1.5rem;">
                <div style="display: flex; gap: 1rem; flex-wrap: wrap; align-items: end;">
                    <div class="input-group" style="margin-bottom: 0; flex: 1;">
                        <label>Select Course</label>
                        <select name="courseId" class="input-field" onchange="this.form.submit()">
                            <option value="">-- Select a Course --</option>
                            <c:forEach var="c" items="${courses}">
                                <option value="${c.id}" ${c.id == selectedCourseId ? 'selected' : ''}>
                                    ${c.courseCode} - ${c.courseName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <c:if test="${not empty selectedCourseId && not empty exams}">
                        <div class="input-group" style="margin-bottom: 0; flex: 1;">
                            <label>Select Exam</label>
                            <select name="examId" class="input-field" onchange="this.form.submit()">
                                <option value="">-- Select an Exam --</option>
                                <c:forEach var="exam" items="${exams}">
                                    <option value="${exam.id}" ${exam.id == selectedExamId ? 'selected' : ''}>
                                        ${exam.examName} (${exam.examType}) - ${exam.examDate}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </c:if>
                </div>
            </form>

            <c:if test="${not empty selectedCourseId && empty exams}">
                <p style="color: #666; text-align: center; margin-top: 1rem;">No assessments created for this course yet. Click "Create Assessment" to add one.</p>
            </c:if>
        </div>

        <%-- Selected exam info + delete button --%>
        <c:if test="${not empty selectedExam}">
            <div class="glass-panel" style="padding: 1.2rem 2rem; margin-bottom: 1rem; display: flex; justify-content: space-between; align-items: center;">
                <div>
                    <strong>${selectedExam.examName}</strong>
                    <span class="max-score-badge" style="margin-left: 0.75rem;">Max: ${selectedExam.maxScore}</span>
                    <span style="color: #aaa; margin-left: 1rem; font-size: 0.85rem;">${selectedExam.examType} · ${selectedExam.examDate}</span>
                </div>
                <form action="${pageContext.request.contextPath}/teacher/exams/delete" method="post"
                      onsubmit="return confirm('Delete this assessment and all its grades?')">
                    <input type="hidden" name="examId" value="${selectedExam.id}">
                    <input type="hidden" name="courseId" value="${selectedCourseId}">
                    <button type="submit" class="btn btn-sm btn-danger">Delete Assessment</button>
                </form>
            </div>
        </c:if>

        <%-- Grading table --%>
        <c:if test="${not empty selectedExamId && not empty students}">
            <div class="glass-panel" style="padding: 2rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;">
                    <h3 style="margin: 0;">Grade Students</h3>
                    <input type="text" id="gradeSearch" class="input-field" placeholder="🔍 Search by name or ID..." style="max-width: 280px;" oninput="filterGradeTable()">
                </div>
                <div class="table-container">
                    <table>
                        <thead><tr><th>Student ID</th><th>Name</th><th>Current Score</th><th>New Score</th><th>Remark</th><th>Action</th></tr></thead>
                        <tbody>
                            <c:forEach var="sMap" items="${students}">
                                <tr>
                                    <td>${sMap.student.studentNumber}</td>
                                    <td>${sMap.user.firstName} ${sMap.user.lastName}</td>
                                    <td>
                                        <c:set var="sid" value="${sMap.studentId}" />
                                        <c:choose>
                                            <c:when test="${resultsMap != null && resultsMap.containsKey(sid)}">
                                                <strong>${resultsMap[sid]}</strong> / ${selectedExam.maxScore}
                                            </c:when>
                                            <c:otherwise><span style="color: #999;">Not Graded</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/teacher/grades" method="post" style="display: flex; gap: 0.5rem; align-items: center;">
                                            <input type="hidden" name="examId" value="${selectedExamId}">
                                            <input type="hidden" name="studentId" value="${sMap.studentId}">
                                            <input type="number" name="score" class="input-field" min="0" max="${selectedExam.maxScore}" step="1" required style="width: 80px;"
                                                   value="${resultsMap != null && resultsMap.containsKey(sid) ? resultsMap[sid] : ''}">
                                    </td>
                                    <td>
                                            <input type="text" name="remark" class="input-field remark-input" placeholder="Add a remark..."
                                                   value="${remarksMap != null && remarksMap.containsKey(sid) ? remarksMap[sid] : ''}">
                                    </td>
                                    <td>
                                            <button type="submit" class="btn btn-sm btn-primary">Save</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty students}">
                                <tr><td colspan="6" style="text-align: center; color: #666;">No students enrolled in this course.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>

        <c:if test="${empty selectedCourseId}">
            <div class="glass-panel" style="padding: 2rem;">
                <p style="text-align: center; color: #666;">Select a course and exam to start grading.</p>
            </div>
        </c:if>
    </div>
</div>

<%-- Create Exam Modal --%>
<div id="createExamModal" class="modal-overlay" onclick="if(event.target===this) this.classList.remove('active')">
    <div class="modal-content">
        <h3>Create New Assessment</h3>
        <form action="${pageContext.request.contextPath}/teacher/exams/create" method="post">
            <input type="hidden" name="courseId" value="${selectedCourseId}">
            <div class="input-group"><label>Assessment Name</label><input type="text" name="examName" class="input-field" placeholder="e.g. Midterm Exam" required></div>
            <div class="input-group">
                <label>Type</label>
                <select name="examType" class="input-field" required>
                    <option value="Quiz">Quiz</option><option value="Assignment">Assignment</option><option value="Midterm">Midterm</option>
                    <option value="Final">Final Exam</option><option value="Project">Project</option><option value="Lab">Lab Work</option>
                </select>
            </div>
            <div class="input-group"><label>Date</label><input type="date" name="examDate" class="input-field" required></div>
            <div class="input-group"><label>Max Score</label><input type="number" name="maxScore" class="input-field" min="1" max="100" value="20" step="1" required></div>
            <div style="display: flex; gap: 1rem; margin-top: 1rem;">
                <button type="submit" class="btn btn-primary" style="flex: 1;">Create</button>
                <button type="button" class="btn btn-danger" style="flex: 1;" onclick="document.getElementById('createExamModal').classList.remove('active')">Cancel</button>
            </div>
        </form>
    </div>
</div>

<script>
function filterGradeTable() {
    const q = document.getElementById('gradeSearch').value.toLowerCase();
    const rows = document.querySelectorAll('.table-container tbody tr');
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(q) ? '' : 'none';
    });
}
</script>
</body>
</html>
