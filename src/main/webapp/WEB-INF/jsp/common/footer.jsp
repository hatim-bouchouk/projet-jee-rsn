    <!-- Main content ends here -->
    </div>
    
    <footer class="footer bg-light mt-5 py-3">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <p class="mb-0">&copy; <%= java.time.Year.now().getValue() %> Supply Chain Management System</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <p class="mb-0">Version 1.0.0</p>
                </div>
            </div>
        </div>
    </footer>
    
    <!-- Bootstrap Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.3.0/dist/chart.umd.min.js"></script>
    
    <!-- Common JavaScript -->
    <script src="${pageContext.request.contextPath}/assets/js/common.js"></script>
    
    <!-- Page-specific JavaScript -->
    <c:if test="${not empty param.extraScripts}">
        <jsp:include page="${param.extraScripts}" />
    </c:if>
</body>
</html> 