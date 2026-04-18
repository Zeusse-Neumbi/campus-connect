<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Child Transcript - School Management</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
    <style>
        .transcript-header { text-align: center; margin-bottom: 2rem; border-bottom: 2px solid var(--border-color); padding-bottom: 1rem; }
        .summary-block { display: flex; justify-content: space-around; background: rgba(0,0,0,0.02); padding: 1rem; border-radius: 8px; margin-bottom: 2rem; }
        .summary-item { text-align: center; }
        .summary-value { font-size: 1.5rem; font-weight: 600; color: var(--primary-color); }
        .summary-label { font-size: 0.85rem; color: var(--text-muted); text-transform: uppercase; }
        .course-card { background: rgba(255,255,255,0.5); border: 1px solid var(--border-color); border-radius: 8px; margin-bottom: 1rem; overflow: hidden; }
        .course-header { display: flex; justify-content: space-between; align-items: center; padding: 1rem; background: rgba(0,0,0,0.03); font-weight: 500; }
        .course-stats { display: flex; gap: 1.5rem; font-size: 0.9rem; }
        .course-stat span { font-weight: 600; }
        .course-details { padding: 1rem; display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; font-size: 0.85rem; }
    </style>
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/parent_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Transcript Generator</div>
            <div style="flex-grow: 1;"></div>
            <button onclick="generatePDF()" class="btn btn-sm" style="background: #ef4444; color: white; margin-right: 1rem;">📄 Export PDF</button>
            <a href="${pageContext.request.contextPath}/parent/children" class="btn btn-sm" style="background: rgba(255,255,255,0.1); margin-right: 1rem;">← Back</a>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem; background: white; color: #111;" id="transcriptContent">
            
            <div class="transcript-header">
                <h1 style="margin:0; font-size: 1.8rem;">Official Academic Transcript</h1>
                <p style="margin: 0.5rem 0 0; color: #555;">Student: <strong>${childUser.firstName} ${childUser.lastName}</strong></p>
                <p style="margin: 0; color: #555;">Student ID: ${childStudent.studentNumber}</p>
                <p style="margin: 0; color: #555;">Generated on: <script>document.write(new Date().toLocaleDateString());</script></p>
            </div>

            <div class="summary-block">
                <div class="summary-item">
                    <div class="summary-value">${globalGpa} / 20</div>
                    <div class="summary-label">Global Average</div>
                </div>
                <div class="summary-item">
                    <div class="summary-value">${globalAttendance}%</div>
                    <div class="summary-label">Global Attendance</div>
                </div>
            </div>

            <h3 style="border-bottom: 1px solid #eee; padding-bottom: 0.5rem; margin-bottom: 1rem;">Course Details</h3>

            <c:forEach var="course" items="${transcriptCourses}">
                <div class="course-card">
                    <div class="course-header">
                        <div>
                            <strong>${course.courseName}</strong> <span style="color: #666; font-size: 0.85rem;">(${course.courseCode})</span>
                        </div>
                        <div class="course-stats">
                            <div class="course-stat">Avg: <span style="color: ${course.average >= 10 ? '#16a34a' : '#dc2626'}">${course.average} / 20</span></div>
                            <div class="course-stat">Rank: <span>#${course.rank}/${course.totalStudents}</span></div>
                            <div class="course-stat">Attendance: <span>${course.attendanceRate}%</span></div>
                        </div>
                    </div>
                    <div class="course-details">
                        <c:forEach var="entry" items="${course.exams}">
                            <div style="background: #f8fafc; padding: 0.5rem; border-radius: 4px; border: 1px solid #e2e8f0;">
                                <div style="font-weight: 500; margin-bottom: 0.2rem; display: flex; justify-content: space-between;">
                                    <span>${entry.key}</span>
                                    <span><strong>${entry.value.score}</strong> / ${entry.value.maxScore}</span>
                                </div>
                                <div style="font-size: 0.75rem; color: #64748b;">
                                    Score mapping: <c:choose><c:when test="${entry.key == 'Midterm' || entry.key == 'Final'}">40% weight</c:when><c:otherwise>Part of the 20% aggregate weight</c:otherwise></c:choose>
                                </div>
                            </div>
                        </c:forEach>
                        <c:if test="${empty course.exams}">
                            <div style="color: #64748b;">No graded exams yet.</div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty transcriptCourses}">
                <p style="text-align: center; color: #666; font-style: italic;">No course registrations found for this student.</p>
            </c:if>

            <div style="margin-top: 3rem; text-align: center; color: #999; font-size: 0.8rem; border-top: 1px solid #eee; padding-top: 1rem;">
                This transcript is generated automatically by the School Management System.
            </div>

        </div>
    </div>
</div>

<script>
    function generatePDF() {
        const element = document.getElementById('transcriptContent');
        const opt = {
            margin:       10,
            filename:     'Transcript_${childUser.firstName}_${childUser.lastName}.pdf',
            image:        { type: 'jpeg', quality: 0.98 },
            html2canvas:  { scale: 2 },
            jsPDF:        { unit: 'mm', format: 'a4', orientation: 'portrait' }
        };

        // Output to PDF
        html2pdf().set(opt).from(element).save();
    }
</script>
</body>
</html>
