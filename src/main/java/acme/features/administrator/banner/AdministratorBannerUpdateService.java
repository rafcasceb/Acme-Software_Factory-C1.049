
package acme.features.administrator.banner;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Administrator;
import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.SpamDetector;
import acme.entities.banners.Banner;
import acme.entities.configuration.Configuration;

@Service
public class AdministratorBannerUpdateService extends AbstractService<Administrator, Banner> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBannerRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Banner object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneBannerById(id);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Banner object) {
		assert object != null;

		super.bind(object, "displayStartMoment", "displayEndMoment", "picture", "slogan", "target");
	}

	@Override
	public void validate(final Banner object) {
		assert object != null;

		String dateString = "2201/01/01 00:00";
		Date futureMostDate = MomentHelper.parse(dateString, "yyyy/MM/dd HH:mm");

		if (object.getDisplayStartMoment() != null) {

			if (!super.getBuffer().getErrors().hasErrors("displayStartMoment"))
				super.state(MomentHelper.isAfter(object.getDisplayStartMoment(), object.getMoment()), "displayStartMoment", "administrator.banner.form.error.startDate");

			if (!super.getBuffer().getErrors().hasErrors("displayStartMoment"))
				super.state(MomentHelper.isBefore(object.getDisplayStartMoment(), futureMostDate), "displayStartMoment", "administrator.banner.form.error.dateOutOfBounds");

			if (object.getDisplayStartMoment() != null) {

				if (!super.getBuffer().getErrors().hasErrors("displayEndMoment"))
					super.state(MomentHelper.isLongEnough(object.getDisplayStartMoment(), object.getDisplayEndMoment(), 1, ChronoUnit.WEEKS), "displayEndMoment", "administrator.banner.form.error.period");

				if (!super.getBuffer().getErrors().hasErrors("displayEndMoment"))
					super.state(MomentHelper.isAfter(object.getDisplayEndMoment(), object.getDisplayStartMoment()), "displayEndMoment", "administrator.banner.form.error.startDateAfterEndDate");
			}
		}

		if (object.getDisplayStartMoment() != null) {

			if (!super.getBuffer().getErrors().hasErrors("displayEndMoment"))
				super.state(MomentHelper.isAfter(object.getDisplayEndMoment(), object.getMoment()), "displayEndMoment", "administrator.banner.form.error.endDate");

			if (!super.getBuffer().getErrors().hasErrors("displayEndMoment"))
				super.state(MomentHelper.isBefore(object.getDisplayEndMoment(), futureMostDate), "displayEndMoment", "administrator.banner.form.error.dateOutOfBounds");
		}

		if (!super.getBuffer().getErrors().hasErrors("slogan")) {
			Configuration config = this.repository.findConfiguration();
			String spamTerms = config.getSpamTerms();
			Double spamThreshold = config.getSpamThreshold();
			SpamDetector spamHelper = new SpamDetector(spamTerms, spamThreshold);
			super.state(!spamHelper.isSpam(object.getSlogan()), "slogan", "administrator.banner.form.error.spam");
		}

	}

	@Override
	public void perform(final Banner object) {
		assert object != null;

		Date moment;
		moment = MomentHelper.getCurrentMoment();
		object.setMoment(moment);

		this.repository.save(object);
	}

	@Override
	public void unbind(final Banner object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbind(object, "moment", "displayStartMoment", "displayEndMoment", "picture", "slogan", "target");

		super.getResponse().addData(dataset);
	}

}
