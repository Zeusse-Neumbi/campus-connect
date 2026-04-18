<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />
    <title>Child Attendance - School Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/pages/teacher-attendance.css">
</head>
<body>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/layout/parent_sidebar.jsp" />

    <div class="main-content">
        <div class="header glass-panel">
            <div class="page-title">Attendance — ${childUser.firstName} ${childUser.lastName}</div>
            <div style="flex-grow: 1;"></div>
            <a href="${pageContext.request.contextPath}/parent/children" class="btn btn-sm" style="background: rgba(255,255,255,0.1); margin-right: 1rem;">← Back</a>
            <div class="user-profile">
                <span><strong>${sessionScope.user.firstName}</strong></span>
                <div class="avatar" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9);">${sessionScope.user.firstName.substring(0,1)}</div>
            </div>
        </div>

        <div class="glass-panel" style="padding: 2rem;">
            <h3>📋 ${childUser.firstName}'s Attendance Record</h3>
            <div class="table-container" style="overflow-x: auto;">
                <table>
                    <thead>
                        <tr>
                            <th style="position: sticky; left: 0; background: rgba(255,255,255,0.95); z-index: 2;">Course</th>
                            <c:forEach var="date" items="${allSessionDates}">
                                <th style="text-align: center; font-size: 0.72rem; min-width: 45px; padding: 0.5rem 0.3rem;">
                                    ${date}
                                </th>
                            </c:forEach>
                            <th style="text-align: center;">Rate</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="row" items="${attendanceMatrix}">
                            <tr>
                                <td style="position: sticky; left: 0; background: rgba(255,255,255,0.95); z-index: 1; white-space: nowrap;">
                                    <strong>${row.course.courseName}</strong>
                                    <br><span style="color: var(--text-muted); font-size: 0.78rem;">${row.course.courseCode}</span>
                                </td>
                                <c:forEach var="date" items="${allSessionDates}">
                                    <c:set var="st" value="${row.dateStatuses[date]}" />
                                    <td style="text-align: center; padding: 0.4rem;">
                                        <c:choose>
                                            <c:when test="${st eq 'Present' || st eq 'PRESENT' || st eq 'present'}"><span class="att-status present" title="Present"></span></c:when>
                                            <c:when test="${st eq 'Absent' || st eq 'ABSENT' || st eq 'absent'}"><span class="att-status absent" title="Absent"></span></c:when>
                                            <c:when test="${st eq 'Late' || st eq 'LATE' || st eq 'late'}"><span class="att-status late" title="Late"></span></c:when>
                                            <c:when test="${empty st || st eq 'NONE'}"><span class="att-status none" title="Not available / Not recorded"></span></c:when>
                                            <c:otherwise><span class="att-status none" title="Not recorded"></span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </c:forEach>
                                <td style="text-align: center;">
                                    <c:choose>
                                        <c:when test="${row.rate >= 80}">
                                            <span class="att-rate good"><fmt:formatNumber value="${row.rate}" maxFractionDigits="0"/>%</span>
                                        </c:when>
                                        <c:when test="${row.rate >= 50}">
                                            <span class="att-rate warn"><fmt:formatNumber value="${row.rate}" maxFractionDigits="0"/>%</span>
                                        </c:when>
                                        <c:when test="${row.rate >= 0}">
                                            <span class="att-rate bad"><fmt:formatNumber value="${row.rate}" maxFractionDigits="0"/>%</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: var(--text-muted); font-size: 0.8rem;">—</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty attendanceMatrix}">
                            <tr><td colspan="${allSessionDates.size() + 2}" style="text-align:center;">No enrolled courses found.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%-- Legend --%>
            <div class="att-legend" style="margin-top: 1.5rem;">
                <div class="att-legend-item"><span class="att-status present"></span> Present</div>
                <div class="att-legend-item"><span class="att-status late"></span> Late</div>
                <div class="att-legend-item"><span class="att-status absent"></span> Absent</div>
                <div class="att-legend-item"><span class="att-status none"></span> No Session / Not recorded</div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
