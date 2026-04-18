<%@ tag description="Smart Pagination" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="currentPage" required="true" type="java.lang.Integer" %>
<%@ attribute name="totalPages" required="true" type="java.lang.Integer" %>
<%@ attribute name="queryString" required="false" type="java.lang.String" %>

<c:if test="${totalPages > 1}">
<div class="pagination">
    <%-- Previous button --%>
    <c:if test="${currentPage > 1}">
        <a href="?page=${currentPage - 1}${queryString}" class="btn btn-sm btn-secondary">Previous</a>
    </c:if>

    <%-- Page 1 (always shown) --%>
    <a href="?page=1${queryString}" class="btn btn-sm ${currentPage == 1 ? 'btn-primary' : 'btn-secondary'}">1</a>

    <%-- Left ellipsis: gap between page 1 and neighborhood start --%>
    <c:if test="${currentPage - 1 > 2}">
        <span class="pagination-ellipsis">...</span>
    </c:if>

    <%-- Page before current (if > 1, i.e. not the first page which is already shown) --%>
    <c:if test="${currentPage - 1 > 1}">
        <a href="?page=${currentPage - 1}${queryString}" class="btn btn-sm btn-secondary">${currentPage - 1}</a>
    </c:if>

    <%-- Current page (if not first and not last, since those are always rendered) --%>
    <c:if test="${currentPage != 1 && currentPage != totalPages}">
        <a href="?page=${currentPage}${queryString}" class="btn btn-sm btn-primary">${currentPage}</a>
    </c:if>

    <%-- Page after current (if < totalPages, i.e. not the last page which is already shown) --%>
    <c:if test="${currentPage + 1 < totalPages}">
        <a href="?page=${currentPage + 1}${queryString}" class="btn btn-sm btn-secondary">${currentPage + 1}</a>
    </c:if>

    <%-- Right ellipsis: gap between neighborhood end and last page --%>
    <c:if test="${currentPage + 1 < totalPages - 1}">
        <span class="pagination-ellipsis">...</span>
    </c:if>

    <%-- Last page (always shown, if > 1) --%>
    <a href="?page=${totalPages}${queryString}" class="btn btn-sm ${currentPage == totalPages ? 'btn-primary' : 'btn-secondary'}">${totalPages}</a>

    <%-- Next button --%>
    <c:if test="${currentPage < totalPages}">
        <a href="?page=${currentPage + 1}${queryString}" class="btn btn-sm btn-secondary">Next</a>
    </c:if>
</div>
</c:if>
