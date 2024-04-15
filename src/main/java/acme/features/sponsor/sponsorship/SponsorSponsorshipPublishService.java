
package acme.features.sponsor.sponsorship;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.client.views.SelectChoices;
import acme.entities.projects.Project;
import acme.entities.sponsorships.Invoice;
import acme.entities.sponsorships.Sponsorship;
import acme.entities.sponsorships.SponsorshipType;
import acme.roles.Sponsor;

@Service
public class SponsorSponsorshipPublishService extends AbstractService<Sponsor, Sponsorship> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private SponsorSponsorshipRepository repository;

	// AbstractService<Auditor, CodeAudit> ---------------------------


	@Override
	public void authorise() {
		System.out.println("auth");
		boolean status;
		int id;
		Sponsor sponsor;
		Sponsorship sponsorship;

		id = super.getRequest().getData("id", int.class);
		sponsorship = this.repository.findOneSponsorshipById(id);

		sponsor = sponsorship == null ? null : sponsorship.getSponsor();
		status = sponsorship != null && !sponsorship.isPublished() && super.getRequest().getPrincipal().hasRole(sponsor);

		super.getResponse().setAuthorised(status);
		System.out.println(status);
	}

	@Override
	public void load() {
		System.out.println("load");
		Sponsorship object;
		int id;
		Date instantiationMoment;
		instantiationMoment = MomentHelper.getCurrentMoment();

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneSponsorshipById(id);
		object.setMoment(instantiationMoment);

		super.getBuffer().addData(object);
		System.out.println(object);
	}

	@Override
	public void bind(final Sponsorship object) {
		System.out.println("bind");
		assert object != null;
		super.bind(object, "published");
	}

	@Override
	public void validate(final Sponsorship object) {
		System.out.println("validate");
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("published")) {
			Double amount = object.getAmount().getAmount();
			Double total = 0.0;
			Collection<Invoice> invoices = this.repository.findAllInvoicesBySponsorshipId(object.getId());
			for (Invoice invoice : invoices)
				total += invoice.getValue().getAmount();

			System.out.println(total + " " + amount);
			super.state(amount == total, "published", "sponsor.sponsorship.form.error.amount");
		}

	}

	@Override
	public void perform(final Sponsorship object) {
		System.out.println("perfonm");
		assert object != null;
		object.setPublished(true);
		this.repository.save(object);
		System.out.println(object);
	}

	@Override
	public void unbind(final Sponsorship object) {
		System.out.println("unbind");
		assert object != null;
		Dataset dataset;
		SelectChoices choices;
		SelectChoices projects;

		Collection<Project> unpublishedProjects = this.repository.findAllUnpublishedProjects();
		projects = SelectChoices.from(unpublishedProjects, "code", object.getProject());

		choices = SelectChoices.from(SponsorshipType.class, object.getType());

		dataset = super.unbind(object, "code", "moment", "startDate", "endDate", "type", "amount", "email", "link", "published");
		dataset.put("types", choices);

		dataset.put("project", projects.getSelected().getKey());
		dataset.put("projects", projects);

		super.getResponse().addData(dataset);
		System.out.println(dataset);
	}

}