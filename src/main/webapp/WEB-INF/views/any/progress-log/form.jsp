<%--
- form.jsp
-
- Copyright (C) 2012-2024 Rafael Corchuelo.
-
- In keeping with the traditional purpose of furthering education and research, it is
- the policy of the copyright owner to permit non-commercial use and redistribution of
- this software. It has been tested carefully, but it is not guaranteed for any particular
- purposes.  The copyright owner does not offer any warranties or representations, nor do
- they accept any liabilities with respect to them.
--%>

<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.progresslog.label.recordId" path="recordId" />
	<acme:input-double code="any.progresslog.label.completeness" path="completeness" />
	<acme:input-textbox code="any.progresslog.label.comment" path="comment" />
	<acme:input-textbox code="any.progresslog.label.responsiblePerson" path="responsiblePerson" />
	<acme:input-select code="any.progresslog.label.contract" path="contract" choices="${contracts}"/>
	
</acme:form>