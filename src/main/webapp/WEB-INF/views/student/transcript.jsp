<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>My Transcript - School Management</title>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.10.1/html2pdf.bundle.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/transcript.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/student_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Transcript Generator</div>
            <div style="flex-grow: 1;"></div>
            <button onclick="generatePDF('${sessionScope.user.firstName.replace('\'', '\\\'')}', '${sessionScope.user.lastName.replace('\'', '\\\'')}')" class="btn btn-sm" style="background: #ef4444; color: white; margin-right: 1rem;">📄 Export PDF</button>
            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-sm" style="background: rgba(255,255,255,0.1); margin-right: 1rem;">← Back</a>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem; background: white; color: #111;" id="transcriptContent">
            
            <div class="transcript-header">
                <h1 style="margin:0; font-size: 1.8rem;">Official Academic Transcript</h1>
                <p style="margin: 0.5rem 0 0; color: #555;">Student: <strong>${sessionScope.user.firstName} ${sessionScope.user.lastName}</strong></p>
                <p style="margin: 0; color: #555;">Student ID: ${student.studentNumber}</p>
                <p style="margin: 0; color: #555;">Generated on: <c:set var="now" value="<%=new java.util.Date()%>"/><fmt:formatDate value="${now}" pattern="dd/MM/yyyy"/></p>
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
                                    Score mapping: <c:choose><c:when test="${entry.key == 'MIDTERM' || entry.key == 'FINAL'}">40% weight</c:when><c:otherwise>Part of the 20% aggregate weight</c:otherwise></c:choose>
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

<script src="${pageContext.request.contextPath}/assets/js/student/transcript.js"></script>
</body>
</html>
